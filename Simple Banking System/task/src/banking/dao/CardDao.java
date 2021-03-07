package banking.dao;

import banking.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardDao {

    private Connection connection;

    public CardDao(Connection connection) {
        this.connection = connection;
    }

    public void save(Card card) {
        get(card.toString()).ifPresentOrElse(existed -> update(card), ()->{
            String sql = "INSERT INTO card(number, pin) VALUES(?,?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, card.toString());
                pstmt.setString(2, card.getPin());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Optional<Card> get(String number) {
        String sql = "SELECT pin, balance FROM card WHERE number=?";

        List<Card> cards = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, number);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Card card = new Card(number);
                card.setPin(rs.getString("pin"));
                card.setBalance(rs.getInt("balance"));
                cards.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards.isEmpty() ? Optional.empty() : Optional.of(cards.get(0));
    }

    public void update(Card card) {
        String sql = "update card set balance=? where number=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, card.getBalance());
            pstmt.setString(2, card.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String cardNumber) {
        String sql = "DELETE FROM card WHERE number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, cardNumber);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Card> getAll() {
        String sql = "select * from card";
        List<Card> cards = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                Card card = new Card(resultSet.getString("number"));
                card.setPin(resultSet.getString("pin"));
                card.setBalance(resultSet.getInt("balance"));
                cards.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

}
