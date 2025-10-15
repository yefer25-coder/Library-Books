package dao;

import models.Member;
import exceptions.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Member data access operations
 */
public interface MemberDAO {

    /**
     * Create a new member
     */
    Member create(Member member) throws SQLException, DuplicateEmailException;

    /**
     * Find member by ID
     */
    Optional<Member> findById(Integer id) throws SQLException;

    /**
     * Find member by email
     */
    Optional<Member> findByEmail(String email) throws SQLException;

    /**
     * Find all members
     */
    List<Member> findAll() throws SQLException;

    /**
     * Find only active members
     */
    List<Member> findAllActive() throws SQLException;

    /**
     * Update member information
     */
    boolean update(Member member) throws SQLException, DuplicateEmailException;

    /**
     * Delete member by ID
     */
    boolean delete(Integer id) throws SQLException;

    /**
     * Activate member
     */
    boolean activate(Integer id) throws SQLException;

    /**
     * Deactivate member
     */
    boolean deactivate(Integer id) throws SQLException;

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email) throws SQLException;
}