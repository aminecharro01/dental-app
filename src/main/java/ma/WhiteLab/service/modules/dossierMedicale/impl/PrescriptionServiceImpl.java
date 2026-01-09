package ma.WhiteLab.service.modules.dossierMedicale.impl;

import ma.WhiteLab.entities.dossierMedical.Prescription;
import ma.WhiteLab.repository.modules.dossierMedical.api.OrdonnanceRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.WhiteLab.service.modules.dossierMedicale.api.PrescriptionService;
import ma.WhiteLab.service.modules.dossierMedicale.api.PrescriptionValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final MedicamentRepository medicamentRepository;

    // Notre validateur moderne et complet
    private final PrescriptionValidator validator;

    public PrescriptionServiceImpl(
            PrescriptionRepository prescriptionRepository,
            OrdonnanceRepository ordonnanceRepository,
            MedicamentRepository medicamentRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.ordonnanceRepository = ordonnanceRepository;
        this.medicamentRepository = medicamentRepository;

        // Instanciation directe du validateur
        this.validator = new PrescriptionValidatorImpl(
                ordonnanceRepository,
                medicamentRepository,
                prescriptionRepository
        );
    }

    /* ================= CRUD AVEC VALIDATION ================= */

    @Override
    public void create(Prescription p) {
        Map<String, String> errors = validator.validateCreation(p);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erreurs de validation lors de la création de la prescription : " + errors);
        }
        prescriptionRepository.create(p);
    }

    @Override
    public void update(Prescription p) {
        if (p == null || p.getId() == null) {
            throw new IllegalArgumentException("L'ID de la prescription est obligatoire pour une mise à jour.");
        }

        Map<String, String> errors = validator.validateUpdate(p);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erreurs de validation lors de la mise à jour de la prescription : " + errors);
        }
        prescriptionRepository.update(p);
    }

    @Override
    public List<Prescription> getAll() {
        return prescriptionRepository.findAll();
    }

    @Override
    public Prescription getById(Long id) {
        return prescriptionRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la prescription est obligatoire pour la suppression.");
        }
        prescriptionRepository.deleteById(id);
    }

    @Override
    public long count() {
        return prescriptionRepository.findAll().size();
    }

    /* ================= MÉTHODES SPÉCIFIQUES ================= */

    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) return List.of();
        return prescriptionRepository.findByOrdonnanceId(ordonnanceId);
    }

    @Override
    public List<Prescription> findByMedicamentId(Long medicamentId) {
        if (medicamentId == null) return List.of();
        return prescriptionRepository.findByMedicamentId(medicamentId);
    }

    @Override
    public List<Prescription> findByFrequence(String frequence) {
        if (frequence == null || frequence.trim().isEmpty()) return List.of();
        return prescriptionRepository.findByFrequence(frequence.trim());
    }

    @Override
    public boolean exists(Long ordonnanceId, Long medicamentId) {
        if (ordonnanceId == null || medicamentId == null) return false;
        return prescriptionRepository.exists(ordonnanceId, medicamentId);
    }

    /* ================= MÉTHODES MÉTIERS ================= */

    @Override
    public List<Prescription> findByOrdonnanceIds(List<Long> ordonnanceIds) {
        if (ordonnanceIds == null || ordonnanceIds.isEmpty()) return List.of();
        return ordonnanceIds.stream()
                .flatMap(id -> findByOrdonnanceId(id).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Prescription> findByMedicamentIds(List<Long> medicamentIds) {
        if (medicamentIds == null || medicamentIds.isEmpty()) return List.of();
        return medicamentIds.stream()
                .flatMap(id -> findByMedicamentId(id).stream())
                .collect(Collectors.toList());
    }

    @Override
    public int totalQuantitePrescriteForMedicament(Long medicamentId) {
        if (medicamentId == null) return 0;
        return findByMedicamentId(medicamentId).stream()
                .mapToInt(Prescription::getQte)
                .sum();
    }

    @Override
    public double totalPrixPrescritForMedicament(Long medicamentId) {
        if (medicamentId == null) return 0.0;
        return findByMedicamentId(medicamentId).stream()
                .mapToDouble(p -> p.getQte() * (p.getMedicament() != null ? p.getMedicament().getPrixUnitaire() : 0.0))
                .sum();
    }

    @Override
    public List<Prescription> findRecent(int days) {
        if (days <= 0) return List.of();
        LocalDate threshold = LocalDate.now().minusDays(days);
        return getAll().stream()
                .filter(p -> p.getOrdonnance() != null
                        && p.getOrdonnance().getDateOrdonnance() != null
                        && !p.getOrdonnance().getDateOrdonnance().isBefore(threshold))
                .collect(Collectors.toList());
    }
}