package views;

import controllers.ExportController;

import javax.swing.*;

/**
 * View for export data (use JOptionPane)
 */
public class ExportView {

    private final ExportController exportController;

    public ExportView(ExportController exportController) {
        this.exportController = exportController;
    }

    public void showMenu() {
        String option;
        do {
            option = JOptionPane.showInputDialog(null,
                    """
                      EXPORTACIÓN DE DATOS
                    
                    1. Exportar catálogo de libros
                    2. Exportar todos los préstamos
                    3. Exportar préstamos vencidos
                    0. Volver al menú principal
                    
                    Seleccione una opción:
                    """,
                    "Menú de Exportación", JOptionPane.PLAIN_MESSAGE);

            if (option == null) break; // cancelar

            switch (option) {
                case "1" -> exportController.exportBooksCatalog();
                case "2" -> exportController.exportAllLoans();
                case "3" -> exportController.exportOverdueLoans();
                case "0" -> JOptionPane.showMessageDialog(null, "Volviendo al menú principal...");
                default -> JOptionPane.showMessageDialog(null, "Opción inválida");
            }
        } while (!"0".equals(option));
    }
}
