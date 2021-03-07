package banking.terminal;

public class MenuItem {

    private final int num;
    private final String name;
    private final Action action;

    public MenuItem(int num, String name, Action action) {
        this.num = num;
        this.name = name;
        this.action = action;
    }

    public void performAction(){
        action.run();
    }

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }
}
