package ma.WhiteLab.mvc.ui.frames;

import ma.WhiteLab.mvc.controllers.modules.auth.api.AuthController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Map;

public class AuthView extends JFrame {

    private final AuthController controller;

    private JTextField emailField;
    private JPasswordField passwordField;

    private JLabel lblErrorEmail;
    private JLabel lblErrorPassword;
    private JLabel lblErrorGlobal;

    private JButton btnLogin;
    private JButton btnCancel;

    public AuthView(AuthController controller) {
        this.controller = controller;

        initializeWindow();
        setContentPane(buildRootPanel());
        installKeyBindings();

        // Focus sur le champ email au démarrage
        SwingUtilities.invokeLater(() -> emailField.requestFocusInWindow());
    }

    private void initializeWindow() {
        setTitle("WhiteLab - Authentification");
        setSize(520, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        // Icône de l'application (fenêtre)
        ImageIcon appIcon = loadIcon("/static/icons/Logo.png", 64, 64);
        if (appIcon != null) {
            setIconImage(appIcon.getImage());
        }
    }

    // ======================== ACTIONS ========================

    private void onLoginAction(ActionEvent e) {
        clearErrors();
        String email = emailField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        controller.onLoginRequested(email, password);

        // Efface le mot de passe pour sécurité
        passwordField.setText("");
    }

    private void onCancelAction(ActionEvent e) {
        controller.onCancelRequested();
    }

    // ======================== API VIEW ← CONTROLLER ========================

    public void showFieldErrors(Map<String, String> errors) {
        if (errors == null || errors.isEmpty()) {
            clearErrors();
            return;
        }

        String emailError = errors.getOrDefault("login", "");
        String passwordError = errors.getOrDefault("password", "");
        String globalError = errors.getOrDefault("_global", "");

        lblErrorEmail.setText(emailError);
        lblErrorPassword.setText(passwordError);
        lblErrorGlobal.setText(globalError);

        // Bordures rouges pour les champs en erreur
        emailField.setBorder(emailError.isEmpty()
                ? UIManager.getBorder("TextField.border")
                : BorderFactory.createLineBorder(new Color(220, 53, 69), 2));

        passwordField.setBorder(passwordError.isEmpty()
                ? UIManager.getBorder("PasswordField.border")
                : BorderFactory.createLineBorder(new Color(220, 53, 69), 2));
    }

    public void clearErrors() {
        lblErrorEmail.setText("");
        lblErrorPassword.setText("");
        lblErrorGlobal.setText("");

        emailField.setBorder(UIManager.getBorder("TextField.border"));
        passwordField.setBorder(UIManager.getBorder("PasswordField.border"));
    }

    public void clearPasswordField() {
        passwordField.setText("");
    }

    public void requestFocusInLoginField() {
        emailField.requestFocusInWindow();
    }

    public boolean isDisposed() {
        return !isDisplayable();
    }

    // ======================== UI CONSTRUCTION ========================

    private JPanel buildRootPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(20, 30, 40, 30));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenterForm(), BorderLayout.CENTER);
        root.add(buildFooterButtons(), BorderLayout.SOUTH);

        return root;
    }

    private JPanel buildHeader() {
        JLabel logo = new JLabel();
        ImageIcon icon = loadIcon("/static/icons/Logo.png", 140, 140);
        if (icon != null) {
            logo.setIcon(icon);
        } else {
            logo.setText("WhiteLab");
            logo.setFont(new Font("Segoe UI", Font.BOLD, 42));
            logo.setForeground(new Color(64, 120, 255));
        }
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 40, 0));
        header.add(logo, BorderLayout.CENTER);

        JLabel subtitle = new JLabel("Gestion de cabinet médical");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(Color.GRAY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(subtitle, BorderLayout.SOUTH);

        return header;
    }

    private JPanel buildCenterForm() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Email
        JPanel rowEmail = createInputRow("/static/icons/user.png", "Email ou login");
        emailField = (JTextField) rowEmail.getComponent(1);
        center.add(rowEmail);
        center.add(Box.createVerticalStrut(8));
        lblErrorEmail = createErrorLabel();
        center.add(lblErrorEmail);
        center.add(Box.createVerticalStrut(20));

        // Password
        JPanel rowPass = createInputRow("/static/icons/pass.png", "Mot de passe");
        passwordField = (JPasswordField) rowPass.getComponent(1);
        center.add(rowPass);
        center.add(Box.createVerticalStrut(8));
        lblErrorPassword = createErrorLabel();
        center.add(lblErrorPassword);
        center.add(Box.createVerticalStrut(30));

        // Global error
        lblErrorGlobal = createErrorLabel();
        lblErrorGlobal.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(lblErrorGlobal);

        return center;
    }

    private JPanel createInputRow(String iconPath, String placeholder) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Icône
        JLabel iconLabel = new JLabel(loadIcon(iconPath, 32, 32));
        iconLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Champ de texte
        JComponent field = placeholder.contains("Mot de passe")
                ? new JPasswordField()
                : new JTextField();

        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JTextField.showClearButton", true);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        // Valeurs de test pour développement
        if (placeholder.contains("Email")) {
            ((JTextField) field).setText("ali.medecin@example.com");
        } else if (placeholder.contains("Mot de passe")) {
            ((JPasswordField) field).setText("Medecin@2025");
        }

        row.add(iconLabel, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);

        return row;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(220, 53, 69));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel buildFooterButtons() {
        btnLogin = createStyledButton("Se connecter", "/static/icons/connect.png", new Color(64, 120, 255));
        btnLogin.addActionListener(this::onLoginAction);

        btnCancel = createStyledButton("Annuler", "/static/icons/cancel.png", new Color(108, 117, 125));
        btnCancel.addActionListener(this::onCancelAction);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));
        footer.add(btnLogin);
        footer.add(btnCancel);

        return footer;
    }

    private JButton createStyledButton(String text, String iconPath, Color bgColor) {
        JButton btn = new JButton(text);
        ImageIcon icon = loadIcon(iconPath, 32, 32);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(12);
        }

        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(14, 40, 14, 40));
        btn.setPreferredSize(new Dimension(220, 56));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    // ======================== UTILS ========================

    private ImageIcon loadIcon(String path, int width, int height) {
        URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon original = new ImageIcon(url);
            Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        System.err.println("Icône non trouvée : " + path);
        return null;
    }

    private void installKeyBindings() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "LOGIN");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");

        actionMap.put("LOGIN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoginAction(e);
            }
        });

        actionMap.put("CANCEL", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancelAction(e);
            }
        });

        getRootPane().setDefaultButton(btnLogin);
    }
}