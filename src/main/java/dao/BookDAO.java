package dao;

import models.Book;
import exceptions.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Book data access operations
 */
public interface BookDAO {

    /**
     * Create a new book
     */
    Book create(Book book) throws SQLException, DuplicateIsbnException;

    /**
     * Find book by ISBN
     */
    Optional<Book> findByIsbn(String isbn) throws SQLException;

    /**
     * Find all books
     */
    List<Book> findAll() throws SQLException;

    /**
     * Find only active books
     */
    List<Book> findAllActive() throws SQLException;

    /**
     * Find books by category
     */
    List<Book> findByCategory(String category) throws SQLException;

    /**
     * Find books by author
     */
    List<Book> findByAuthor(String author) throws SQLException;

    /**
     * Update book information
     */
    boolean update(Book book) throws SQLException;

    /**
     * Delete book by ISBN
     */
    boolean delete(String isbn) throws SQLException;

    /**
     * Activate book
     */
    boolean activate(String isbn) throws SQLException;

    /**
     * Deactivate book
     */
    boolean deactivate(String isbn) throws SQLException;

    /**
     * Update available copies (for loans/returns)
     */
    boolean updateAvailableCopies(String isbn, int newAvailable) throws SQLException;

    /**
     * Check if ISBN exists
     */
    boolean existsByIsbn(String isbn) throws SQLException;
}