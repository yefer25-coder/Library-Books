package dao.impl;

import config.DatabaseConnection;
import dao.UserDAO;
import exceptions.DuplicateUsernameException;
import models.User;
import models.User.UserRole;
import models.User.UserStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserDAO
 */
public class UserDAOImpl implements UserDAO {

    private final DatabaseConnection dbConnection;

    public UserDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public User create(User user) throws SQLException, DuplicateUsernameException {
        if (existsByUsername(user.getUsername())) {
            throw new DuplicateUsernameException("Username already exists: " + user.getUsername());
        }

        String sql = "INSERT INTO users (username, password, role, status, created_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setIdUser(rs.getInt(1));
                    }
                }
            }

            return user;
        }
    }

    @Override
    public Optional<User> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id_user = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }

        return users;
    }

    @Override
    public boolean update(User user) throws SQLException, DuplicateUsernameException {
        // Check if username is taken by another user
        Optional<User> existing = findByUsername(user.getUsername());
        if (existing.isPresent() && !existing.get().getIdUser().equals(user.getIdUser())) {
            throw new DuplicateUsernameException("Username already exists: " + user.getUsername());
        }

        String sql = "UPDATE users SET username = ?, password = ?, role = ?, status = ? WHERE id_user = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getStatus().name());
            stmt.setInt(5, user.getIdUser());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM users WHERE id_user = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<User> authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 'ACTIVE'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id_user"),
                rs.getString("username"),
                rs.getString("password"),
                UserRole.valueOf(rs.getString("role")),
                UserStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}