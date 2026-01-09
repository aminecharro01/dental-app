package ma.WhiteLab.mvc.ui.pages.otherPages;

import ma.WhiteLab.entities.agenda.RendezVous;
import ma.WhiteLab.entities.dossierMedical.*;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.service.modules.dossierMedicale.api.DossierMedicalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DossierDetailPanel extends JPanel {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UserPrincipal principal; // Stocké pour utilisation dans le bouton "Enregistrer"

    public DossierDetailPanel(DossierMedical dossier, DossierMedicalService service, UserPrincipal principal) {
        this.principal = principal;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Patient patient = dossier.getPat();
        Medecin medecin = dossier.getMedecine();

        add(buildHeader(patient, medecin, dossier), BorderLayout.NORTH);
        add(buildTabbedPane(dossier, service), BorderLayout.CENTER);
    }

    private JPanel buildHeader(Patient patient, Medecin medecin, DossierMedical dossier) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel title = new JLabel("Dossier Médical - " + getPatientFullName(patient));
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(64, 120, 255));

        String medecinName = medecin != null ? getMedecinFullName(medecin) : "Non assigné";
        String creation = "Créé le : " + formatDateTime(dossier.getDateCreation());
        String maj = dossier.getDateMiseAJour() != null
                ? " | Mis à jour le : " + formatDateTime(dossier.getDateMiseAJour())
                : "";

        JLabel info = new JLabel("<html><b>Médecin :</b> " + medecinName + " &nbsp;&nbsp; " + creation + maj + "</html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        info.setForeground(Color.DARK_GRAY);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        infoPanel.add(info);

        header.add(title, BorderLayout.NORTH);
        header.add(infoPanel, BorderLayout.SOUTH);

        return header;
    }

    private JTabbedPane buildTabbedPane(DossierMedical dossier, DossierMedicalService service) {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));

        List<Consultation> consultations = nullSafeList(service.getConsultations(dossier.getId()));
        List<Ordonnance> ordonnances = nullSafeList(service.getOrdonnances(dossier.getId()));
        List<Certificat> certificats = nullSafeList(service.getCertificats(dossier.getId()));
        List<Antecedent> antecedents = nullSafeList(service.getAntecedents(dossier.getId()));
        List<RendezVous> rendezVous = nullSafeList(service.getRendezVous(dossier.getId()));

        tabs.addTab("Historique Général", buildHistoriquePanel(dossier, service));
        tabs.addTab("Consultations (" + consultations.size() + ")", buildConsultationsPanel(consultations));
        tabs.addTab("Ordonnances (" + ordonnances.size() + ")", buildOrdonnancesPanel(ordonnances));
        tabs.addTab("Certificats (" + certificats.size() + ")", buildCertificatsPanel(certificats));
        tabs.addTab("Antécédents (" + antecedents.size() + ")", buildAntecedentsPanel(antecedents));
        tabs.addTab("Rendez-vous (" + rendezVous.size() + ")", buildRendezVousPanel(rendezVous));

        return tabs;
    }

    private <T> List<T> nullSafeList(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    private JScrollPane buildHistoriquePanel(DossierMedical dossier, DossierMedicalService service) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea historiqueArea = new JTextArea(dossier.getHistorique() != null ? dossier.getHistorique() : "");
        historiqueArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        historiqueArea.setLineWrap(true);
        historiqueArea.setWrapStyleWord(true);
        historiqueArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scroll = new JScrollPane(historiqueArea);
        scroll.setPreferredSize(new Dimension(0, 300));

        JButton saveBtn = new JButton("Enregistrer les modifications");
        saveBtn.setBackground(new Color(64, 120, 255));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.addActionListener(e -> {
            try {
                String modifiedBy = (principal != null && principal.fullName() != null)
                        ? principal.fullName()
                        : "unknown";
                service.updateHistorique(dossier.getId(), historiqueArea.getText(), modifiedBy);
                JOptionPane.showMessageDialog(this, "Historique mis à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(saveBtn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return new JScrollPane(panel);
    }

    private JScrollPane buildConsultationsPanel(List<Consultation> consultations) {
        Object[][] data = consultations.stream().map(c -> new Object[]{
                formatDateTime(c.getDate()),
                safeString(c.getStatus() != null ? c.getStatus().name() : "Inconnu"),
                safeString(c.getObservationsMedecin()),
                safeString(c.getNotes())
        }).toArray(Object[][]::new);

        return buildTablePanel(consultations, new String[]{"Date & Heure", "Statut", "Observations Médecin", "Notes"}, data);
    }

    private JScrollPane buildOrdonnancesPanel(List<Ordonnance> ordonnances) {
        Object[][] data = ordonnances.stream().map(o -> {
            String prescriptionsText = "Aucune prescription";
            if (o.getPrescriptions() != null && !o.getPrescriptions().isEmpty()) {
                prescriptionsText = o.getPrescriptions().stream()
                        .map(p -> "• " + safeString(String.valueOf(p.getMedicament())) +
                                (p.getDuree() != 0 ? " (" + p.getDuree() + ")" : ""))
                        .collect(Collectors.joining("\n"));
            }

            return new Object[]{
                    formatDate(o.getDateOrdonnance()),
                    prescriptionsText
            };
        }).toArray(Object[][]::new);

        return buildTablePanel(ordonnances, new String[]{"Date de l'ordonnance", "Prescriptions & Posologie"}, data);
    }

    private JScrollPane buildCertificatsPanel(List<Certificat> certificats) {
        Object[][] data = certificats.stream().map(c -> new Object[]{
                formatDate(c.getDateDebut()),
                formatDate(c.getDateFin()),
                c.getDureeRepos() + " jour" + (c.getDureeRepos() > 1 ? "s" : ""),
                truncate(safeString(c.getContenu()), 150)
        }).toArray(Object[][]::new);

        return buildTablePanel(certificats, new String[]{"Date début", "Date fin", "Durée repos", "Contenu"}, data);
    }

    private JScrollPane buildAntecedentsPanel(List<Antecedent> antecedents) {
        Object[][] data = antecedents.stream().map(a -> new Object[]{
                safeString(a.getNom()),
                safeString(a.getDescription()),
                formatDate(LocalDate.from(a.getDateCreation()))
        }).toArray(Object[][]::new);

        return buildTablePanel(antecedents, new String[]{"Type", "Description", "Date"}, data);
    }

    private JScrollPane buildRendezVousPanel(List<RendezVous> rdvList) {
        Object[][] data = rdvList.stream().map(r -> new Object[]{
                formatDateTime(r.getDate()),
                safeString(r.getMotif()),
                safeString(r.getStatus() != null ? r.getStatus().name() : "Inconnu")
        }).toArray(Object[][]::new);

        return buildTablePanel(rdvList, new String[]{"Date & Heure", "Motif", "Statut"}, data);
    }

    private JScrollPane buildTablePanel(List<?> data, String[] columns, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(50);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(64, 120, 255));
        table.getTableHeader().setForeground(Color.WHITE);

        if (data.isEmpty()) {
            Object[] emptyRow = new Object[columns.length];
            emptyRow[0] = "Aucun élément trouvé";
            for (int i = 1; i < columns.length; i++) emptyRow[i] = "";
            model.addRow(emptyRow);
        }

        return new JScrollPane(table);
    }

    // === Utilitaires ===
    private String safeString(String value) {
        return value != null ? value.trim() : "";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMAT) : "";
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : "";
    }

    private String truncate(String text, int max) {
        if (text == null || text.length() <= max) return text != null ? text : "";
        return text.substring(0, max) + "...";
    }

    private String getPatientFullName(Patient p) {
        if (p == null) return "Patient inconnu";
        String prenom = safeString(p.getPrenom());
        String nom = safeString(p.getNom()).toUpperCase();
        if (prenom.isEmpty() && nom.isEmpty()) return "Patient sans nom";
        return prenom.isEmpty() ? nom : nom.isEmpty() ? prenom : prenom + " " + nom;
    }

    private String getMedecinFullName(Medecin m) {
        if (m == null) return "Inconnu";
        String prenom = safeString(m.getPrenom());
        String nom = safeString(m.getNom()).toUpperCase();
        return "Dr. " + prenom + " " + nom;
    }
}