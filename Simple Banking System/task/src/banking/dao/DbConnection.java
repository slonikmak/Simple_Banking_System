package banking.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private String url;

    public DbConnection(String url) {
        this.url = url;
    }

    public Connection connection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public Connection connectionWithTx() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
