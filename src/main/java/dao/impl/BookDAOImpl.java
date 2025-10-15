package dao.impl;

import config.DatabaseConnection;
import dao.BookDAO;
import exceptions.DuplicateIsbnException;
import models.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of BookDAO
 */
public class BookDAOImpl implements BookDAO {

    private final DatabaseConnection dbConnection;

    public BookDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Book create(Book book) throws SQLException, DuplicateIsbnException {
        if (existsByIsbn(book.getIsbn())) {
            throw new DuplicateIsbnException("ISBN already exists: " + book.getIsbn());
        }

        String sql = "INSERT INTO books (isbn, title, author, category, total_copies, available_copies, reference_price, is_active, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getCategory());
            stmt.setInt(5, book.getTotalCopies());
            stmt.setInt(6, book.getAvailableCopies());
            stmt.setDouble(7, book.getReferencePrice());
            stmt.setBoolean(8, book.getIsActive());
            stmt.setTimestamp(9, Timestamp.valueOf(book.getCreatedAt()));

            stmt.executeUpdate();
            return book;
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }

        return books;
    }

    @Override
    public List<Book> findAllActive() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE is_active = TRUE ORDER BY title";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }

        return books;
    }

    @Override
    public List<Book> findByCategory(String category) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE category = ? AND is_active = TRUE ORDER BY title";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }

        return books;
    }

    @Override
    public List<Book> findByAuthor(String author) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author LIKE ? AND is_active = TRUE ORDER BY title";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + author + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }

        return books;
    }

    @Override
    public boolean update(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, category = ?, total_copies = ?, " +
                "available_copies = ?, reference_price = ?, is_active = ? WHERE isbn = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getCategory());
            stmt.setInt(4, book.getTotalCopies());
            stmt.setInt(5, book.getAvailableCopies());
            stmt.setDouble(6, book.getReferencePrice());
            stmt.setBoolean(7, book.getIsActive());
            stmt.setString(8, book.getIsbn());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String isbn) throws SQLException {
        String sql = "DELETE FROM books WHERE isbn = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean activate(String isbn) throws SQLException {
        String sql = "UPDATE books SET is_active = TRUE WHERE isbn = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deactivate(String isbn) throws SQLException {
        String sql = "UPDATE books SET is_active = FALSE WHERE isbn = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateAvailableCopies(String isbn, int newAvailable) throws SQLException {
        String sql = "UPDATE books SET available_copies = ? WHERE isbn = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newAvailable);
            stmt.setString(2, isbn);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsByIsbn(String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM books WHERE isbn = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Map ResultSet to Book object
     */
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies"),
                rs.getDouble("reference_price"),
                rs.getBoolean("is_active"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}