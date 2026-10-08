package gym.dao;

import gym.model.Plan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanDAO {

    public List<Plan> getAllPlans() throws SQLException {
        List<Plan> list = new ArrayList<>();
        String sql = "SELECT * FROM plans ORDER BY plan_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Plan(rs.getInt("plan_id"), rs.getString("plan_name"),
                        rs.getInt("duration_months"), rs.getDouble("price")));
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
                    return new Plan(rs.getInt("plan_id"), rs.getString("plan_name"),
                            rs.getInt("duration_months"), rs.getDouble("price"));
                }
            }
        }
        return null;
    }
}
