package dao.impl;

import config.DatabaseConnection;
import dao.LoanDAO;
import models.Loan;
import models.Loan.LoanStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of LoanDAO with transaction support
 */
public class LoanDAOImpl implements LoanDAO {

    private final DatabaseConnection dbConnection;

    public LoanDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Loan create(Loan loan, Connection connection) throws SQLException {
        String sql = "INSERT INTO loans (isbn, id_member, loan_date, due_date, fine_amount, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, loan.getIsbn());
            stmt.setInt(2, loan.getIdMember());
            stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));
            stmt.setDouble(5, loan.getFineAmount());
            stmt.setString(6, loan.getStatus().name());
            stmt.setTimestamp(7, Timestamp.valueOf(loan.getCreatedAt()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        loan.setIdLoan(rs.getInt(1));
                    }
                }
            }

            return loan;
        }
    }

    @Override
    public Optional<Loan> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM loans WHERE id_loan = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLoan(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Loan> findAll() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }

        return loans;
    }

    @Override
    public List<Loan> findAllActive() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE status = 'ACTIVE' ORDER BY due_date";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }

        return loans;
    }

    @Override
    public List<Loan> findOverdueLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE status = 'ACTIVE' AND due_date < CURDATE() ORDER BY due_date";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }

        return loans;
    }

    @Override
    public List<Loan> findByMember(Integer idMember) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE id_member = ? ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMember);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        }

        return loans;
    }

    @Override
    public List<Loan> findActiveLoansByMember(Integer idMember) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE id_member = ? AND status = 'ACTIVE' ORDER BY due_date";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMember);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        }

        return loans;
    }

    @Override
    public List<Loan> findByBook(String isbn) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE isbn = ? ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        }

        return loans;
    }

    @Override
    public boolean update(Loan loan, Connection connection) throws SQLException {
        String sql = "UPDATE loans SET isbn = ?, id_member = ?, loan_date = ?, due_date = ?, " +
                "return_date = ?, fine_amount = ?, status = ? WHERE id_loan = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loan.getIsbn());
            stmt.setInt(2, loan.getIdMember());
            stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));
            stmt.setDate(5, loan.getReturnDate() != null ? Date.valueOf(loan.getReturnDate()) : null);
            stmt.setDouble(6, loan.getFineAmount());
            stmt.setString(7, loan.getStatus().name());
            stmt.setInt(8, loan.getIdLoan());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM loans WHERE id_loan = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean markAsReturned(Integer idLoan, double fineAmount, Connection connection) throws SQLException {
        String sql = "UPDATE loans SET status = 'RETURNED', return_date = CURDATE(), fine_amount = ? WHERE id_loan = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, fineAmount);
            stmt.setInt(2, idLoan);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Map ResultSet to Loan object
     */
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Date returnDateSql = rs.getDate("return_date");
        LocalDate returnDate = returnDateSql != null ? returnDateSql.toLocalDate() : null;

        return new Loan(
                rs.getInt("id_loan"),
                rs.getString("isbn"),
                rs.getInt("id_member"),
                rs.getDate("loan_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                returnDate,
                rs.getDouble("fine_amount"),
                LoanStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
