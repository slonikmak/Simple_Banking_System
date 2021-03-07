package banking.terminal;

public class ExitAction implements Action {

    @Override
    public void run() {
        System.out.println("\nBye!");
        System.exit(1);
    }
}
