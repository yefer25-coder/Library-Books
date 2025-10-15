package controllers;

import exceptions.DatabaseException;
import exceptions.DuplicateIsbnException;
import models.Book;
import services.BookService;
import utils.InputValidator;
import utils.MessageHelper;
import utils.TableFormatter;

import java.util.List;
import java.util.Optional;

/**
 * Controller for book operations
 */
public class BookController {

    private final BookService bookService;

    public BookController() {
        this.bookService = new BookService();
    }

    /**
     * Create a new book
     */
    public boolean createBook(String isbn, String title, String author, String category,
                              String totalCopiesStr, String referencePriceStr) {
        try {
            // Validate input
            if (InputValidator.isNullOrEmpty(isbn)) {
                MessageHelper.showError("El ISBN es requerido");
                return false;
            }

            if (!InputValidator.isValidISBN(isbn)) {
                MessageHelper.showError("Formato de ISBN inválido");
                return false;
            }

            if (InputValidator.isNullOrEmpty(title)) {
                MessageHelper.showError("El título es requerido");
                return false;
            }

            if (InputValidator.isNullOrEmpty(author)) {
                MessageHelper.showError("El autor es requerido");
                return false;
            }

            if (!InputValidator.isPositiveInteger(totalCopiesStr)) {
                MessageHelper.showError("El número de ejemplares debe ser un entero positivo");
                return false;
            }

            if (!InputValidator.isNonNegativeDouble(referencePriceStr)) {
                MessageHelper.showError("El precio debe ser un número positivo");
                return false;
            }

            Integer totalCopies = InputValidator.parseIntSafely(totalCopiesStr);
            Double referencePrice = InputValidator.parseDoubleSafely(referencePriceStr);

            // Create book
            Book book = bookService.createBook(isbn, title, author, category, totalCopies, referencePrice);

            MessageHelper.showSuccess("Libro creado exitosamente:\n" +
                    "ISBN: " + book.getIsbn() + "\n" +
                    "Título: " + book.getTitle());
            return true;

        } catch (DuplicateIsbnException e) {
            MessageHelper.showError("El ISBN ya existe: " + isbn);
            return false;
        } catch (DatabaseException e) {
            MessageHelper.showError("Error al crear el libro: " + e.getMessage());
            return false;
        } catch (Exception e) {
            MessageHelper.showError("Error inesperado: " + e.getMessage());
            return false;
        }
    }

    /**
     * View all books
     */
    public void viewAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();

            if (books.isEmpty()) {
                MessageHelper.showInfo("No hay libros registrados en el sistema");
                return;
            }

            String table = TableFormatter.formatBooksTable(books);
            MessageHelper.showScrollableData("Catálogo de Libros", table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al cargar los libros: " + e.getMessage());
        }
    }

    /**
     * View active books only
     */
    public void viewActiveBooks() {
        try {
            List<Book> books = bookService.getActiveBooks();

            if (books.isEmpty()) {
                MessageHelper.showInfo("No hay libros activos en el sistema");
                return;
            }

            String table = TableFormatter.formatBooksTable(books);
            MessageHelper.showScrollableData("Libros Activos", table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al cargar los libros: " + e.getMessage());
        }
    }

    /**
     * Search books by category
     */
    public void searchByCategory(String category) {
        try {
            if (InputValidator.isNullOrEmpty(category)) {
                MessageHelper.showError("La categoría es requerida");
                return;
            }

            List<Book> books = bookService.getBooksByCategory(category);

            if (books.isEmpty()) {
                MessageHelper.showInfo("No se encontraron libros en la categoría: " + category);
                return;
            }

            String table = TableFormatter.formatBooksTable(books);
            MessageHelper.showScrollableData("Libros - Categoría: " + category, table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al buscar libros: " + e.getMessage());
        }
    }

    /**
     * Search books by author
     */
    public void searchByAuthor(String author) {
        try {
            if (InputValidator.isNullOrEmpty(author)) {
                MessageHelper.showError("El autor es requerido");
                return;
            }

            List<Book> books = bookService.getBooksByAuthor(author);

            if (books.isEmpty()) {
                MessageHelper.showInfo("No se encontraron libros del autor: " + author);
                return;
            }

            String table = TableFormatter.formatBooksTable(books);
            MessageHelper.showScrollableData("Libros - Autor: " + author, table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al buscar libros: " + e.getMessage());
        }
    }

    /**
     * View book details
     */
    public void viewBookDetails(String isbn) {
        try {
            if (InputValidator.isNullOrEmpty(isbn)) {
                MessageHelper.showError("El ISBN es requerido");
                return;
            }

            Optional<Book> bookOpt = bookService.findBookByIsbn(isbn);

            if (bookOpt.isEmpty()) {
                MessageHelper.showError("Libro no encontrado con ISBN: " + isbn);
                return;
            }

            String details = TableFormatter.formatBookDetails(bookOpt.get());
            MessageHelper.showScrollableData("Detalles del Libro", details);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al buscar el libro: " + e.getMessage());
        }
    }

    /**
     * Update book
     */
    public boolean updateBook(String isbn, String title, String author, String category,
                              String totalCopiesStr, String availableCopiesStr, String referencePriceStr) {
        try {
            // Validate input
            if (!InputValidator.isPositiveInteger(totalCopiesStr)) {
                MessageHelper.showError("El número de ejemplares totales debe ser un entero positivo");
                return false;
            }

            if (!InputValidator.isNonNegativeInteger(availableCopiesStr)) {
                MessageHelper.showError("El número de ejemplares disponibles debe ser un entero no negativo");
                return false;
            }

            if (!InputValidator.isNonNegativeDouble(referencePriceStr)) {
                MessageHelper.showError("El precio debe ser un número positivo");
                return false;
            }

            Integer totalCopies = InputValidator.parseIntSafely(totalCopiesStr);
            Integer availableCopies = InputValidator.parseIntSafely(availableCopiesStr);
            Double referencePrice = InputValidator.parseDoubleSafely(referencePriceStr);

            // Get existing book
            Optional<Book> bookOpt = bookService.findBookByIsbn(isbn);
            if (bookOpt.isEmpty()) {
                MessageHelper.showError("Libro no encontrado");
                return false;
            }

            Book book = bookOpt.get();
            book.setTitle(title);
            book.setAuthor(author);
            book.setCategory(category);
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(availableCopies);
            book.setReferencePrice(referencePrice);

            boolean updated = bookService.updateBook(book);

            if (updated) {
                MessageHelper.showSuccess("Libro actualizado exitosamente");
            } else {
                MessageHelper.showError("No se pudo actualizar el libro");
            }

            return updated;

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al actualizar el libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Activate book
     */
    public boolean activateBook(String isbn) {
        try {
            boolean activated = bookService.activateBook(isbn);

            if (activated) {
                MessageHelper.showSuccess("Libro activado exitosamente");
            } else {
                MessageHelper.showError("No se pudo activar el libro");
            }

            return activated;

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al activar el libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deactivate book
     */
    public boolean deactivateBook(String isbn) {
        try {
            boolean deactivated = bookService.deactivateBook(isbn);

            if (deactivated) {
                MessageHelper.showSuccess("Libro desactivado exitosamente");
            } else {
                MessageHelper.showError("No se pudo desactivar el libro");
            }

            return deactivated;

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al desactivar el libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete book
     */
    public boolean deleteBook(String isbn) {
        try {
            if (!MessageHelper.showConfirmation("¿Está seguro de eliminar este libro?\nISBN: " + isbn)) {
                return false;
            }

            boolean deleted = bookService.deleteBook(isbn);

            if (deleted) {
                MessageHelper.showSuccess("Libro eliminado exitosamente");
            } else {
                MessageHelper.showError("No se pudo eliminar el libro");
            }

            return deleted;

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al eliminar el libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all books (for exports or other operations)
     */
    public List<Book> getAllBooks() throws DatabaseException {
        return bookService.getAllBooks();
    }
}