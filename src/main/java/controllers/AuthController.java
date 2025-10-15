package controllers;

import exceptions.InvalidCredentialsException;
import models.User;
import services.UserService;
import utils.InputValidator;
import utils.MessageHelper;

/**
 * Controller for authentication operations
 */
public class AuthController {

    private final UserService userService;

    public AuthController() {
        this.userService = new UserService();
    }

    /**
     * Handle user login
     */
    public User login(String username, String password) {
        try {
            // Validate input
            if (InputValidator.isNullOrEmpty(username) || InputValidator.isNullOrEmpty(password)) {
                MessageHelper.showError("Usuario y contraseña son requeridos");
                return null;
            }

            // Attempt login
            User user = userService.login(username, password);

            MessageHelper.showWelcome(user.getUsername(), user.getRole().name());
            return user;

        } catch (InvalidCredentialsException e) {
            MessageHelper.showError("Credenciales inválidas. Por favor, intente nuevamente.");
            return null;
        } catch (Exception e) {
            MessageHelper.showError("Error al iniciar sesión: " + e.getMessage());
            return null;
        }
    }

    /**
     * Handle user logout
     */
    public void logout() {
        userService.logout();
    }
}