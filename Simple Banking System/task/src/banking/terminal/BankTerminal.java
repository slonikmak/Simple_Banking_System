package banking.terminal;

import banking.Bank;
import banking.Card;
import banking.CardProcessingException;
import banking.dao.CardDao;
import banking.dao.TransferDao;

import javax.xml.transform.Source;
import java.sql.SQLException;
import java.util.*;

public class BankTerminal {

    private static final String createAccountAnswer = "\nYour card has been created\n" +
            "Your card number:\n" +
            "%s\n" +
            "Your card PIN:\n" +
            "%s\n";


    private final List<MenuItem> mainMenu = new ArrayList<>();
    private final Bank bank;

    private Scanner sc;

    private List<MenuItem> actualMenu;

    public BankTerminal(Bank bank) {
        this.bank = bank;

    }

    public void start(){
        generateMenu();
        actualMenu = mainMenu;
        printMenu();

        sc = new Scanner(System.in);

        while (true) {
            int answer = Integer.parseInt(sc.nextLine());
            processAnswer(answer);
            printMenu();
        }
    }

    private void processAnswer(int answer) {
        actualMenu.stream().filter(menuItem -> menuItem.getNum() == answer).findFirst().ifPresent(MenuItem::performAction);
    }

    private void generateMenu(){
        Action exitAction = new ExitAction();

        mainMenu.add(new MenuItem(1, "Create an account", this::createAnAccount));
        mainMenu.add(new MenuItem(2, "Log into account", this::logIntoAccount));
        mainMenu.add(new MenuItem(0, "Exit", exitAction));
        mainMenu.add(new MenuItem(3, "Get all", this::getAll));
        mainMenu.add(new MenuItem(4, "Verify", this::verify));
    }

    private void verify() {
        System.out.println("Enter card number");
        String number = sc.nextLine();
        bank.verifyCheckSum(number);
    }

    private List<MenuItem> balanceMenu(String cardNumber) {
        List<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem(1, "Balance", printBalance(cardNumber)));
        list.add(new MenuItem(2, "Add income", addIncome(cardNumber)));
        list.add(new MenuItem(3, "Do transfer", transfer(cardNumber)));
        list.add(new MenuItem(4, "Close account", closeAccount(cardNumber)));
        list.add(new MenuItem(5, "Log out", this::logOut));
        list.add(new MenuItem(0, "Exit", new ExitAction()));
        return list;
    }

    private void printMenu(){
        actualMenu.forEach(menuItem-> System.out.println(menuItem.getNum()+". "+menuItem.getName()));
    }

    private void createAnAccount() {
        Card card = bank.randomCard();
        System.out.printf((createAccountAnswer) + "%n", card, card.getPin());
    }

    private void logIntoAccount() {
        System.out.println("\nEnter your card number:");
        String cardNumber = sc.nextLine();
        System.out.println("Enter your PIN:");
        String pin = sc.nextLine();
        if (bank.verifyPin(cardNumber, pin)) {
            System.out.println("\nYou have successfully logged in!");
            actualMenu = balanceMenu(cardNumber);
        } else {
            System.out.println("\nWrong card number or PIN!");
        }
    }

    private Action printBalance(String cardNumber){
        return () -> System.out.println("\nBalance: "+bank.getBalance(cardNumber));
    }

    private Action addIncome(String cardNumber){
        return () -> {
            System.out.println("Enter income: ");
            int income = Integer.parseInt(sc.nextLine());
            bank.addIncome(cardNumber, income);
            System.out.println("Income was added!");
        };
    }

    private Action transfer(String cardNumber) {
        return () -> {
            try {
                System.out.println("Enter card number:");
                String toCardNumber = sc.nextLine();
                if (!bank.verifyCheckSum(toCardNumber)) throw new CardProcessingException("Probably you made a mistake in the card number. Please try again!\n");
                if (!bank.verifyExists(toCardNumber)) throw new CardProcessingException("Such a card does not exist.\n");
                System.out.println("Enter how much money you want to transfer:");
                int amount = Integer.parseInt(sc.nextLine());
                bank.transfer(cardNumber, toCardNumber, amount);
                System.out.println("Success!");
            } catch (CardProcessingException cardProcessingException) {
                System.out.println(cardProcessingException.getMessage());
            }
        };
    }

    private Action closeAccount(String cardNumber) {
        return () -> {
            bank.closeAccount(cardNumber);
            System.out.println("The account has been closed!");
        };
    }

    private void logOut() {
        System.out.println("\nYou have successfully logged out!");
        actualMenu = mainMenu;
    }

    private void getAll() {
        System.out.println("All cards:");
        List<Card> cards = bank.getAll();
        cards.forEach(c-> System.out.printf("%s %s %s%n",c.toString(), c.getPin(), c.getBalance()));
    }
}
