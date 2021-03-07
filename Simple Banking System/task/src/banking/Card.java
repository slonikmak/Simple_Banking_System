package banking;

public class Card {

    private String bin;
    private String accountId;
    private String checkSum;
    private String pin;
    private int balance;

    public Card(String bin, String accountId, String checkSum) {
        this.bin = bin;
        this.accountId = accountId;
        this.checkSum = checkSum;
    }

    public Card(String number) {
        this.bin = number.substring(0, 6);
        this.accountId = number.substring(6, 16);
        this.checkSum = number.substring(16);
    }

    public void setPin(String pin){
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return bin + accountId + checkSum;
    }

}
