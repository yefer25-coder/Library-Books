package services;

import config.LoggerConfig;
import dao.MemberDAO;
import dao.impl.MemberDAOImpl;
import exceptions.*;
import models.Member;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Member business logic
 */
public class MemberService {

    private final MemberDAO memberDAO;

    public MemberService() {
        this.memberDAO = new MemberDAOImpl();
    }

    /**
     * Create new member - simulates HTTP POST request
     */
    public Member createMember(String name, String email, String phone, String address)
            throws DuplicateEmailException, DatabaseException {
        try {
            LoggerConfig.logHttpRequest("POST", "/api/members", "system");

            // Validate input
            validateMemberData(name, email);

            Member member = new Member(name, email, phone, address);
            Member createdMember = memberDAO.create(member);

            LoggerConfig.logHttpResponse(201, "Member created successfully");
            LoggerConfig.logInfo("New member created: " + email);

            return createdMember;

        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error creating member", e);
            throw new DatabaseException("Error creating member", e);
        }
    }

    /**
     * Get all members - simulates HTTP GET request
     */
    public List<Member> getAllMembers() throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/members", "system");
            List<Member> members = memberDAO.findAll();
            LoggerConfig.logHttpResponse(200, "Members retrieved: " + members.size());
            return members;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving members", e);
            throw new DatabaseException("Error retrieving members", e);
        }
    }

    /**
     * Get only active members
     */
    public List<Member> getActiveMembers() throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/members?status=active", "system");
            List<Member> members = memberDAO.findAllActive();
            LoggerConfig.logHttpResponse(200, "Active members retrieved: " + members.size());
            return members;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving active members", e);
            throw new DatabaseException("Error retrieving active members", e);
        }
    }

    /**
     * Find member by ID - simulates HTTP GET request
     */
    public Optional<Member> findMemberById(Integer id) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/members/" + id, "system");
            Optional<Member> member = memberDAO.findById(id);
            LoggerConfig.logHttpResponse(member.isPresent() ? 200 : 404,
                    member.isPresent() ? "Member found" : "Member not found");
            return member;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error finding member", e);
            throw new DatabaseException("Error finding member", e);
        }
    }

    /**
     * Update member - simulates HTTP PATCH request
     */
    public boolean updateMember(Member member) throws DuplicateEmailException, DatabaseException {
        try {
            LoggerConfig.logHttpRequest("PATCH", "/api/members/" + member.getIdMember(), "system");

            // Validate input
            validateMemberData(member.getName(), member.getEmail());

            boolean updated = memberDAO.update(member);
            LoggerConfig.logHttpResponse(updated ? 200 : 404,
                    updated ? "Member updated successfully" : "Member not found");

            if (updated) {
                LoggerConfig.logInfo("Member updated: " + member.getEmail());
            }

            return updated;

        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error updating member", e);
            throw new DatabaseException("Error updating member", e);
        }
    }

    /**
     * Delete member - simulates HTTP DELETE request
     */
    public boolean deleteMember(Integer id) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("DELETE", "/api/members/" + id, "system");
            boolean deleted = memberDAO.delete(id);
            LoggerConfig.logHttpResponse(deleted ? 200 : 404,
                    deleted ? "Member deleted successfully" : "Member not found");

            if (deleted) {
                LoggerConfig.logInfo("Member deleted: ID " + id);
            }

            return deleted;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error deleting member", e);
            throw new DatabaseException("Error deleting member", e);
        }
    }

    /**
     * Activate member - simulates HTTP PATCH request
     */
    public boolean activateMember(Integer id) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("PATCH", "/api/members/" + id + "/activate", "system");
            boolean activated = memberDAO.activate(id);
            LoggerConfig.logHttpResponse(activated ? 200 : 404,
                    activated ? "Member activated" : "Member not found");

            if (activated) {
                LoggerConfig.logInfo("Member activated: ID " + id);
            }

            return activated;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error activating member", e);
            throw new DatabaseException("Error activating member", e);
        }
    }

    /**
     * Deactivate member - simulates HTTP PATCH request
     */
    public boolean deactivateMember(Integer id) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("PATCH", "/api/members/" + id + "/deactivate", "system");
            boolean deactivated = memberDAO.deactivate(id);
            LoggerConfig.logHttpResponse(deactivated ? 200 : 404,
                    deactivated ? "Member deactivated" : "Member not found");

            if (deactivated) {
                LoggerConfig.logInfo("Member deactivated: ID " + id);
            }

            return deactivated;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error deactivating member", e);
            throw new DatabaseException("Error deactivating member", e);
        }
    }


    /**
     * Validate member input data
     */
    private void validateMemberData(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Member email cannot be empty");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}