package controllers;

import exceptions.DatabaseException;
import models.Book;
import models.Loan;
import utils.CSVExporter;
import utils.MessageHelper;

import java.util.List;

/**
 * Controller for export operations (CSV)
 */
public class ExportController {

    private final BookController bookController;
    private final LoanController loanController;

    public ExportController() {
        this.bookController = new BookController();
        this.loanController = new LoanController();
    }

    /**
     * Export complete book catalog to CSV
     */
    public void exportBooksCatalog() {
        try {
            List<Book> books = bookController.getAllBooks();

            if (books.isEmpty()) {
                MessageHelper.showWarning("No hay libros para exportar");
                return;
            }

            String filename = "libros_export.csv";
            boolean success = CSVExporter.exportBooksCatalog(books, filename);

            if (success) {
                MessageHelper.showSuccess("Catálogo exportado exitosamente:\n" +
                        "Archivo: " + filename + "\n" +
                        "Total de registros: " + books.size());
            } else {
                MessageHelper.showError("Error al exportar el catálogo");
            }

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al obtener los datos: " + e.getMessage());
        }
    }

    /**
     * Export overdue loans to CSV
     */
    public void exportOverdueLoans() {
        try {
            List<Loan> loans = loanController.getOverdueLoans();

            if (loans.isEmpty()) {
                MessageHelper.showInfo("No hay préstamos vencidos para exportar");
                return;
            }

            String filename = "prestamos_vencidos.csv";
            boolean success = CSVExporter.exportOverdueLoans(loans, filename);

            if (success) {
                MessageHelper.showSuccess("Préstamos vencidos exportados exitosamente:\n" +
                        "Archivo: " + filename + "\n" +
                        "Total de registros: " + loans.size());
            } else {
                MessageHelper.showError("Error al exportar los préstamos vencidos");
            }

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al obtener los datos: " + e.getMessage());
        }
    }

    /**
     * Export all loans to CSV
     */
    public void exportAllLoans() {
        try {
            List<Loan> loans = loanController.getAllLoans();

            if (loans.isEmpty()) {
                MessageHelper.showWarning("No hay préstamos para exportar");
                return;
            }

            String filename = "prestamos_export.csv";
            boolean success = CSVExporter.exportAllLoans(loans, filename);

            if (success) {
                MessageHelper.showSuccess("Préstamos exportados exitosamente:\n" +
                        "Archivo: " + filename + "\n" +
                        "Total de registros: " + loans.size());
            } else {
                MessageHelper.showError("Error al exportar los préstamos");
            }

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al obtener los datos: " + e.getMessage());
        }
    }
}