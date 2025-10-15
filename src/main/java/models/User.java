package models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User entity representing system users with authentication and roles
 */
public class User {
    private Integer idUser;
    private String username;
    private String password;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;

    // Constructors
    public User() {
        this.role = UserRole.ASSISTANT;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public User(Integer idUser, String username, String password, UserRole role,
                UserStatus status, LocalDateTime createdAt) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Business methods
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(idUser, user.idUser) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, username);
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', role=%s, status=%s}",
                idUser, username, role, status);
    }

    // Enums
    public enum UserRole {
        ADMIN, ASSISTANT
    }

    public enum UserStatus {
        ACTIVE, INACTIVE
    }
}