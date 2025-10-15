package app;

import controllers.*;
import models.User;
import views.*;

import javax.swing.*;

/**
 * Clase principal de la aplicación de gestión de biblioteca
 * Integra autenticación, gestión de libros, socios, préstamos y exportaciones.
 */
public class App {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        new App().start();
    }

    private final AuthController authController;
    private final BookController bookController;
    private final MemberController memberController;
    private final LoanController loanController;
    private final ExportController exportController;

    private final AuthView authView;
    private final BookView bookView;
    private final MemberView memberView;
    private final LoanView loanView;
    private final ExportView exportView;

    public App() {

        this.authController = new AuthController();
        this.bookController = new BookController();
        this.memberController = new MemberController();
        this.loanController = new LoanController();
        this.exportController = new ExportController();


        this.authView = new AuthView(authController);
        this.bookView = new BookView(bookController);
        this.memberView = new MemberView(memberController);
        this.loanView = new LoanView(loanController);
        this.exportView = new ExportView(exportController);
    }

    /**
     * Inicia la aplicación: muestra login y luego menú principal
     */
    public void start() {
        JOptionPane.showMessageDialog(null,
                " Bienvenido al Sistema de Gestión de Biblioteca",
                "Biblioteca - Inicio", JOptionPane.INFORMATION_MESSAGE);

        // Login
        User loggedUser = authView.showLogin();
        if (loggedUser == null) {
            JOptionPane.showMessageDialog(null, "Aplicación finalizada.");
            System.exit(0);
        }

        // Mostrar menú principal según rol
        showMainMenu(loggedUser);
    }

    /**
     * Menú principal según el rol del usuario
     */
    private void showMainMenu(User user) {
        String option;

        do {
            option = JOptionPane.showInputDialog(null,
                    String.format("""
                              Bienvenido, %s (%s)
                            
                              MENÚ PRINCIPAL
                            
                            1. Gestión de Libros
                            2. Gestión de Socios
                            3. Gestión de Préstamos
                            4. Exportar Datos
                            9. Cerrar Sesión
                            0. Salir
                            
                            Seleccione una opción:
                            """, user.getUsername(), user.getRole()),
                    "Menú Principal", JOptionPane.PLAIN_MESSAGE);

            if (option == null) break;

            switch (option) {
                case "1" -> bookView.showMenu();
                case "2" -> memberView.showMenu();
                case "3" -> loanView.showMenu();
                case "4" -> exportView.showMenu();
                case "9" -> {
                    authView.logout();
                    start();
                    return;
                }
                case "0" -> {
                    JOptionPane.showMessageDialog(null, " Gracias por usar el sistema. ¡Hasta pronto!");
                    System.exit(0);
                }
                default -> JOptionPane.showMessageDialog(null, " Opción inválida. Intente de nuevo.");
            }

        } while (true);
    }
}
