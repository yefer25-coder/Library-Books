package dao.impl;

import config.DatabaseConnection;
import dao.MemberDAO;
import exceptions.DuplicateEmailException;
import models.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of MemberDAO
 */
public class MemberDAOImpl implements MemberDAO {

    private final DatabaseConnection dbConnection;

    public MemberDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Member create(Member member) throws SQLException, DuplicateEmailException {
        if (existsByEmail(member.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + member.getEmail());
        }

        String sql = "INSERT INTO members (name, email, phone, address, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.setString(4, member.getAddress());
            stmt.setBoolean(5, member.getIsActive());
            stmt.setTimestamp(6, Timestamp.valueOf(member.getCreatedAt()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        member.setIdMember(rs.getInt(1));
                    }
                }
            }

            return member;
        }
    }

    @Override
    public Optional<Member> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM members WHERE id_member = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Member> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM members WHERE email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Member> findAll() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        }

        return members;
    }

    @Override
    public List<Member> findAllActive() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE is_active = TRUE ORDER BY name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        }

        return members;
    }

    @Override
    public boolean update(Member member) throws SQLException, DuplicateEmailException {
        // Check if email is taken by another member
        Optional<Member> existing = findByEmail(member.getEmail());
        if (existing.isPresent() && !existing.get().getIdMember().equals(member.getIdMember())) {
            throw new DuplicateEmailException("Email already exists: " + member.getEmail());
        }

        String sql = "UPDATE members SET name = ?, email = ?, phone = ?, address = ?, is_active = ? WHERE id_member = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.setString(4, member.getAddress());
            stmt.setBoolean(5, member.getIsActive());
            stmt.setInt(6, member.getIdMember());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM members WHERE id_member = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean activate(Integer id) throws SQLException {
        String sql = "UPDATE members SET is_active = TRUE WHERE id_member = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deactivate(Integer id) throws SQLException {
        String sql = "UPDATE members SET is_active = FALSE WHERE id_member = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM members WHERE email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Map ResultSet to Member object
     */
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getInt("id_member"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getBoolean("is_active"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}