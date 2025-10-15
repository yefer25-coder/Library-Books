package dao;

import models.Loan;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Loan data access operations
 * Note: This DAO uses transaction-aware methods that receive Connection objects
 */
public interface LoanDAO {

    /**
     * Create a new loan within a transaction
     * @param loan Loan object to create
     * @param connection Active database connection for transaction
     * @return Created loan with generated ID
     * @throws SQLException if database error occurs
     */
    Loan create(Loan loan, Connection connection) throws SQLException;

    /**
     * Find loan by ID
     * @param id Loan ID
     * @return Optional containing loan if found, empty otherwise
     * @throws SQLException if database error occurs
     */
    Optional<Loan> findById(Integer id) throws SQLException;

    /**
     * Find all loans
     * @return List of all loans
     * @throws SQLException if database error occurs
     */
    List<Loan> findAll() throws SQLException;

    /**
     * Find all active loans (status = ACTIVE)
     * @return List of active loans ordered by due date
     * @throws SQLException if database error occurs
     */
    List<Loan> findAllActive() throws SQLException;

    /**
     * Find overdue loans (active loans past due date)
     * @return List of overdue loans ordered by due date
     * @throws SQLException if database error occurs
     */
    List<Loan> findOverdueLoans() throws SQLException;

    /**
     * Find all loans for a specific member
     * @param idMember Member ID
     * @return List of loans for the member
     * @throws SQLException if database error occurs
     */
    List<Loan> findByMember(Integer idMember) throws SQLException;

    /**
     * Find active loans for a specific member
     * @param idMember Member ID
     * @return List of active loans for the member
     * @throws SQLException if database error occurs
     */
    List<Loan> findActiveLoansByMember(Integer idMember) throws SQLException;

    /**
     * Find all loans for a specific book
     * @param isbn Book ISBN
     * @return List of loans for the book
     * @throws SQLException if database error occurs
     */
    List<Loan> findByBook(String isbn) throws SQLException;

    /**
     * Update loan information within a transaction
     * @param loan Loan object with updated data
     * @param connection Active database connection for transaction
     * @return true if update was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean update(Loan loan, Connection connection) throws SQLException;

    /**
     * Delete loan by ID
     * @param id Loan ID
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean delete(Integer id) throws SQLException;

    /**
     * Mark loan as returned and set fine amount within a transaction
     * @param idLoan Loan ID
     * @param fineAmount Fine amount to be set
     * @param connection Active database connection for transaction
     * @return true if operation was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean markAsReturned(Integer idLoan, double fineAmount, Connection connection) throws SQLException;
}