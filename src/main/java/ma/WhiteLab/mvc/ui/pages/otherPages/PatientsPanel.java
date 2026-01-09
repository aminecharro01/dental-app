package ma.WhiteLab.mvc.ui.pages.otherPages;

import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.mvc.controllers.modules.patient.api.PatientsController;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.service.modules.patient.api.PatientService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientsPanel extends JPanel {

    private final PatientsController controller;
    private final PatientService patientService;
    private final UserPrincipal principal;

    /**
     * ⚠️ La liste n’est PLUS final
     * Le controller reste propriétaire des données
     */
    private List<Patient> patients;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PatientsPanel(PatientsController controller,
                         PatientService patientService,
                         List<Patient> patients,
                         UserPrincipal principal) {

        this.controller = controller;
        this.patientService = patientService;
        this.patients = patients;
        this.principal = principal;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    // ================= HEADER =================

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Gestion des Patients");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(64, 120, 255));

        searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.putClientProperty("JTextField.placeholderText",
                "Rechercher par nom, prénom, téléphone ou email...");
        searchField.putClientProperty("JTextField.showClearButton", true);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { search(); }
            @Override public void removeUpdate(DocumentEvent e) { search(); }
            @Override public void changedUpdate(DocumentEvent e) {}

            private void search() {
                SwingUtilities.invokeLater(() ->
                        performSearch(searchField.getText().trim()));
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("🔍 "));
        searchPanel.add(searchField);

        header.add(title, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    // ================= TABLE =================

    private JScrollPane buildTablePanel() {

        String[] columns = {
                "ID", "Nom Prénom", "Date naissance",
                "Téléphone", "Email", "Sexe", "Assurance", "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(64, 120, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(60);
        cm.getColumn(1).setPreferredWidth(250);
        cm.getColumn(7).setCellRenderer(new ButtonColumnRenderer());

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        cm.getColumn(0).setCellRenderer(center);
        cm.getColumn(2).setCellRenderer(center);
        cm.getColumn(5).setCellRenderer(center);

        populateTable(patients);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (col == 7 && row >= 0) {
                    Object v = table.getValueAt(row, col);
                    if (v instanceof JButton btn) btn.doClick();
                }
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    // ================= DATA =================

    private void populateTable(List<Patient> list) {
        tableModel.setRowCount(0);

        if (list == null || list.isEmpty()) {
            tableModel.addRow(new Object[]{
                    "-", "Aucun patient trouvé", "-", "-", "-", "-", "-", "-"
            });
            return;
        }

        for (Patient p : list) {

            String fullName = Optional.ofNullable(p.getPrenom()).orElse("") +
                    " " + Optional.ofNullable(p.getNom()).orElse("").toUpperCase();

            JButton openBtn = new JButton("Ouvrir dossier");
            openBtn.setBackground(new Color(64, 120, 255));
            openBtn.setForeground(Color.WHITE);
            openBtn.setFocusPainted(false);
            openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            Long id = p.getId();
            openBtn.addActionListener(e ->
                    JOptionPane.showMessageDialog(
                            this,
                            "Ouverture du dossier du patient ID : " + id,
                            "Dossier patient",
                            JOptionPane.INFORMATION_MESSAGE));

            tableModel.addRow(new Object[]{
                    id,
                    fullName.trim(),
                    Optional.ofNullable(p.getDateNaissance())
                            .map(d -> d.format(DATE_FORMAT)).orElse("-"),
                    Optional.ofNullable(p.getTelephone()).orElse("-"),
                    Optional.ofNullable(p.getEmail()).orElse("-"),
                    Optional.ofNullable(p.getSexe()).map(Enum::name).orElse("-"),
                    Optional.ofNullable(p.getAssurance()).map(Enum::name).orElse("-"),
                    openBtn
            });
        }
    }

    private void performSearch(String query) {
        if (query.isBlank()) {
            populateTable(patients);
            return;
        }

        String q = query.toLowerCase();
        populateTable(
                patients.stream()
                        .filter(p ->
                                Optional.ofNullable(p.getNom()).orElse("").toLowerCase().contains(q) ||
                                        Optional.ofNullable(p.getPrenom()).orElse("").toLowerCase().contains(q) ||
                                        Optional.ofNullable(p.getTelephone()).orElse("").toLowerCase().contains(q) ||
                                        Optional.ofNullable(p.getEmail()).orElse("").toLowerCase().contains(q))
                        .collect(Collectors.toList())
        );
    }

    // ================= REFRESH =================

    /**
     * Appelée UNIQUEMENT par le controller
     */
    public void refreshData(List<Patient> newPatients) {
        this.patients = newPatients;
        searchField.setText("");
        populateTable(this.patients);
    }

    // ================= BUTTON RENDERER =================

    private static class ButtonColumnRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            if (value instanceof JButton btn) {
                btn.setBackground(isSelected
                        ? new Color(50, 100, 200)
                        : new Color(64, 120, 255));
                return btn;
            }
            return super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
        }
    }
}
