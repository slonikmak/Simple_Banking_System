package banking;

import banking.dao.DbConnection;
import banking.terminal.BankTerminal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        DbConnection dbConnection;

        if (args.length > 1) {
            String name = args[1];
            dbConnection = new DbConnection("jdbc:sqlite:"+name);
        } else {
            dbConnection = new DbConnection("jdbc:sqlite:tests.db");
        }

        initTable(dbConnection.connection());

        Bank bank = new Bank(dbConnection);
        BankTerminal bankTerminal = new BankTerminal(bank);
        bankTerminal.start();
    }

    private static void initTable(Connection connection) {

        String createTable = "CREATE TABLE IF NOT EXISTS card (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	number text NOT NULL,\n"
                + "	pin text NOT NULL,\n"
                + "	balance INTEGER DEFAULT 0\n"
                + ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}