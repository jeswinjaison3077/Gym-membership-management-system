package gym.dao;

import gym.model.WorkoutPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutPlanDAO {

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
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean deleteWorkoutPlan(int workoutId) throws SQLException {
        String sql = "DELETE FROM workout_plans WHERE workout_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, workoutId);
            return ps.executeUpdate() > 0;
        }
    }

    private WorkoutPlan mapRow(ResultSet rs) throws SQLException {
        return new WorkoutPlan(
                rs.getInt("workout_id"),
                rs.getInt("member_id"),
                rs.getInt("trainer_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("days_per_week"),
                rs.getDate("created_date").toLocalDate()
        );
    }
}
