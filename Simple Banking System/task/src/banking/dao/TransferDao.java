package banking.dao;

import banking.Bank;
import banking.Card;
import banking.CardProcessingException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class TransferDao {
    private final String from;
    private final String to;
    private final int amount;
    private final Bank bank;

    public TransferDao(String from, String to, int amount, Bank bank) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.bank = bank;
    }

    public void transfer(){
        try (Connection connection = bank.getDbConnection().connectionWithTx()) {
            CardDao cardDao = new CardDao(connection);
            Optional<Card> cardFrom = cardDao.get(from);
            Optional<Card> cardTo = cardDao.get(to);
            if (cardFrom.get().getBalance() < amount) throw new CardProcessingException("Not enough money!\n");
            cardFrom.get().setBalance(cardFrom.get().getBalance() - amount);
            cardDao.save(cardFrom.get());
            cardTo.get().setBalance(cardTo.get().getBalance() + amount);
            cardDao.save(cardTo.get());
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
