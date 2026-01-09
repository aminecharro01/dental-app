package ma.WhiteLab.mvc.ui.pages.otherPages;

import ma.WhiteLab.entities.dossierMedical.DossierMedical;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.mvc.controllers.modules.dossierMedicale.api.DossiersController;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.service.modules.dossierMedicale.api.DossierMedicalService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class DossiersPanel extends JPanel {

    private final DossiersController controller;
    private final DossierMedicalService dossierService;
    private final UserPrincipal principal;
    private final List<DossierMedical> dossiers;

    private JTable table;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DossiersPanel(DossiersController controller,
                         DossierMedicalService dossierService,
                         List<DossierMedical> dossiers,
                         UserPrincipal principal) {
        this.controller = controller;
        this.dossierService = dossierService;
        this.dossiers = dossiers;
        this.principal = principal;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Dossiers Médicaux");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(64, 120, 255));

        JTextField searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher par nom patient ou historique...");
        searchField.putClientProperty("JTextField.showClearButton", true);

        searchField.addActionListener(e -> performSearch(searchField.getText().trim()));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("🔍"));
        searchPanel.add(searchField);

        header.add(title, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {
                "Patient",
                "Médecin",
                "Date création",
                "Dernière MàJ",
                "Aperçu historique",
                "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(50);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(64, 120, 255));
        table.getTableHeader().setForeground(Color.WHITE);

        // Centrer les colonnes de dates
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Rendre les boutons visibles
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonColumnRenderer());

        populateTable(dossiers);

        // === AJOUT CRITIQUE : Gestion du clic sur le bouton dans la table ===
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());

                if (column == 5 && row >= 0) { // Colonne Actions
                    Object value = table.getValueAt(row, column);
                    if (value instanceof JButton button) {
                        button.doClick(); // Exécute l'action du bouton
                    }
                }
            }
        });

        return new JScrollPane(table);
    }

    private void populateTable(List<DossierMedical> list) {
        tableModel.setRowCount(0);

        if (list == null || list.isEmpty()) {
            tableModel.addRow(new Object[]{"Aucun dossier médical trouvé", "", "", "", "", ""});
            return;
        }

        for (DossierMedical dossier : list) {
            Patient patient = dossier.getPat();
            Medecin medecin = dossier.getMedecine();

            // === PATIENT NAME - SAFE ===
            String patientName = Optional.ofNullable(patient)
                    .map(p -> {
                        String prenom = Optional.ofNullable(p.getPrenom()).orElse("");
                        String nom = Optional.ofNullable(p.getNom()).map(String::toUpperCase).orElse("");
                        String full = (prenom + " " + nom).trim();
                        return full.isBlank() ? "Patient inconnu" : full;
                    })
                    .orElse("Patient inconnu");

            // === MÉDECIN NAME - SAFE ===
            String medecinName = Optional.ofNullable(medecin)
                    .map(m -> {
                        String prenom = Optional.ofNullable(m.getPrenom()).orElse("");
                        String nom = Optional.ofNullable(m.getNom()).map(String::toUpperCase).orElse("");
                        String full = (prenom + " " + nom).trim();
                        return full.isBlank() ? "Médecin inconnu" : full;
                    })
                    .orElse("Médecin inconnu");

            // === APERÇU HISTORIQUE - SAFE ===
            String apercu = Optional.ofNullable(dossier.getHistorique())
                    .filter(h -> !h.isBlank())
                    .map(h -> truncate(h, 80))
                    .orElse("<Aucun historique>");

            // === BOUTON OUVRIR ===
            JButton openBtn = new JButton("Ouvrir");
            openBtn.setBackground(new Color(64, 120, 255));
            openBtn.setForeground(Color.WHITE);
            openBtn.setFocusPainted(false);
            openBtn.setBorderPainted(true);
            openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            Long dossierId = dossier.getId();
            openBtn.addActionListener(e -> {
                JPanel detailPanel = controller.getDossierDetailView(dossierId, principal);

                SwingUtilities.invokeLater(() -> {
                    JPanel parent = (JPanel) DossiersPanel.this.getParent();
                    if (parent != null) {
                        parent.removeAll();
                        parent.add(detailPanel, BorderLayout.CENTER);
                        parent.revalidate();
                        parent.repaint();
                    }
                });
            });

            tableModel.addRow(new Object[]{
                    patientName,
                    medecinName,
                    dossier.getDateCreation().format(DATE_FORMAT),
                    dossier.getDateMiseAJour() != null ? dossier.getDateMiseAJour().format(DATE_FORMAT) : "-",
                    apercu,
                    openBtn  // Le vrai bouton est passé ici
            });
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text != null ? text : "";
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    private void performSearch(String searchQuery) {
        if (searchQuery == null || searchQuery.isBlank()) {
            populateTable(dossiers);
            return;
        }

        String lowerQuery = searchQuery.toLowerCase().trim();

        List<DossierMedical> filtered = dossiers.stream()
                .filter(d -> {
                    Patient p = d.getPat();
                    if (p == null) return false;

                    String prenom = Optional.ofNullable(p.getPrenom()).orElse("");
                    String nom = Optional.ofNullable(p.getNom()).orElse("");
                    String patientName = (prenom + " " + nom).toLowerCase().trim();

                    String historique = Optional.ofNullable(d.getHistorique())
                            .orElse("")
                            .toLowerCase();

                    return patientName.contains(lowerQuery) || historique.contains(lowerQuery);
                })
                .toList();

        populateTable(filtered);
    }

    // ====================== RENDERER POUR AFFICHER LES BOUTONS ======================
    private static class ButtonColumnRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value instanceof JButton button) {
                // Style cohérent avec le reste de l'app
                if (isSelected) {
                    button.setBackground(new Color(50, 100, 200));
                } else {
                    button.setBackground(new Color(64, 120, 255));
                }
                return button;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}