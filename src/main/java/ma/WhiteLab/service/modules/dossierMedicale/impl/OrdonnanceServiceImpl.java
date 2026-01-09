package ma.WhiteLab.service.modules.dossierMedicale.impl;

import ma.WhiteLab.entities.dossierMedical.Ordonnance;
import ma.WhiteLab.entities.dossierMedical.Prescription;
import ma.WhiteLab.entities.dossierMedical.DossierMedical;
import ma.WhiteLab.repository.modules.dossierMedical.api.OrdonnanceRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.WhiteLab.service.modules.dossierMedicale.api.OrdonnanceService;
import ma.WhiteLab.service.modules.dossierMedicale.api.OrdonnanceValidator;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;

    // Notre nouveau validateur simple et efficace
    private final OrdonnanceValidator validator;

    public OrdonnanceServiceImpl(
            OrdonnanceRepository ordonnanceRepository,
            DossierMedicalRepository dossierMedicalRepository,
            PrescriptionRepository prescriptionRepository,
            ConsultationRepository consultationRepository) {

        this.ordonnanceRepository = ordonnanceRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.consultationRepository = consultationRepository;

        // Instanciation directe du validateur
        this.validator = new OrdonnanceValidatorImpl(dossierMedicalRepository, consultationRepository);
    }

    /* ================= CRUD AVEC VALIDATION ================= */

    @Override
    public void create(Ordonnance o) {
        // Validation de l'ordonnance
        Map<String, String> errors = validator.validateCreation(o);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erreurs de validation de l'ordonnance : " + errors);
        }

        // Création de l'ordonnance
        ordonnanceRepository.create(o);

        // Création des prescriptions associées
        if (o.getPrescriptions() != null && !o.getPrescriptions().isEmpty()) {
            for (Prescription p : o.getPrescriptions()) {
                // Tu peux ajouter une validation spécifique pour Prescription ici si besoin
                p.setOrdonnance(o);
                prescriptionRepository.create(p);
            }
        }
    }

    @Override
    public void update(Ordonnance o) {
        // Validation pour mise à jour (ID obligatoire)
        Map<String, String> errors = validator.validateUpdate(o);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erreurs de validation de l'ordonnance : " + errors);
        }

        // Mise à jour de l'ordonnance
        ordonnanceRepository.update(o);

        // Gestion des prescriptions (création ou mise à jour)
        if (o.getPrescriptions() != null) {
            for (Prescription p : o.getPrescriptions()) {
                p.setOrdonnance(o);
                if (p.getId() != null) {
                    prescriptionRepository.update(p);
                } else {
                    prescriptionRepository.create(p);
                }
            }
        }
    }

    @Override
    public List<Ordonnance> getAll() {
        List<Ordonnance> list = ordonnanceRepository.findAll();
        list.forEach(o -> o.setPrescriptions(prescriptionRepository.findByOrdonnanceId(o.getId())));
        return list;
    }

    @Override
    public Ordonnance getById(Long id) {
        Ordonnance o = ordonnanceRepository.findById(id);
        if (o != null) {
            o.setPrescriptions(prescriptionRepository.findByOrdonnanceId(o.getId()));
        }
        return o;
    }

    @Override
    public void delete(Long id) {
        // Suppression des prescriptions d'abord
        List<Prescription> prescriptions = prescriptionRepository.findByOrdonnanceId(id);
        for (Prescription p : prescriptions) {
            prescriptionRepository.deleteById(p.getId());
        }
        // Puis l'ordonnance
        ordonnanceRepository.deleteById(id);
    }

    @Override
    public long count() {
        return ordonnanceRepository.findAll().size();
    }

    /* ================= RECHERCHES SPÉCIFIQUES ================= */

    @Override
    public List<Ordonnance> findByDossierId(Long dossierId) {
        List<Ordonnance> list = ordonnanceRepository.findByDossierId(dossierId);
        list.forEach(o -> o.setPrescriptions(prescriptionRepository.findByOrdonnanceId(o.getId())));
        return list;
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        List<Ordonnance> list = ordonnanceRepository.findByConsultationId(consultationId);
        list.forEach(o -> o.setPrescriptions(prescriptionRepository.findByOrdonnanceId(o.getId())));
        return list;
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        List<Ordonnance> list = ordonnanceRepository.findByDate(date);
        list.forEach(o -> o.setPrescriptions(prescriptionRepository.findByOrdonnanceId(o.getId())));
        return list;
    }

    @Override
    public List<Ordonnance> findBetweenDates(LocalDate start, LocalDate end) {
        List<Ordonnance> list = ordonnanceRepository.findBetweenDates(start, end);
        list.forEach(o -> o.setPrescriptions(prescriptionRepository.findByOrdonnanceId(o.getId())));
        return list;
    }

    @Override
    public List<Ordonnance> findByMedecinId(Long medecinId) {
        return getAll().stream()
                .filter(o -> o.getDossierMedical() != null
                        && o.getDossierMedical().getMedecine() != null
                        && o.getDossierMedical().getMedecine().getId().equals(medecinId))
                .toList();
    }

    @Override
    public List<Ordonnance> findByPatientId(Long patientId) {
        DossierMedical dossier = dossierMedicalRepository.findByPatientId(patientId)
                .orElse(null);

        if (dossier == null) {
            return List.of(); // ou throw exception selon ta logique
        }

        return findByDossierId(dossier.getId());
    }

    @Override
    public List<Ordonnance> findRecent(int days) {
        LocalDate start = LocalDate.now().minusDays(days);
        return findBetweenDates(start, LocalDate.now());
    }

    /* ================= TÉLÉCHARGEMENT PDF ================= */

    @Override
    public byte[] downloadOrdonnance(Long ordonnanceId) {
        Ordonnance o = getById(ordonnanceId);
        if (o == null) {
            throw new IllegalArgumentException("Ordonnance introuvable avec l'ID : " + ordonnanceId);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("ORDONNANCE MÉDICALE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            document.add(new Paragraph("Date : " + o.getDateOrdonnance()));
            document.add(new Paragraph(" "));

            String patientName = (o.getDossierMedical() != null && o.getDossierMedical().getPat() != null)
                    ? o.getDossierMedical().getPat().getNom().toUpperCase() + " " + o.getDossierMedical().getPat().getPrenom()
                    : "Patient inconnu";
            document.add(new Paragraph("Patient : " + patientName));

            String medecinName = (o.getDossierMedical() != null && o.getDossierMedical().getMedecine() != null)
                    ? "Dr. " + o.getDossierMedical().getMedecine().getNom() + " " + o.getDossierMedical().getMedecine().getPrenom()
                    : "Médecin inconnu";
            document.add(new Paragraph("Médecin : " + medecinName));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("__________________________________________________________________"));
            document.add(new Paragraph(" "));

            Paragraph prescTitle = new Paragraph("Prescriptions", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            document.add(prescTitle);
            document.add(new Paragraph(" "));

            if (o.getPrescriptions() != null && !o.getPrescriptions().isEmpty()) {
                for (Prescription p : o.getPrescriptions()) {
                    String medic = p.getMedicament() != null ? p.getMedicament().getNom() : "Médicament inconnu";
                    document.add(new Paragraph("• " + medic));
                    document.add(new Paragraph("  Posologie : " + p.getFrequence() + " — Durée : " + p.getDuree() + " jours"));
                    document.add(new Paragraph("  Quantité : " + p.getQte() + " unités"));
                    document.add(new Paragraph(" "));
                }
            } else {
                document.add(new Paragraph("Aucune prescription associée."));
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF de l'ordonnance", e);
        }
    }
}