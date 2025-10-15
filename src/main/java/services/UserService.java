package services;

import config.LoggerConfig;
import dao.UserDAO;
import dao.impl.UserDAOImpl;
import exceptions.*;
import models.User;

import java.sql.SQLException;
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

}