package gym.dao;

import gym.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * [CONCEPT: JDBC / DATABASE CONNECTIVITY & COLLECTIONS]
 * Data Access Object (DAO) consolidating all MySQL operations.
 * 
 * Demonstrates:
 * 1. JDBC Connections via DBConnection.getConnection()
 * 2. PreparedStatement for SQL Injection protection
 * 3. ResultSet mapping to Java Model Objects
 * 4. Java Collections Framework (ArrayList, List)
 * ============================================================================
 */
public class GymDAO {

    // =========================================================================
    // MEMBER OPERATIONS
    // =========================================================================

    public int addMember(Member m) throws SQLException {
        String sql = "INSERT INTO members (name, phone, email, age, gender, join_date, trainer_id) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getPhone());
            ps.setString(3, m.getEmail());
            ps.setInt(4, m.getAge());
            ps.setString(5, m.getGender());
            ps.setDate(6, Date.valueOf(m.getJoinDate()));
            if (m.getTrainerId() != null) {
                ps.setInt(7, m.getTrainerId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateMember(Member m) throws SQLException {
        String sql = "UPDATE members SET name=?, phone=?, email=?, age=?, gender=?, trainer_id=? WHERE member_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getPhone());
            ps.setString(3, m.getEmail());
            ps.setInt(4, m.getAge());
            ps.setString(5, m.getGender());
            if (m.getTrainerId() != null) {
                ps.setInt(6, m.getTrainerId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, m.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteMember(int memberId) throws SQLException {
        String sql = "DELETE FROM members WHERE member_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        }
    }

    public Member getMemberById(int memberId) throws SQLException {
        String sql = "SELECT m.*, t.name AS trainer_name FROM members m " +
                     "LEFT JOIN trainers t ON m.trainer_id = t.trainer_id WHERE m.member_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapMemberRow(rs);
            }
        }
        return null;
    }

    public List<Member> getAllMembers() throws SQLException {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT m.*, t.name AS trainer_name FROM members m " +
                     "LEFT JOIN trainers t ON m.trainer_id = t.trainer_id ORDER BY m.member_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapMemberRow(rs));
            }
        }
        return list;
    }

    public List<Member> searchMembers(String keyword) throws SQLException {
        List<Member> list = new ArrayList<>();
        String trimmed = keyword == null ? "" : keyword.trim();
        if (trimmed.isEmpty()) {
            return getAllMembers();
        }

        try {
            int id = Integer.parseInt(trimmed);
            String sql = "SELECT m.*, t.name AS trainer_name FROM members m " +
                         "LEFT JOIN trainers t ON m.trainer_id = t.trainer_id WHERE m.member_id = ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapMemberRow(rs));
                }
            }
            return list;
        } catch (NumberFormatException e) {
            String sql = "SELECT m.*, t.name AS trainer_name FROM members m " +
                         "LEFT JOIN trainers t ON m.trainer_id = t.trainer_id WHERE m.name LIKE ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, "%" + trimmed + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapMemberRow(rs));
                }
            }
            return list;
        }
    }

    private Member mapMemberRow(ResultSet rs) throws SQLException {
        int trainerId = rs.getObject("trainer_id") != null ? rs.getInt("trainer_id") : -1;
        Member m = new Member(
                rs.getInt("member_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getInt("age"),
                rs.getString("gender"),
                rs.getDate("join_date").toLocalDate(),
                trainerId == -1 ? null : trainerId
        );
        m.setTrainerName(rs.getString("trainer_name"));
        return m;
    }

    // =========================================================================
    // TRAINER OPERATIONS
    // =========================================================================

    public int addTrainer(Trainer t) throws SQLException {
        String sql = "INSERT INTO trainers (name, phone, email, specialization, experience_yrs) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getPhone());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getSpecialization());
            ps.setInt(5, t.getExperienceYears());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean deleteTrainer(int trainerId) throws SQLException {
        String sql = "DELETE FROM trainers WHERE trainer_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Trainer> getAllTrainers() throws SQLException {
        List<Trainer> list = new ArrayList<>();
        String sql = "SELECT * FROM trainers ORDER BY trainer_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Trainer(
                        rs.getInt("trainer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("specialization"),
                        rs.getInt("experience_yrs")
                ));
            }
        }
        return list;
    }

    // =========================================================================
    // PLAN & SUBSCRIPTION OPERATIONS
    // =========================================================================

    public List<Plan> getAllPlans() throws SQLException {
        List<Plan> list = new ArrayList<>();
        String sql = "SELECT * FROM plans ORDER BY plan_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Plan(
                        rs.getInt("plan_id"),
                        rs.getString("plan_name"),
                        rs.getInt("duration_months"),
                        rs.getDouble("price")
                ));
            }
        }
        return list;
    }

    public Plan getPlanById(int planId) throws SQLException {
        String sql = "SELECT * FROM plans WHERE plan_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, planId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Plan(
                            rs.getInt("plan_id"),
                            rs.getString("plan_name"),
                            rs.getInt("duration_months"),
                            rs.getDouble("price")
                    );
                }
            }
        }
        return null;
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

    public boolean updateSubscriptionStatus(int subscriptionId, String status) throws SQLException {
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
                while (rs.next()) {
                    list.add(mapSubscriptionRow(rs));
                }
            }
        }
        return list;
    }

    public List<Subscription> getExpiringSubscriptions(int daysAhead) throws SQLException {
        List<Subscription> list = new ArrayList<>();
        String sql = "SELECT s.*, p.plan_name FROM subscriptions s " +
                     "JOIN plans p ON s.plan_id = p.plan_id " +
                     "WHERE s.status='ACTIVE' AND s.end_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, daysAhead);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSubscriptionRow(rs));
                }
            }
        }
        return list;
    }

    private Subscription mapSubscriptionRow(ResultSet rs) throws SQLException {
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

    // =========================================================================
    // WORKOUT PLAN OPERATIONS
    // =========================================================================

    public int addWorkoutPlan(WorkoutPlan w) throws SQLException {
        String sql = "INSERT INTO workout_plans (member_id, trainer_id, title, description, days_per_week, created_date) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, w.getMemberId());
            ps.setInt(2, w.getTrainerId());
            ps.setString(3, w.getTitle());
            ps.setString(4, w.getDescription());
            ps.setInt(5, w.getDaysPerWeek());
            ps.setDate(6, Date.valueOf(w.getCreatedDate()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<WorkoutPlan> getWorkoutPlansByMember(int memberId) throws SQLException {
        List<WorkoutPlan> list = new ArrayList<>();
        String sql = "SELECT * FROM workout_plans WHERE member_id=? ORDER BY workout_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new WorkoutPlan(
                            rs.getInt("workout_id"),
                            rs.getInt("member_id"),
                            rs.getInt("trainer_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("days_per_week"),
                            rs.getDate("created_date").toLocalDate()
                    ));
                }
            }
        }
        return list;
    }

    // =========================================================================
    // PAYMENT OPERATIONS
    // =========================================================================

    public int addPayment(Payment p) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            return addPayment(con, p);
        }
    }

    public int addPayment(Connection con, Payment p) throws SQLException {
        String sql = "INSERT INTO payments (member_id, subscription_id, amount, payment_date, payment_mode, status) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getMemberId());
            if (p.getSubscriptionId() != null) {
                ps.setInt(2, p.getSubscriptionId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
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
                while (rs.next()) list.add(mapPaymentRow(rs));
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
            while (rs.next()) list.add(mapPaymentRow(rs));
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

    private Payment mapPaymentRow(ResultSet rs) throws SQLException {
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
