package services;

import config.LoggerConfig;
import dao.BookDAO;
import dao.impl.BookDAOImpl;
import exceptions.*;
import models.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Book business logic with validations
 */
public class BookService {

    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAOImpl();
    }

    /**
     * Create new book with ISBN validation - simulates HTTP POST request
     */
    public Book createBook(String isbn, String title, String author, String category,
                           Integer totalCopies, Double referencePrice)
            throws DuplicateIsbnException, DatabaseException {
        try {
            LoggerConfig.logHttpRequest("POST", "/api/books", "system");

            // Validate input
            validateBookData(isbn, title, author, totalCopies, referencePrice);

            // Validate ISBN is unique (business rule)
            if (bookDAO.existsByIsbn(isbn)) {
                LoggerConfig.logHttpResponse(409, "ISBN already exists");
                throw new DuplicateIsbnException("ISBN already exists: " + isbn);
            }

            Book book = new Book(isbn, title, author, category, totalCopies, referencePrice);
            Book createdBook = bookDAO.create(book);

            LoggerConfig.logHttpResponse(201, "Book created successfully");
            LoggerConfig.logInfo("New book created: " + isbn + " - " + title);

            return createdBook;

        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error creating book", e);
            throw new DatabaseException("Error creating book", e);
        }
    }

    /**
     * Get all books - simulates HTTP GET request
     */
    public List<Book> getAllBooks() throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/books", "system");
            List<Book> books = bookDAO.findAll();
            LoggerConfig.logHttpResponse(200, "Books retrieved: " + books.size());
            return books;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving books", e);
            throw new DatabaseException("Error retrieving books", e);
        }
    }

    /**
     * Get only active books
     */
    public List<Book> getActiveBooks() throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/books?status=active", "system");
            List<Book> books = bookDAO.findAllActive();
            LoggerConfig.logHttpResponse(200, "Active books retrieved: " + books.size());
            return books;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving active books", e);
            throw new DatabaseException("Error retrieving active books", e);
        }
    }

    /**
     * Find books by category - simulates HTTP GET request
     */
    public List<Book> getBooksByCategory(String category) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/books?category=" + category, "system");
            List<Book> books = bookDAO.findByCategory(category);
            LoggerConfig.logHttpResponse(200, "Books found: " + books.size());
            return books;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving books by category", e);
            throw new DatabaseException("Error retrieving books by category", e);
        }
    }

    /**
     * Find books by author - simulates HTTP GET request
     */
    public List<Book> getBooksByAuthor(String author) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/books?author=" + author, "system");
            List<Book> books = bookDAO.findByAuthor(author);
            LoggerConfig.logHttpResponse(200, "Books found: " + books.size());
            return books;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error retrieving books by author", e);
            throw new DatabaseException("Error retrieving books by author", e);
        }
    }

    /**
     * Find book by ISBN - simulates HTTP GET request
     */
    public Optional<Book> findBookByIsbn(String isbn) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("GET", "/api/books/" + isbn, "system");
            Optional<Book> book = bookDAO.findByIsbn(isbn);
            LoggerConfig.logHttpResponse(book.isPresent() ? 200 : 404,
                    book.isPresent() ? "Book found" : "Book not found");
            return book;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error finding book", e);
            throw new DatabaseException("Error finding book", e);
        }
    }

    /**
     * Update book - simulates HTTP PATCH request
     */
    public boolean updateBook(Book book) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("PATCH", "/api/books/" + book.getIsbn(), "system");

            // Validate input
            validateBookData(book.getIsbn(), book.getTitle(), book.getAuthor(),
                    book.getTotalCopies(), book.getReferencePrice());

            boolean updated = bookDAO.update(book);
            LoggerConfig.logHttpResponse(updated ? 200 : 404,
                    updated ? "Book updated successfully" : "Book not found");

            if (updated) {
                LoggerConfig.logInfo("Book updated: " + book.getIsbn());
            }

            return updated;

        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error updating book", e);
            throw new DatabaseException("Error updating book", e);
        }
    }

    /**
     * Delete book - simulates HTTP DELETE request
     */
    public boolean deleteBook(String isbn) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("DELETE", "/api/books/" + isbn, "system");
            boolean deleted = bookDAO.delete(isbn);
            LoggerConfig.logHttpResponse(deleted ? 200 : 404,
                    deleted ? "Book deleted successfully" : "Book not found");

            if (deleted) {
                LoggerConfig.logInfo("Book deleted: " + isbn);
            }

            return deleted;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error deleting book", e);
            throw new DatabaseException("Error deleting book", e);
        }
    }

    /**
     * Activate book - simulates HTTP PATCH request
     */
    public boolean activateBook(String isbn) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("PATCH", "/api/books/" + isbn + "/activate", "system");
            boolean activated = bookDAO.activate(isbn);
            LoggerConfig.logHttpResponse(activated ? 200 : 404,
                    activated ? "Book activated" : "Book not found");

            if (activated) {
                LoggerConfig.logInfo("Book activated: " + isbn);
            }

            return activated;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error activating book", e);
            throw new DatabaseException("Error activating book", e);
        }
    }

    /**
     * Deactivate book - simulates HTTP PATCH request
     */
    public boolean deactivateBook(String isbn) throws DatabaseException {
        try {
            LoggerConfig.logHttpRequest("PATCH", "/api/books/" + isbn + "/deactivate", "system");
            boolean deactivated = bookDAO.deactivate(isbn);
            LoggerConfig.logHttpResponse(deactivated ? 200 : 404,
                    deactivated ? "Book deactivated" : "Book not found");

            if (deactivated) {
                LoggerConfig.logInfo("Book deactivated: " + isbn);
            }

            return deactivated;
        } catch (SQLException e) {
            LoggerConfig.logHttpResponse(500, "Database error");
            LoggerConfig.logError("Error deactivating book", e);
            throw new DatabaseException("Error deactivating book", e);
        }
    }

    /**
     * Validate book has available stock (business rule)
     */
    public void validateBookHasStock(String isbn) throws InsufficientStockException, EntityNotFoundException, DatabaseException {
        try {
            Optional<Book> bookOpt = bookDAO.findByIsbn(isbn);

            if (bookOpt.isEmpty()) {
                throw new EntityNotFoundException("Book not found with ISBN: " + isbn);
            }

            Book book = bookOpt.get();
            if (!book.hasAvailableCopies()) {
                throw new InsufficientStockException("No available copies for book: " + book.getTitle());
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error validating book stock", e);
        }
    }

    /**
     * Validate book input data
     */
    private void validateBookData(String isbn, String title, String author, Integer totalCopies, Double referencePrice) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }

        if (totalCopies == null || totalCopies <= 0) {
            throw new IllegalArgumentException("Total copies must be greater than 0");
        }

        if (referencePrice == null || referencePrice < 0) {
            throw new IllegalArgumentException("Reference price cannot be negative");
        }
    }
}