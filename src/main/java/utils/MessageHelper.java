package utils;

import javax.swing.*;

/**
 * Utility class for showing standardized JOptionPane messages
 */
public class MessageHelper {

    /**
     * Show success message
     */
    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show error message
     */
    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show warning message
     */
    public static void showWarning(String message) {
        JOptionPane.showMessageDialog(null, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show information message
     */
    public static void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show confirmation dialog
     */
    public static boolean showConfirmation(String message) {
        int option = JOptionPane.showConfirmDialog(null, message, "Confirmación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return option == JOptionPane.YES_OPTION;
    }

    /**
     * Show input dialog
     */
    public static String showInput(String message) {
        return JOptionPane.showInputDialog(null, message, "Entrada", JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Show input dialog with default value
     */
    public static String showInput(String message, String defaultValue) {
        return (String) JOptionPane.showInputDialog(null, message, "Entrada",
                JOptionPane.QUESTION_MESSAGE, null, null, defaultValue);
    }

    /**
     * Show option dialog
     */
    public static String showOptions(String message, String[] options) {
        return (String) JOptionPane.showInputDialog(null, message, "Seleccione una opción",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    /**
     * Show data in scrollable text area
     */
    public static void showScrollableData(String title, String data) {
        JTextArea textArea = new JTextArea(data);
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(700, 400));

        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show welcome message
     */
    public static void showWelcome(String username, String role) {
        String message = String.format("¡Bienvenido a LibroNova!\n\n" +
                        "Usuario: %s\n" +
                        "Rol: %s\n\n" +
                        "Sistema de Gestión de Bibliotecas",
                username, role);
        showInfo(message);
    }

    /**
     * Show about information
     */
    public static void showAbout() {
        String message = "╔════════════════════════════════════════╗\n" +
                "║         LibroNova v1.0.0               ║\n" +
                "╠════════════════════════════════════════╣\n" +
                "║  Sistema de Gestión de Bibliotecas     ║\n" +
                "║                                        ║\n" +
                "║  Desarrollado con:                     ║\n" +
                "║  • Java SE 17                          ║\n" +
                "║  • JDBC + MySQL                        ║\n" +
                "║  • JOptionPane                         ║\n" +
                "║  • Arquitectura por Capas              ║\n" +
                "║                                        ║\n" +
                "║   2024 Riwi - Módulo 5.1              ║\n" +
                "╚════════════════════════════════════════╝";

        showInfo(message);
    }
}