package controllers;

import exceptions.*;
import models.Loan;
import services.LoanService;
import utils.InputValidator;
import utils.MessageHelper;
import utils.TableFormatter;

import java.util.List;
import java.util.Optional;

/**
 * Controller for loan operations
 */
public class LoanController {

    private final LoanService loanService;

    public LoanController() {
        this.loanService = new LoanService();
    }

    /**
     * Create a new loan
     */
    public boolean createLoan(String isbn, String memberIdStr) {
        try {
            // Validate input
            if (InputValidator.isNullOrEmpty(isbn)) {
                MessageHelper.showError("El ISBN es requerido");
                return false;
            }

            if (!InputValidator.isPositiveInteger(memberIdStr)) {
                MessageHelper.showError("ID de socio inválido");
                return false;
            }

            Integer memberId = InputValidator.parseIntSafely(memberIdStr);

            // Create loan
            Loan loan = loanService.createLoan(isbn, memberId);

            MessageHelper.showSuccess("Préstamo creado exitosamente:\n" +
                    "ID Préstamo: " + loan.getIdLoan() + "\n" +
                    "ISBN: " + loan.getIsbn() + "\n" +
                    "Fecha de vencimiento: " + loan.getDueDate());
            return true;

        } catch (InsufficientStockException e) {
            MessageHelper.showError("No hay ejemplares disponibles del libro");
            return false;
        } catch (InactiveMemberException e) {
            MessageHelper.showError("El socio está inactivo y no puede realizar préstamos");
            return false;
        } catch (EntityNotFoundException e) {
            MessageHelper.showError(e.getMessage());
            return false;
        } catch (InvalidLoanException e) {
            MessageHelper.showError("Préstamo inválido: " + e.getMessage());
            return false;
        } catch (DatabaseException e) {
            MessageHelper.showError("Error al crear el préstamo: " + e.getMessage());
            return false;
        } catch (Exception e) {
            MessageHelper.showError("Error inesperado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return a loan
     */
    public boolean returnLoan(String loanIdStr) {
        try {
            // Validate input
            if (!InputValidator.isPositiveInteger(loanIdStr)) {
                MessageHelper.showError("ID de préstamo inválido");
                return false;
            }

            Integer loanId = InputValidator.parseIntSafely(loanIdStr);

            // Get loan details before returning
            Optional<Loan> loanOpt = loanService.findLoanById(loanId);
            if (loanOpt.isEmpty()) {
                MessageHelper.showError("Préstamo no encontrado");
                return false;
            }

            Loan loan = loanOpt.get();
            double fine = loanService.calculateFine(loan);

            // Show fine warning if applicable
            if (fine > 0) {
                String message = String.format("Este préstamo tiene una multa de $%.2f\n" +
                                "Días de retraso: %d\n\n" +
                                "¿Desea continuar con la devolución?",
                        fine, loan.getDaysOverdue());
                if (!MessageHelper.showConfirmation(message)) {
                    return false;
                }
            }

            // Return loan
            loanService.returnLoan(loanId);

            String message = "Préstamo devuelto exitosamente:\n" +
                    "ID Préstamo: " + loanId;
            if (fine > 0) {
                message += "\n" + String.format("Multa: $%.2f", fine);
            }

            MessageHelper.showSuccess(message);
            return true;

        } catch (EntityNotFoundException e) {
            MessageHelper.showError("Préstamo no encontrado");
            return false;
        } catch (InvalidLoanException e) {
            MessageHelper.showError("El préstamo ya fue devuelto previamente");
            return false;
        } catch (DatabaseException e) {
            MessageHelper.showError("Error al devolver el préstamo: " + e.getMessage());
            return false;
        } catch (Exception e) {
            MessageHelper.showError("Error inesperado: " + e.getMessage());
            return false;
        }
    }

    /**
     * View all loans
     */
    public void viewAllLoans() {
        try {
            List<Loan> loans = loanService.getAllLoans();

            if (loans.isEmpty()) {
                MessageHelper.showInfo("No hay préstamos registrados en el sistema");
                return;
            }

            String table = TableFormatter.formatLoansTable(loans);
            MessageHelper.showScrollableData("Lista de Préstamos", table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al cargar los préstamos: " + e.getMessage());
        }
    }

    /**
     * View active loans
     */
    public void viewActiveLoans() {
        try {
            List<Loan> loans = loanService.getActiveLoans();

            if (loans.isEmpty()) {
                MessageHelper.showInfo("No hay préstamos activos en el sistema");
                return;
            }

            String table = TableFormatter.formatLoansTable(loans);
            MessageHelper.showScrollableData("Préstamos Activos", table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al cargar los préstamos: " + e.getMessage());
        }
    }

    /**
     * View overdue loans
     */
    public void viewOverdueLoans() {
        try {
            List<Loan> loans = loanService.getOverdueLoans();

            if (loans.isEmpty()) {
                MessageHelper.showInfo("No hay préstamos vencidos");
                return;
            }

            String table = TableFormatter.formatLoansTable(loans);
            MessageHelper.showScrollableData("Préstamos Vencidos", table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al cargar los préstamos vencidos: " + e.getMessage());
        }
    }

    /**
     * View loans by member
     */
    public void viewLoansByMember(String memberIdStr) {
        try {
            if (!InputValidator.isPositiveInteger(memberIdStr)) {
                MessageHelper.showError("ID de socio inválido");
                return;
            }

            Integer memberId = InputValidator.parseIntSafely(memberIdStr);
            List<Loan> loans = loanService.getLoansByMember(memberId);

            if (loans.isEmpty()) {
                MessageHelper.showInfo("No se encontraron préstamos para este socio");
                return;
            }

            String table = TableFormatter.formatLoansTable(loans);
            MessageHelper.showScrollableData("Préstamos del Socio ID: " + memberId, table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al buscar préstamos: " + e.getMessage());
        }
    }

    /**
     * View loan details
     */
    public void viewLoanDetails(String loanIdStr) {
        try {
            if (!InputValidator.isPositiveInteger(loanIdStr)) {
                MessageHelper.showError("ID de préstamo inválido");
                return;
            }

            Integer loanId = InputValidator.parseIntSafely(loanIdStr);
            Optional<Loan> loanOpt = loanService.findLoanById(loanId);

            if (loanOpt.isEmpty()) {
                MessageHelper.showError("Préstamo no encontrado con ID: " + loanId);
                return;
            }

            String details = TableFormatter.formatLoanDetails(loanOpt.get());
            MessageHelper.showScrollableData("Detalles del Préstamo", details);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al buscar el préstamo: " + e.getMessage());
        }
    }

    /**
     * Get all loans (for exports or other operations)
     */
    public List<Loan> getAllLoans() throws DatabaseException {
        return loanService.getAllLoans();
    }

    /**
     * Get overdue loans (for CSV export)
     */
    public List<Loan> getOverdueLoans() throws DatabaseException {
        return loanService.getOverdueLoans();
    }
}