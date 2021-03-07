package banking.dao;

import banking.Card;

import java.sql.*;
import java.util.Optional;

public class Repository {

    private String url = "jdbc:sqlite:tests.db";

    String createTable = "CREATE TABLE IF NOT EXISTS card (\n"
            + "	id integer PRIMARY KEY,\n"
            + "	number text NOT NULL,\n"
            + "	pin text NOT NULL,\n"
            + "	balance INTEGER DEFAULT 0\n"
            + ");";

    public Repository() {

    }

    public Repository(String url) {
        this.url = url;
    }

    public void init() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(createTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(String number, String pin) {
        String sql = "INSERT INTO card(number, pin) VALUES(?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String number, int balance) {
        String sql = "update card set balance=? where number=?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, balance);
            pstmt.setString(2, number);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Card> get(String number) {
        String sql = "SELECT pin, balance FROM card WHERE number=?";

        Card card = null;
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                card = new Card(number);
                card.setPin(rs.getString("pin"));
                card.setBalance(rs.getInt("balance"));
            }
            return Optional.ofNullable(card);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(card);
    }

    public void delete(String number) {
        String sql = "DELETE FROM card WHERE number = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, number);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
