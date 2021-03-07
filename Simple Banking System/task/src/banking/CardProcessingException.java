package banking;

public class CardProcessingException extends RuntimeException {
    public CardProcessingException(String msg) {
        super(msg);
    }
}
