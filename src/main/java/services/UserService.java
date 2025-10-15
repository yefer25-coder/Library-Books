package services;

import config.LoggerConfig;
import dao.UserDAO;
import dao.impl.UserDAOImpl;
import exceptions.*;
import models.User;
import models.User.UserRole;
import models.User.UserStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for User business logic with decorator pattern
 */
public class UserService {

    private final UserDAO userDAO;
    private User currentUser;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Authenticate user - simulates HTTP POST request
     */
    public User login(String username, String password) throws InvalidCredentialsException {
        try {
            LoggerConfig.logHttpRequest("POST", "/api/auth/login", username);

            Optional<User> userOpt = userDAO.authenticate(username, password);

            if (userOpt.isEmpty()) {
                LoggerConfig.logHttpResponse(401, "Invalid credentials");
                throw new InvalidCredentialsException("Invalid username or password");
            }

            User user = userOpt.get();

            if (!user.isActive()) {
                LoggerConfig.logHttpResponse(403, "User account is inactive");
                throw new InvalidCredentialsException("User account is inactive");
            }

            this.currentUser = user;
            LoggerConfig.logHttpResponse(200, "Login successful");
            LoggerConfig.logInfo("User logged in: " + username);

            return user;

        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Login failed", e);
            throw new InvalidCredentialsException("Authentication error occurred");
        }
    }

    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            LoggerConfig.logHttpRequest("POST", "/api/auth/logout", currentUser.getUsername());
            LoggerConfig.logInfo("User logged out: " + currentUser.getUsername());
            currentUser = null;
            LoggerConfig.logHttpResponse(200, "Logout successful");
        }
    }

    /**
     * Create new user with decorator pattern (adds default properties)
     * Simulates HTTP POST request
     */
    public User createUser(String username, String password) throws DuplicateUsernameException, DatabaseException {
        try {
            LoggerConfig.logHttpRequest("POST", "/api/users", getCurrentUsername());

            // Validate input
            if (username == null || username.trim().isEmpty()) {
                LoggerConfig.logHttpResponse(400, "Username cannot be empty");
                throw new IllegalArgumentException("Username cannot be empty");
            }

            if (password == null || password.trim().isEmpty()) {
                LoggerConfig.logHttpResponse(400, "Password cannot be empty");
                throw new IllegalArgumentException("Password cannot be empty");
            }

            // Create user with decorator pattern - adds default properties
            User user = createUserWithDefaults(username, password);

            User createdUser = userDAO.create(user);

            LoggerConfig.logHttpResponse(201, "User created successfully");
            LoggerConfig.logInfo("New user created: " + username);

            return createdUser;

        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error creating user", e);
            throw new DatabaseException("Error creating user", e);
        }
    }

    /**
     * DECORATOR PATTERN: Adds default properties to new user
     * - role: ASSISTANT (default)
     * - status: ACTIVE
     * - createdAt: now()
     */
    private User createUserWithDefaults(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(UserRole.ASSISTANT); // Default role
        user.setStatus(UserStatus.ACTIVE); // Default status
        user.setCreatedAt(LocalDateTime.now()); // Default timestamp
        return user;
    }

    /**
     * Get all users - simulates HTTP GET request
     */
    public List<User> getAllUsers() throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/users", getCurrentUsername());
            List<User> users = userDAO.findAll();
            LoggerConfig.logHttpResponse(200, "Users retrieved: " + users.size());
            return users;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving users", e);
            throw new DatabaseException("Error retrieving users", e);
        }
    }

    /**
     * Find user by ID - simulates HTTP GET request
     */
    public Optional<User> findUserById(Integer id) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/users/" + id, getCurrentUsername());
            Optional<User> user = userDAO.findById(id);
            LoggerConfig.logHttpResponse(user.isPresent() ? 200 : 404,
                    user.isPresent() ? "User found" : "User not found");
            return user;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error finding user", e);
            throw new DatabaseException("Error finding user", e);
        }
    }

    /**
     * Update user - simulates HTTP PATCH request
     */
    public boolean updateUser(User user) throws DuplicateUsernameException, DatabaseException {
        try {
            LoggerConfig.logHttpRequest("PATCH", "/api/users/" + user.getIdUser(), getCurrentUsername());
            boolean updated = userDAO.update(user);
            LoggerConfig.logHttpResponse(updated ? 200 : 404,
                    updated ? "User updated successfully" : "User not found");

            if (updated) {
                LoggerConfig.logInfo("User updated: " + user.getUsername());
            }

            return updated;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error updating user", e);
            throw new DatabaseException("Error updating user", e);
        }
    }

    /**
     * Delete user - simulates HTTP DELETE request
     */
    public boolean deleteUser(Integer id) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("DELETE", "/api/users/" + id, getCurrentUsername());
            boolean deleted = userDAO.delete(id);
            LoggerConfig.logHttpResponse(deleted ? 200 : 404,
                    deleted ? "User deleted successfully" : "User not found");

            if (deleted) {
                LoggerConfig.logInfo("User deleted: ID " + id);
            }

            return deleted;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error deleting user", e);
            throw new DatabaseException("Error deleting user", e);
        }
    }

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if current user is admin
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    private String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "anonymous";
    }
}