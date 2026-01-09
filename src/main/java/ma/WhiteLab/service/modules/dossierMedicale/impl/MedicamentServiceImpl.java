package ma.WhiteLab.service.modules.dossierMedicale.impl;

import ma.WhiteLab.entities.dossierMedical.DossierMedical;
import ma.WhiteLab.entities.dossierMedical.Medicament;
import ma.WhiteLab.entities.enums.Forme;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.WhiteLab.repository.modules.patient.api.AntecedentRepository;
import ma.WhiteLab.service.modules.dossierMedicale.api.MedicamentService;
import ma.WhiteLab.service.modules.dossierMedicale.api.MedicamentValidator;
import ma.WhiteLab.service.modules.dossierMedicale.dto.MedicamentDashboardDTO;
import ma.WhiteLab.service.modules.dossierMedicale.dto.MedicamentPatientDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository medicamentRepo;
    private final MedicamentValidator validator;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final AntecedentRepository antecedentRepository;

    public MedicamentServiceImpl(MedicamentRepository medicamentRepo,
                                 MedicamentValidator validator,
                                 DossierMedicalRepository dossierMedicalRepository,
                                 AntecedentRepository antecedentRepository) {
        this.medicamentRepo = medicamentRepo;
        this.validator = validator;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.antecedentRepository = antecedentRepository;
    }

    /* ================= CRUD ================= */
    @Override
    public List<Medicament> getAll() {
        return medicamentRepo.findAll();
    }

    @Override
    public Medicament getById(Long id) {
        return medicamentRepo.findById(id);
    }

    @Override
    public void create(Medicament m) {
        Map<String, String> errors = validator.validateCreation(m);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erreurs de validation lors de la création : " + errors);
        }
        medicamentRepo.create(m);
    }

    @Override
    public void update(Medicament m) {
        Map<String, String> errors = validator.validateUpdate(m);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erreurs de validation lors de la mise à jour : " + errors);
        }
        medicamentRepo.update(m);
    }

    @Override
    public void delete(Long id) {
        if (medicamentRepo.findById(id) == null) {
            throw new IllegalArgumentException("Médicament introuvable (ID=" + id + ")");
        }
        medicamentRepo.deleteById(id);
    }

    /* ================= Recherches ================= */
    @Override
    public List<Medicament> searchByName(String nom) {
        return medicamentRepo.searchByName(nom);
    }

    @Override
    public List<Medicament> searchByNameLike(String text) {
        return medicamentRepo.searchByNameLike(text);
    }

    @Override
    public List<Medicament> findByLabo(String labo) {
        return medicamentRepo.findByLabo(labo);
    }

    @Override
    public List<Medicament> findRemboursables() {
        return medicamentRepo.findRemboursables();
    }

    @Override
    public List<Medicament> findByForme(Forme forme) {
        return medicamentRepo.findByForme(forme != null ? forme.name() : null);
    }

    @Override
    public List<Medicament> findByPrixBetween(double min, double max) {
        return medicamentRepo.findByPrixBetween(min, max);
    }

    @Override
    public List<Medicament> findCheap(double maxPrice) {
        return findByPrixBetween(0, maxPrice);
    }

    @Override
    public List<Medicament> findExpensive(double minPrice) {
        return findByPrixBetween(minPrice, Double.MAX_VALUE);
    }

    /* ================= Statistiques ================= */
    @Override
    public long countTotal() {
        return medicamentRepo.count(); // Utilise une méthode dédiée dans le repo si possible
    }

    @Override
    public long countRemboursables() {
        return medicamentRepo.countRemboursables(); // À ajouter dans le repo pour performance
    }

    @Override
    public long countNonRemboursables() {
        return countTotal() - countRemboursables();
    }

    @Override
    public double getPrixMoyen() {
        return medicamentRepo.getPrixMoyen(); // À ajouter dans le repo pour éviter de charger tout
    }

    @Override
    public Medicament getMedicamentLePlusCher() {
        return medicamentRepo.findTopByPrixDesc();
    }

    @Override
    public Medicament getMedicamentLeMoinsCher() {
        return medicamentRepo.findTopByPrixAsc();
    }

    @Override
    public MedicamentDashboardDTO getDashboardStats() {
        List<Medicament> all = medicamentRepo.findAll();

        long total = all.size();
        long remboursables = all.stream().filter(Medicament::isRemboursable).count();
        double prixMoyen = all.stream().mapToDouble(Medicament::getPrixUnitaire).average().orElse(0.0);
        Medicament plusCher = getMedicamentLePlusCher();
        Medicament moinsCher = getMedicamentLeMoinsCher();

        MedicamentDashboardDTO dto = new MedicamentDashboardDTO();
        dto.setTotal(total);
        dto.setRemboursables(remboursables);
        dto.setNonRemboursables(total - remboursables);
        dto.setPrixMoyen(prixMoyen);
        dto.setPlusCher(plusCher);
        dto.setMoinsCher(moinsCher);

        return dto;
    }

    /* ================= Logique Patient & Antécédents ================= */
    @Override
    public MedicamentPatientDTO getMedicamentsPourPatient(Long patientId) {
        Optional<DossierMedical> dossierOpt = dossierMedicalRepository.findByPatientId(patientId);
        if (dossierOpt.isEmpty()) {
            throw new RuntimeException("Dossier médical introuvable pour le patient ID=" + patientId);
        }

        Patient patient = dossierOpt.get().getPat();
        List<Antecedent> antecedents = antecedentRepository.getAntecedentsByPatientId(patient.getId());

        List<Medicament> compatibles = getMedicamentsCompatibles(antecedents);
        List<Medicament> contreIndiques = getMedicamentsContreIndiques(antecedents);

        return new MedicamentPatientDTO(patient.getId(), compatibles, contreIndiques);
    }

    @Override
    public List<Medicament> getMedicamentsCompatibles(List<Antecedent> antecedents) {
        if (antecedents == null || antecedents.isEmpty()) {
            return getAll();
        }

        return getAll().stream()
                .filter(m -> {
                    for (Antecedent a : antecedents) {
                        switch (a.getCategorie()) {
                            case ALLERGIE:
                                if ("antibiotique".equalsIgnoreCase(m.getType()) ||
                                        m.getNom().toLowerCase().contains(a.getNom().toLowerCase())) {
                                    return false;
                                }
                                break;
                            case CONTRE_INDICATION:
                                if (m.getNom().toLowerCase().contains(a.getNom().toLowerCase())) {
                                    return false;
                                }
                                break;
                            case MALADIE_CHRONIQUE:
                                if ("anti-inflammatoire".equalsIgnoreCase(m.getType())) {
                                    return false;
                                }
                                break;
                            case TRAITEMENT_EN_COURS:
                                if (m.getNom().toLowerCase().contains(a.getNom().toLowerCase())) {
                                    return false;
                                }
                                break;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Medicament> getMedicamentsContreIndiques(List<Antecedent> antecedents) {
        List<Medicament> compatibles = getMedicamentsCompatibles(antecedents);
        return getAll().stream()
                .filter(m -> !compatibles.contains(m))
                .toList();
    }
}