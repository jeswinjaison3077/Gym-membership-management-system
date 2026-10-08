package gym.dao;

import gym.model.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the members table.
 * All SQL for Member is isolated here so the service/UI layer never
 * writes raw SQL -> clean separation of concerns.
 */
public class MemberDAO {

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
            if (m.getTrainerId() != null) ps.setInt(7, m.getTrainerId());
            else ps.setNull(7, Types.INTEGER);

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
            if (m.getTrainerId() != null) ps.setInt(6, m.getTrainerId());
            else ps.setNull(6, Types.INTEGER);
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
                if (rs.next()) return mapRow(rs);
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
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Member> searchByNameOrId(String keyword) throws SQLException {
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
                    while (rs.next()) list.add(mapRow(rs));
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
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
            return list;
        }
    }

    // Helper: maps one row of the ResultSet into a Member object
    private Member mapRow(ResultSet rs) throws SQLException {
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
}
