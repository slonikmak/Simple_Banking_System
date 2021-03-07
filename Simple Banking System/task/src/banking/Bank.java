package banking;

import banking.dao.CardDao;
import banking.dao.DbConnection;
import banking.dao.TransferDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bank {

    private static String DEFAULT_BIN = "400000";
    private final DbConnection dbConnection;

    public Bank(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public DbConnection getDbConnection() {
        return dbConnection;
    }

    public Card defaultCard(String accountId) {
        try (Connection con = dbConnection.connection()){
            Card card =  new Card(DEFAULT_BIN, accountId, calcCheckSum(DEFAULT_BIN, accountId));
            card.setPin(pin());
            CardDao cardDao = new CardDao(con);
            cardDao.save(card);
            return card;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Card randomCard() {
        String accountId = byteArrayToString(new int[9]);
        return defaultCard(accountId);
    }

    public String pin(){
        return byteArrayToString(new int[4]);
    }

    public boolean verifyPin(String cardNumber, String pin) {
        try (Connection con = dbConnection.connection()){
            CardDao cardDao = new CardDao(con);
            Optional<Card> result = cardDao.get(cardNumber);
            return result.map(card -> card.getPin().equals(pin)).orElse(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public int getBalance(String cardNumber) {
        try (Connection con = dbConnection.connection()){
            CardDao cardDao = new CardDao(dbConnection.connection());
            return cardDao.get(cardNumber).map(Card::getBalance).orElse(0);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public void addIncome(String cardNumber, int income) {
        try (Connection con = dbConnection.connectionWithTx()){
            CardDao cardDao = new CardDao(con);
            cardDao.get(cardNumber).ifPresent(card -> {
                card.setBalance(card.getBalance() + income);
                cardDao.update(card);
                try {
                    con.commit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void transfer(String fromCardNumber, String toCardNumber, int amount) {
        TransferDao transferDao = new TransferDao(fromCardNumber, toCardNumber, amount, this);
        transferDao.transfer();
    }

    public boolean verifyExists(String cardNumber) {
        try (Connection con = dbConnection.connection()){
            CardDao cardDao = new CardDao(dbConnection.connection());
            return cardDao.get(cardNumber).isPresent();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void closeAccount(String number) {
        try (Connection connection = dbConnection.connection()) {
            CardDao cardDao = new CardDao(connection);
            cardDao.delete(number);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Card> getAll() {
        try (Connection connection = dbConnection.connection()) {
            CardDao cardDao = new CardDao(connection);
            return cardDao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean verifyCheckSum(String number) {
        //if (!number.startsWith("4")) return false;
        String bin = number.substring(0, 6);
        String accountId = number.substring(6, 15);
        String checkSum = number.substring(15, 16);
        String calculatedCheckSum = calcCheckSum(bin, accountId);
        return checkSum.equals(calculatedCheckSum);
    }

    private String calcCheckSum(String bin, String accountId) {
        int[] numbers = new int[15];
        char[] chars = (bin+accountId).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            numbers[i] =  Integer.parseInt(String.valueOf(chars[i]));
        }
        int sum = IntStream.range(0, numbers.length).map(i -> {
            if (i % 2 == 0) return numbers[i] * 2;
            else return numbers[i];
        }).map(i -> {
            if (i > 9) return i - 9;
            else return i;
        }).sum();
        char[] sumChars = String.valueOf(sum).toCharArray();
        int lastDigit = Integer.parseInt(String.valueOf(sumChars[sumChars.length-1]));

        return String.valueOf(lastDigit == 0 ? 0 : 10 - lastDigit);
    }

    private String byteArrayToString(int[] array) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(9);
        }
        return IntStream.of(array).mapToObj(String::valueOf).collect(Collectors.joining());
    }
}
