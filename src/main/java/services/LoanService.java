package services;

import config.ConfigLoader;
import config.DatabaseConnection;
import config.LoggerConfig;
import dao.BookDAO;
import dao.LoanDAO;
import dao.MemberDAO;
import dao.impl.BookDAOImpl;
import dao.impl.LoanDAOImpl;
import dao.impl.MemberDAOImpl;
import exceptions.*;
import models.Book;
import models.Loan;
import models.Member;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Loan business logic with TRANSACTIONS
 */
public class LoanService {

    private final LoanDAO loanDAO;
    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final DatabaseConnection dbConnection;
    private final ConfigLoader config;

    public LoanService() {
        this.loanDAO = new LoanDAOImpl();
        this.bookDAO = new BookDAOImpl();
        this.memberDAO = new MemberDAOImpl();
        this.dbConnection = DatabaseConnection.getInstance();
        this.config = ConfigLoader.getInstance();
    }

    /**
     * Create loan with TRANSACTION (insert loan + update book stock)
     * Simulates HTTP POST request
     */
    public Loan createLoan(String isbn, Integer memberId)
            throws InsufficientStockException, InactiveMemberException, EntityNotFoundException,
            InvalidLoanException, DatabaseException {

        Connection conn = null;

        try {
            LoggerConfig.logHttpRequest("POST", "/api/loans", "system");

            // Get new connection for transaction
            conn = dbConnection.getNewConnection();
            conn.setAutoCommit(false); // Start transaction

            // Validate book exists and has stock
            Optional<Book> bookOpt = bookDAO.findByIsbn(isbn);
            if (bookOpt.isEmpty()) {
                throw new EntityNotFoundException("Book not found with ISBN: " + isbn);
            }

            Book book = bookOpt.get();

            if (!book.isActive()) {
                throw new InvalidLoanException("Book is inactive: " + book.getTitle());
            }

            if (!book.hasAvailableCopies()) {
                throw new InsufficientStockException("No available copies for: " + book.getTitle());
            }

            // Validate member exists and is active
            Optional<Member> memberOpt = memberDAO.findById(memberId);
            if (memberOpt.isEmpty()) {
                throw new EntityNotFoundException("Member not found with ID: " + memberId);
            }

            Member member = memberOpt.get();

            if (!member.isActive()) {
                throw new InactiveMemberException("Member is inactive: " + member.getName());
            }

            // Create loan
            int loanDays = config.getLoanDays();
            LocalDate dueDate = LocalDate.now().plusDays(loanDays);

            Loan loan = new Loan(isbn, memberId, dueDate);
            Loan createdLoan = loanDAO.create(loan, conn);

            // Update book stock
            int newAvailable = book.getAvailableCopies() - 1;
            bookDAO.updateAvailableCopies(isbn, newAvailable);

            // Commit transaction
            conn.commit();

            LoggerConfig.logHttpResponse(201, "Loan created successfully");
            LoggerConfig.logInfo("New loan created: Loan ID " + createdLoan.getIdLoan() + " - Book: " + isbn);

            return createdLoan;

        } catch (InsufficientStockException | InactiveMemberException | EntityNotFoundException | InvalidLoanException e) {
            // Rollback transaction on business rule violation
            if (conn != null) {
                try {
                    conn.rollback();
                    LoggerConfig.logWarning("Transaction rolled back: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    LoggerConfig.logError("Error during rollback", rollbackEx);
                }
            }
            LoggerConfig.logHttpResponse(400, e.getMessage());
            throw e;

        } catch (SQLException e) {
            // Rollback transaction on database error
            if (conn != null) {
                try {
                    conn.rollback();
                    LoggerConfig.logWarning("Transaction rolled back due to database error");
                } catch (SQLException rollbackEx) {
                    LoggerConfig.logError("Error during rollback", rollbackEx);
                }
            }
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error creating loan", e);
            throw new DatabaseException("Error creating loan", e);

        } finally {
            // Close connection
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LoggerConfig.logError("Error closing connection", e);
                }
            }
        }
    }

    /**
     * Return loan with TRANSACTION (update loan + calculate fine + restore book stock)
     * Simulates HTTP PATCH request
     */
    public void returnLoan(Integer loanId)
            throws EntityNotFoundException, InvalidLoanException, DatabaseException {

        Connection conn = null;

        try {
            LoggerConfig.logHttpRequest("PATCH", "/api/loans/" + loanId + "/return", "system");

            // Get new connection for transaction
            conn = dbConnection.getNewConnection();
            conn.setAutoCommit(false); // Start transaction

            // Validate loan exists
            Optional<Loan> loanOpt = loanDAO.findById(loanId);
            if (loanOpt.isEmpty()) {
                throw new EntityNotFoundException("Loan not found with ID: " + loanId);
            }

            Loan loan = loanOpt.get();

            if (!loan.isActive()) {
                throw new InvalidLoanException("Loan is already returned");
            }

            // Calculate fine if overdue
            double finePerDay = config.getFinePerDay();
            double fineAmount = loan.calculateFine(finePerDay);

            // Mark loan as returned
            loanDAO.markAsReturned(loanId, fineAmount, conn);

            // Restore book stock
            Optional<Book> bookOpt = bookDAO.findByIsbn(loan.getIsbn());
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                int newAvailable = book.getAvailableCopies() + 1;
                bookDAO.updateAvailableCopies(loan.getIsbn(), newAvailable);
            }

            // Commit transaction
            conn.commit();

            LoggerConfig.logHttpResponse(200, "Loan returned successfully");
            LoggerConfig.logInfo("Loan returned: Loan ID " + loanId + " - Fine: $" + fineAmount);

            if (fineAmount > 0) {
                LoggerConfig.logInfo("Fine calculated: $" + fineAmount + " for " + loan.getDaysOverdue() + " days overdue");
            }

        } catch (EntityNotFoundException | InvalidLoanException e) {
            // Rollback transaction on business rule violation
            if (conn != null) {
                try {
                    conn.rollback();
                    LoggerConfig.logWarning("Transaction rolled back: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    LoggerConfig.logError("Error during rollback", rollbackEx);
                }
            }
            LoggerConfig.logHttpResponse(400, e.getMessage());
            throw e;

        } catch (SQLException e) {
            // Rollback transaction on database error
            if (conn != null) {
                try {
                    conn.rollback();
                    LoggerConfig.logWarning("Transaction rolled back due to database error");
                } catch (SQLException rollbackEx) {
                    LoggerConfig.logError("Error during rollback", rollbackEx);
                }
            }
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error returning loan", e);
            throw new DatabaseException("Error returning loan", e);

        } finally {
            // Close connection
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LoggerConfig.logError("Error closing connection", e);
                }
            }
        }
    }

    /**
     * Get all loans - simulates HTTP GET request
     */
    public List<Loan> getAllLoans() throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/loans", "system");
            List<Loan> loans = loanDAO.findAll();
            LoggerConfig.logHttpResponse(200, "Loans retrieved: " + loans.size());
            return loans;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving loans", e);
            throw new DatabaseException("Error retrieving loans", e);
        }
    }

    /**
     * Get active loans
     */
    public List<Loan> getActiveLoans() throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/loans?status=active", "system");
            List<Loan> loans = loanDAO.findAllActive();
            LoggerConfig.logHttpResponse(200, "Active loans retrieved: " + loans.size());
            return loans;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving active loans", e);
            throw new DatabaseException("Error retrieving active loans", e);
        }
    }

    /**
     * Get overdue loans (for CSV export)
     */
    public List<Loan> getOverdueLoans() throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/loans?status=overdue", "system");
            List<Loan> loans = loanDAO.findOverdueLoans();
            LoggerConfig.logHttpResponse(200, "Overdue loans retrieved: " + loans.size());
            return loans;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving overdue loans", e);
            throw new DatabaseException("Error retrieving overdue loans", e);
        }
    }

    /**
     * Get loans by member
     */
    public List<Loan> getLoansByMember(Integer memberId) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/loans?memberId=" + memberId, "system");
            List<Loan> loans = loanDAO.findByMember(memberId);
            LoggerConfig.logHttpResponse(200, "Loans found: " + loans.size());
            return loans;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving loans by member", e);
            throw new DatabaseException("Error retrieving loans by member", e);
        }
    }


    /**
     * Find loan by ID
     */
    public Optional<Loan> findLoanById(Integer id) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/loans/" + id, "system");
            Optional<Loan> loan = loanDAO.findById(id);
            LoggerConfig.logHttpResponse(loan.isPresent() ? 200 : 404,
                    loan.isPresent() ? "Loan found" : "Loan not found");
            return loan;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error finding loan", e);
            throw new DatabaseException("Error finding loan", e);
        }
    }

    /**
     * Calculate fine for a loan (business logic)
     */
    public double calculateFine(Loan loan) {
        double finePerDay = config.getFinePerDay();
        return loan.calculateFine(finePerDay);
    }
}