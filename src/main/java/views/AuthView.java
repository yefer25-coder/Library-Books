package views;

import controllers.AuthController;
import models.User;

import javax.swing.*;

/**
 * Vista para autenticación (login/logout) usando JOptionPane
 */
public class AuthView {

    private final AuthController authController;

    public AuthView(AuthController authController) {
        this.authController = authController;
    }

    /**
     * show formulate of login.
     * .
     */
    public User showLogin() {
        User loggedUser = null;

        do {
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            Object[] message = {
                    "Usuario:", usernameField,
                    "Contraseña:", passwordField
            };

            int option = JOptionPane.showConfirmDialog(
                    null,
                    message,
                    "Iniciar sesión",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Inicio de sesión cancelado");
                return null;
            }

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            loggedUser = authController.login(username, password);

            if (loggedUser == null) {
                int retry = JOptionPane.showConfirmDialog(
                        null,
                        "Credenciales inválidas.\n¿Desea intentarlo nuevamente?",
                        "Error de autenticación",
                        JOptionPane.YES_NO_OPTION
                );
                if (retry != JOptionPane.YES_OPTION) {
                    return null;
                }
            }

        } while (loggedUser == null);

        return loggedUser;
    }

    /**
     * Cierra la sesión actual.
     */
    public void logout() {
        authController.logout();
        JOptionPane.showMessageDialog(null, "Sesión cerrada exitosamente");
    }
}
