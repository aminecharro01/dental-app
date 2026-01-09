/*
package ma.WhiteLab.mvc.ui.modules.dashboard;


import ma.WhiteLab.mvc.ui.modules.dossierMedical.DossierMedicalView;
import ma.WhiteLab.mvc.ui.modules.medicament.MedicamentView;
import ma.WhiteLab.mvc.ui.modules.ordonnance.OrdonnanceView;
import ma.WhiteLab.mvc.ui.modules.dossierMedical.PrescriptionView;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.service.modules.dossierMedicale.api.DossierMedicalService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardView extends JFrame {

    private final UserPrincipal currentUser;
    private final DossierMedicalService dossierMedicalService;

    private JPanel contentPanel;
    private JLabel lblDate;

    // Couleurs Thème WhiteLab
    private final Color COLOR_PRIMARY = new Color(25, 118, 210);
    private final Color COLOR_SIDEBAR = new Color(33, 37, 41);
    private final Color COLOR_ACCENT = new Color(30, 140, 240);

    public DashboardView(UserPrincipal currentUser, DossierMedicalService dossierMedicalService) {
        this.currentUser = currentUser;
        this.dossierMedicalService = dossierMedicalService;

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeUI();
        startClock();
    }

    private void initializeUI() {
        setTitle("WhiteLab Management System - " + currentUser);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 247, 251));
        showWelcome();
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(getWidth(), 70));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        String rolesStr = currentUser.getRoles().stream()
                .map(r -> r.getLibelle().name())
                .reduce((r1, r2) -> r1 + ", " + r2)
                .orElse("Aucun rôle");

        JLabel lblUser = new JLabel("  👤  " + currentUser.getUsername().toUpperCase() + " [" + rolesStr + "]");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(COLOR_PRIMARY);
        lblUser.setBorder(new EmptyBorder(0, 20, 0, 0));

        lblDate = new JLabel();
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDate.setForeground(Color.GRAY);
        lblDate.setBorder(new EmptyBorder(0, 0, 0, 20));

        topBar.add(lblUser, BorderLayout.WEST);
        topBar.add(lblDate, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(new EmptyBorder(30, 15, 30, 15));

        JLabel lblLogo = new JLabel("🦷 WhiteLab");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(50));

        // Menu buttons
        addMenuButton(sidebar, "Tableau de bord", "🏠", this::showWelcome);

        if (currentUser.hasRole("MEDECIN") || currentUser.hasRole("ADMIN") || currentUser.hasRole("SECRETAIRE")) {
            addMenuButton(sidebar, "Patients & Dossiers", "📁", this::openDossierMedical);
        }

        if (currentUser.hasRole("ADMIN") || currentUser.hasRole("MEDECIN")) {
            addMenuButton(sidebar, "Médicaments", "💊", this::openMedicament);
            addMenuButton(sidebar, "Ordonnances", "📝", this::openOrdonnance);
            addMenuButton(sidebar, "Prescriptions", "💉", this::openPrescription);
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("  Se déconnecter  ");
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private void addMenuButton(JPanel sidebar, String text, String icon, Runnable action) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setMaximumSize(new Dimension(250, 50));
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(COLOR_SIDEBAR);
        btn.setForeground(new Color(200, 200, 200));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(50, 55, 60));
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_SIDEBAR);
                btn.setForeground(new Color(200, 200, 200));
            }
        });

        btn.addActionListener(e -> action.run());
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(10));
    }

    private void showWelcome() {
        updateContent(createWelcomePanel());
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 251));

        JLabel welcome = new JLabel("Bienvenue, " + currentUser.getUsername());
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcome.setForeground(COLOR_PRIMARY);

        JLabel sub = new JLabel("Système de gestion dentaire WhiteLab v2025");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        sub.setForeground(Color.GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        panel.add(welcome, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(sub, gbc);

        return panel;
    }

    private void updateContent(JPanel newContent) {
        contentPanel.removeAll();
        contentPanel.add(newContent, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void openDossierMedical() {
        DossierMedicalView dossierView = new DossierMedicalView(currentUser);
        new ma.WhiteLab.mvc.controllers.modules.dossierMedical.DossierMedicalController(
                dossierView,
                dossierMedicalService,
                currentUser
        );
        updateContent((JPanel) dossierView.getContentPane());
    }

    private void openMedicament() {
        MedicamentView view = new MedicamentView(currentUser);
        updateContent((JPanel) view.getContentPane());
    }

    private void openOrdonnance() {
        OrdonnanceView view = new OrdonnanceView(currentUser);
        updateContent((JPanel) view.getContentPane());
    }

    private void openPrescription() {
        PrescriptionView view = new PrescriptionView(currentUser);
        updateContent((JPanel) view.getContentPane());
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy - HH:mm");
        Timer timer = new Timer(1000, e -> lblDate.setText(LocalDateTime.now().format(formatter)));
        timer.start();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment quitter la session ?", "Déconnexion",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                new ma.WhiteLab.mvc.ui.modules.auth.LoginView().setVisible(true);
            });
        }
    }
}

 */