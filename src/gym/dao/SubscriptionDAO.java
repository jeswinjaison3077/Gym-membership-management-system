package gym.dao;

import gym.model.Subscription;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDAO {

    public int addSubscription(int memberId, int planId, Date startDate, Date endDate) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            return addSubscription(con, memberId, planId, startDate, endDate);
        }
    }

    public int addSubscription(Connection con, int memberId, int planId, Date startDate, Date endDate) throws SQLException {
        String sql = "INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status) VALUES (?,?,?,?,'ACTIVE')";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            ps.setInt(2, planId);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateStatus(int subscriptionId, String status) throws SQLException {
        String sql = "UPDATE subscriptions SET status=? WHERE subscription_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, subscriptionId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Subscription> getSubscriptionsByMember(int memberId) throws SQLException {
        List<Subscription> list = new ArrayList<>();
        String sql = "SELECT s.*, p.plan_name FROM subscriptions s " +
                     "JOIN plans p ON s.plan_id = p.plan_id WHERE s.member_id=? ORDER BY s.subscription_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Subscription> getExpiringSoon(int daysAhead) throws SQLException {
        List<Subscription> list = new ArrayList<>();
        String sql = "SELECT s.*, p.plan_name FROM subscriptions s " +
                     "JOIN plans p ON s.plan_id = p.plan_id " +
                     "WHERE s.status='ACTIVE' AND s.end_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, daysAhead);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Subscription mapRow(ResultSet rs) throws SQLException {
        return new Subscription(
                rs.getInt("subscription_id"),
                rs.getInt("member_id"),
                rs.getInt("plan_id"),
                rs.getString("plan_name"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getString("status")
        );
    }
}
