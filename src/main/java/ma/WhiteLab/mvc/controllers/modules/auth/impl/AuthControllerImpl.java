package ma.WhiteLab.mvc.controllers.modules.auth.impl;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.mvc.controllers.dashboardModule.api.DashboardController;
import ma.WhiteLab.mvc.controllers.modules.auth.api.AuthController;
import ma.WhiteLab.mvc.dto.auth.AuthRequest;
import ma.WhiteLab.mvc.dto.auth.AuthResult;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.frames.AuthView;
import ma.WhiteLab.service.modules.auth.api.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    private AuthView view;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void showLoginView() {
        SwingUtilities.invokeLater(() -> {
            if (view == null || view.isDisposed()) {
                view = new AuthView(this);
            }
            view.clearErrors();
            view.setVisible(true);
            view.requestFocusInLoginField(); // Optionnel : focus sur le champ login
        });
    }

    @Override
    public void onLoginRequested(String login, String password) {
        String trimmedLogin = (login != null) ? login.trim() : "";
        String trimmedPassword = (password != null) ? password : "";

        if (trimmedLogin.isEmpty() || trimmedPassword.isEmpty()) {
            view.showFieldErrors(Map.of("login", "Veuillez remplir tous les champs"));
            return;
        }

        AuthResult result = authService.authenticate(new AuthRequest(trimmedLogin, trimmedPassword));

        // 1. Erreurs de validation du formulaire
        if (result.hasFieldErrors() && !result.getFieldErrors().isEmpty()) {
            view.showFieldErrors(result.getFieldErrors());
            return;
        }

        // 2. Échec d'authentification
        if (!result.isSuccess() || result.getUserPrincipal() == null) {
            JOptionPane.showMessageDialog(
                    view,
                    result.getMessage() != null ? result.getMessage() : "Identifiants incorrects",
                    "Échec de l'authentification",
                    JOptionPane.ERROR_MESSAGE
            );
            view.clearPasswordField(); // Sécurité : efface le mot de passe
            return;
        }

        // 3. Succès
        UserPrincipal principal = result.getUserPrincipal();

        JOptionPane.showMessageDialog(
                view,
                "Bienvenue, " + principal.fullName() + " !",
                "Connexion réussie",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Fermer proprement la fenêtre de login
        if (view != null) {
            view.dispose();
            view = null; // Permet de recréer une nouvelle vue au prochain login
        }

        // Ouvrir le dashboard
        onLoginSuccess(principal);
    }

    @Override
    public void onCancelRequested() {
        if (view == null) {
            System.exit(0);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Voulez-vous vraiment quitter l'application WhiteLab ?",
                "Confirmation de fermeture",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            view.dispose();
            System.exit(0);
        }
    }

    @Override
    public void onLoginSuccess(UserPrincipal principal) {
        SwingUtilities.invokeLater(() -> {
            DashboardController dashboardController = ApplicationContext.getInstance().getBean(DashboardController.class);

            if (dashboardController != null) {
                dashboardController.showDashboard(principal);
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Connexion réussie, " + principal.fullName() + " !\n" +
                                "Cependant, le tableau de bord n'est pas disponible.",
                        "Connexion réussie",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }
}