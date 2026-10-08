package gym.dao;

import gym.model.Trainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerDAO {

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

    public boolean updateTrainer(Trainer t) throws SQLException {
        String sql = "UPDATE trainers SET name=?, phone=?, email=?, specialization=?, experience_yrs=? WHERE trainer_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getPhone());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getSpecialization());
            ps.setInt(5, t.getExperienceYears());
            ps.setInt(6, t.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteTrainer(int trainerId) throws SQLException {
        String sql = "DELETE FROM trainers WHERE trainer_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            return ps.executeUpdate() > 0;
        }
    }

    public Trainer getTrainerById(int trainerId) throws SQLException {
        String sql = "SELECT * FROM trainers WHERE trainer_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Trainer> getAllTrainers() throws SQLException {
        List<Trainer> list = new ArrayList<>();
        String sql = "SELECT * FROM trainers ORDER BY trainer_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Trainer mapRow(ResultSet rs) throws SQLException {
        return new Trainer(
                rs.getInt("trainer_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("specialization"),
                rs.getInt("experience_yrs")
        );
    }
}
