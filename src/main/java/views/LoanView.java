package views;

import controllers.LoanController;

import javax.swing.*;

/**
 * Vista para gestionar préstamos (LoanView)
 */
public class LoanView {

    private final LoanController loanController;

    public LoanView(LoanController loanController) {
        this.loanController = loanController;
    }

    /**
     * Muestra el menú principal de préstamos
     */
    public void showMenu() {
        String option;
        do {
            option = JOptionPane.showInputDialog(null,
                    """
                      GESTIÓN DE PRÉSTAMOS
                    
                    1️⃣  Registrar nuevo préstamo
                    2️⃣  Devolver préstamo
                    3️⃣  Ver todos los préstamos
                    4️⃣  Ver préstamos activos
                    5️⃣  Ver préstamos vencidos
                    6️⃣  Ver préstamos por socio
                    7️⃣  Ver detalles de préstamo
                    0️⃣  Volver al menú principal
                    """,
                    "Menú de Préstamos", JOptionPane.QUESTION_MESSAGE);

            if (option == null) return; // si el usuario cierra la ventana

            switch (option) {
                case "1" -> createLoan();
                case "2" -> returnLoan();
                case "3" -> loanController.viewAllLoans();
                case "4" -> loanController.viewActiveLoans();
                case "5" -> loanController.viewOverdueLoans();
                case "6" -> viewLoansByMember();
                case "7" -> viewLoanDetails();
                case "0" -> JOptionPane.showMessageDialog(null, "Regresando al menú principal...");
                default -> JOptionPane.showMessageDialog(null, "Opción inválida. Intente nuevamente.");
            }

        } while (!"0".equals(option));
    }

    /**
     * Registrar un nuevo préstamo
     */
    private void createLoan() {
        String isbn = JOptionPane.showInputDialog("Ingrese el ISBN del libro:");
        if (isbn == null) return;

        String memberId = JOptionPane.showInputDialog("Ingrese el ID del socio:");
        if (memberId == null) return;

        loanController.createLoan(isbn.trim(), memberId.trim());
    }

    /**
     * Devolver un préstamo existente
     */
    private void returnLoan() {
        String loanId = JOptionPane.showInputDialog("Ingrese el ID del préstamo a devolver:");
        if (loanId == null) return;

        loanController.returnLoan(loanId.trim());
    }

    /**
     * Ver préstamos por socio
     */
    private void viewLoansByMember() {
        String memberId = JOptionPane.showInputDialog("Ingrese el ID del socio:");
        if (memberId == null) return;

        loanController.viewLoansByMember(memberId.trim());
    }

    /**
     * Ver detalles de un préstamo específico
     */
    private void viewLoanDetails() {
        String loanId = JOptionPane.showInputDialog("Ingrese el ID del préstamo:");
        if (loanId == null) return;

        loanController.viewLoanDetails(loanId.trim());
    }
}
