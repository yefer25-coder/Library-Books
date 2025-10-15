package dao;

import models.User;
import exceptions.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface for User data access operations
 */
public interface UserDAO {

    /**
     * Create a new user
     */
    User create(User user) throws SQLException, DuplicateUsernameException;

    /**
     * Find user by ID
     */
    Optional<User> findById(Integer id) throws SQLException;

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username) throws SQLException;

    /**
     * Find all users
     */
    List<User> findAll() throws SQLException;

    /**
     * Update user information
     */
    boolean update(User user) throws SQLException, DuplicateUsernameException;

    /**
     * Delete user by ID
     */
    boolean delete(Integer id) throws SQLException;

    /**
     * Authenticate user with username and password
     */
    Optional<User> authenticate(String username, String password) throws SQLException;

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username) throws SQLException;
}