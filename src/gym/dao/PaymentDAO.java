package gym.dao;

import gym.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public int addPayment(Payment p) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            return addPayment(con, p);
        }
    }

    public int addPayment(Connection con, Payment p) throws SQLException {
        String sql = "INSERT INTO payments (member_id, subscription_id, amount, payment_date, payment_mode, status) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getMemberId());
            if (p.getSubscriptionId() != null) ps.setInt(2, p.getSubscriptionId());
            else ps.setNull(2, Types.INTEGER);
            ps.setDouble(3, p.getAmount());
            ps.setDate(4, Date.valueOf(p.getPaymentDate()));
            ps.setString(5, p.getPaymentMode());
            ps.setString(6, p.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<Payment> getPaymentsByMember(int memberId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE member_id=? ORDER BY payment_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Payment> getAllDuePayments() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE status='DUE' ORDER BY payment_date";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(amount) AS total FROM payments WHERE status='PAID'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("total");
        }
        return 0.0;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        int subId = rs.getObject("subscription_id") != null ? rs.getInt("subscription_id") : -1;
        return new Payment(
                rs.getInt("payment_id"),
                rs.getInt("member_id"),
                subId == -1 ? null : subId,
                rs.getDouble("amount"),
                rs.getDate("payment_date").toLocalDate(),
                rs.getString("payment_mode"),
                rs.getString("status")
        );
    }
}
