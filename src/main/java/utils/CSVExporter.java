package utils;

import config.LoggerConfig;
import models.Book;
import models.Loan;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting data to CSV files
 */
public class CSVExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Export complete book catalog to CSV
     */
    public static boolean exportBooksCatalog(List<Book> books, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            // Write CSV header
            writer.println("ISBN,Title,Author,Category,Total Copies,Available Copies,Reference Price,Is Active,Created At");

            // Write book data
            for (Book book : books) {
                writer.printf("%s,%s,%s,%s,%d,%d,%.2f,%s,%s%n",
                        escapeCsv(book.getIsbn()),
                        escapeCsv(book.getTitle()),
                        escapeCsv(book.getAuthor()),
                        escapeCsv(book.getCategory()),
                        book.getTotalCopies(),
                        book.getAvailableCopies(),
                        book.getReferencePrice(),
                        book.getIsActive() ? "ACTIVE" : "INACTIVE",
                        book.getCreatedAt().format(DATETIME_FORMATTER)
                );
            }

            LoggerConfig.logInfo("Books catalog exported successfully: " + filename + " (" + books.size() + " records)");
            return true;

        } catch (IOException e) {
            LoggerConfig.logError("Error exporting books catalog to CSV", e);
            return false;
        }
    }

    /**
     * Export overdue loans to CSV
     */
    public static boolean exportOverdueLoans(List<Loan> loans, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            // Write CSV header
            writer.println("Loan ID,ISBN,Member ID,Loan Date,Due Date,Days Overdue,Fine Amount,Status,Created At");

            // Write loan data
            for (Loan loan : loans) {
                writer.printf("%d,%s,%d,%s,%s,%d,%.2f,%s,%s%n",
                        loan.getIdLoan(),
                        escapeCsv(loan.getIsbn()),
                        loan.getIdMember(),
                        loan.getLoanDate().format(DATE_FORMATTER),
                        loan.getDueDate().format(DATE_FORMATTER),
                        loan.getDaysOverdue(),
                        loan.getFineAmount(),
                        loan.getStatus().name(),
                        loan.getCreatedAt().format(DATETIME_FORMATTER)
                );
            }

            LoggerConfig.logInfo("Overdue loans exported successfully: " + filename + " (" + loans.size() + " records)");
            return true;

        } catch (IOException e) {
            LoggerConfig.logError("Error exporting overdue loans to CSV", e);
            return false;
        }
    }

    /**
     * Export all loans to CSV
     */
    public static boolean exportAllLoans(List<Loan> loans, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            // Write CSV header
            writer.println("Loan ID,ISBN,Member ID,Loan Date,Due Date,Return Date,Fine Amount,Status,Created At");

            // Write loan data
            for (Loan loan : loans) {
                String returnDate = loan.getReturnDate() != null ?
                        loan.getReturnDate().format(DATE_FORMATTER) : "N/A";

                writer.printf("%d,%s,%d,%s,%s,%s,%.2f,%s,%s%n",
                        loan.getIdLoan(),
                        escapeCsv(loan.getIsbn()),
                        loan.getIdMember(),
                        loan.getLoanDate().format(DATE_FORMATTER),
                        loan.getDueDate().format(DATE_FORMATTER),
                        returnDate,
                        loan.getFineAmount(),
                        loan.getStatus().name(),
                        loan.getCreatedAt().format(DATETIME_FORMATTER)
                );
            }

            LoggerConfig.logInfo("All loans exported successfully: " + filename + " (" + loans.size() + " records)");
            return true;

        } catch (IOException e) {
            LoggerConfig.logError("Error exporting all loans to CSV", e);
            return false;
        }
    }

    /**
     * Escape special characters in CSV fields
     */
    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        // If value contains comma, quote, or newline, wrap in quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // Escape existing quotes by doubling them
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }

        return value;
    }
}