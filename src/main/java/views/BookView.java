package views;

import controllers.BookController;

import javax.swing.*;

/**
 * Vista para la gestión de libros (usa JOptionPane)
 */
public class BookView {

    private final BookController bookController;

    public BookView(BookController bookController) {
        this.bookController = bookController;
    }

    public void showMenu() {
        String option;
        do {
            option = JOptionPane.showInputDialog(null,
                    """
                      GESTIÓN DE LIBROS
                    
                    1. Registrar libro
                    2. Ver todos los libros
                    3. Ver libros activos
                    4. Buscar por categoría
                    5. Buscar por autor
                    6. Ver detalles de un libro
                    7. Actualizar libro
                    8. Activar libro
                    9. Desactivar libro
                    10. Eliminar libro
                    0. Volver al menú principal
                    
                    Seleccione una opción:
                    """,
                    "Menú de Libros", JOptionPane.PLAIN_MESSAGE);

            if (option == null) break; // cancelar

            switch (option) {
                case "1" -> createBook();
                case "2" -> bookController.viewAllBooks();
                case "3" -> bookController.viewActiveBooks();
                case "4" -> searchByCategory();
                case "5" -> searchByAuthor();
                case "6" -> viewBookDetails();
                case "7" -> updateBook();
                case "8" -> activateBook();
                case "9" -> deactivateBook();
                case "10" -> deleteBook();
                case "0" -> JOptionPane.showMessageDialog(null, "Volviendo al menú principal...");
                default -> JOptionPane.showMessageDialog(null, "Opción inválida");
            }
        } while (!"0".equals(option));
    }

    private void createBook() {
        String isbn = JOptionPane.showInputDialog("Ingrese el ISBN:");
        String title = JOptionPane.showInputDialog("Ingrese el título:");
        String author = JOptionPane.showInputDialog("Ingrese el autor:");
        String category = JOptionPane.showInputDialog("Ingrese la categoría:");
        String totalCopies = JOptionPane.showInputDialog("Ingrese el número total de ejemplares:");
        String price = JOptionPane.showInputDialog("Ingrese el precio de referencia:");

        if (isbn == null || title == null || author == null || category == null
                || totalCopies == null || price == null) return;

        bookController.createBook(isbn, title, author, category, totalCopies, price);
    }

    private void searchByCategory() {
        String category = JOptionPane.showInputDialog("Ingrese la categoría a buscar:");
        if (category != null) bookController.searchByCategory(category);
    }

    private void searchByAuthor() {
        String author = JOptionPane.showInputDialog("Ingrese el autor a buscar:");
        if (author != null) bookController.searchByAuthor(author);
    }

    private void viewBookDetails() {
        String isbn = JOptionPane.showInputDialog("Ingrese el ISBN del libro:");
        if (isbn != null) bookController.viewBookDetails(isbn);
    }

    private void updateBook() {
        String isbn = JOptionPane.showInputDialog("Ingrese el ISBN del libro a actualizar:");
        if (isbn == null) return;

        String title = JOptionPane.showInputDialog("Nuevo título:");
        String author = JOptionPane.showInputDialog("Nuevo autor:");
        String category = JOptionPane.showInputDialog("Nueva categoría:");
        String totalCopies = JOptionPane.showInputDialog("Total de ejemplares:");
        String availableCopies = JOptionPane.showInputDialog("Ejemplares disponibles:");
        String price = JOptionPane.showInputDialog("Nuevo precio de referencia:");

        if (title == null || author == null || category == null ||
                totalCopies == null || availableCopies == null || price == null)
            return;

        bookController.updateBook(isbn, title, author, category, totalCopies, availableCopies, price);
    }

    private void activateBook() {
        String isbn = JOptionPane.showInputDialog("Ingrese el ISBN del libro a activar:");
        if (isbn != null) bookController.activateBook(isbn);
    }

    private void deactivateBook() {
        String isbn = JOptionPane.showInputDialog("Ingrese el ISBN del libro a desactivar:");
        if (isbn != null) bookController.deactivateBook(isbn);
    }

    private void deleteBook() {
        String isbn = JOptionPane.showInputDialog("Ingrese el ISBN del libro a eliminar:");
        if (isbn != null) bookController.deleteBook(isbn);
    }
}
