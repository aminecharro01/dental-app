import ma.WhiteLab.service.modules.patient.dto.PatientDTO;

import ma.WhiteLab.common.consoleLog.ConsoleLogger;
import ma.WhiteLab.common.utils.RepoFactory;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.repository.modules.patient.api.PatientRepository;
import ma.WhiteLab.service.modules.patient.api.PatientService;
import ma.WhiteLab.service.common.Transaction;

import java.util.List;
import java.util.Optional;

public class PatientServiceImpl implements PatientService {

    private final RepoFactory<PatientRepository> patientRepoFactory;
    private final PatientValidator validator = new PatientValidator(); // Instanciation directe ou injection

    public PatientServiceImpl(RepoFactory<PatientRepository> patientRepoFactory) {
        this.patientRepoFactory = patientRepoFactory;
    }

    // =========================
    //        CRUD de base
    // =========================

    @Override
    public List<Patient> findAll() {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.findAll();
        });
    }

    @Override
    public Patient findById(Long id) {
        ConsoleLogger.info("Recherche du patient par ID : " + id);

        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            Patient patient = repo.findById(id);
            if (patient == null) {
                ConsoleLogger.warn("Patient non trouvé pour ID : " + id);
            }
            return patient;
        });
    }

    @Override
    public Patient create(Patient patient) {
        ConsoleLogger.info("Création d'un nouveau patient : " + patient.getNom() + " " + patient.getPrenom());

        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            repo.create(patient);

            ConsoleLogger.info("Patient créé avec succès, ID généré : " + patient.getId());
            return patient;
        });
    }

    @Override
    public ma.WhiteLab.mvc.dto.PatientDTO create(ma.WhiteLab.mvc.dto.PatientDTO dto) throws ma.WhiteLab.common.exceptions.ValidationException {
        // 1. Validation
        java.util.Map<String, String> errors = validator.validate(dto);
        if (!errors.isEmpty()) {
            throw new ma.WhiteLab.common.exceptions.ValidationException("Erreur de validation patient");
        }

        // 2. Mapping DTO -> Entity
        ma.WhiteLab.entities.patient.Patient patient = mapToEntity(dto);

        // 3. Appel méthode existante transactionnelle
        ma.WhiteLab.entities.patient.Patient saved = create(patient);

        // 4. Mapping Entity -> DTO
        return mapToDto(saved);
    }

    @Override
    public Patient update(Patient patient) {
        if (patient.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un patient sans ID");
        }

        ConsoleLogger.info("Mise à jour du patient ID : " + patient.getId());

        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);

            if (!repo.existsById(patient.getId())) {
                throw new IllegalArgumentException("Patient avec ID " + patient.getId() + " n'existe pas");
            }

            repo.update(patient);
            ConsoleLogger.info("Patient mis à jour avec succès, ID : " + patient.getId());
            return patient;
        });
    }

    @Override
    public void delete(Patient patient) {
        if (patient == null || patient.getId() == null) {
            throw new IllegalArgumentException("Patient null ou sans ID ne peut pas être supprimé");
        }
        deleteById(patient.getId());
    }

    @Override
    public void deleteById(Long id) {
        ConsoleLogger.info("Suppression du patient ID : " + id);

        Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);

            if (!repo.existsById(id)) {
                ConsoleLogger.warn("Tentative de suppression d'un patient inexistant ID : " + id);
                return null;
            }

            repo.deleteById(id);
            ConsoleLogger.info("Patient supprimé avec succès, ID : " + id);
            return null;
        });
    }

    // =========================
    //        Recherches spécifiques
    // =========================

    @Override
    public Optional<Patient> findByEmail(String email) {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.findByEmail(email);
        });
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.findByTelephone(telephone);
        });
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        ConsoleLogger.info("Recherche patients par nom/prénom : " + keyword);

        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.searchByNomPrenom(keyword);
        });
    }

    @Override
    public boolean existsById(Long id) {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.existsById(id);
        });
    }

    @Override
    public long count() {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.count();
        });
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.findPage(limit, offset);
        });
    }

    // =========================
    //      Gestion des antécédents (Many-to-Many)
    // =========================

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        ConsoleLogger.info("Ajout antécédent ID " + antecedentId + " au patient ID " + patientId);

        Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            repo.addAntecedentToPatient(patientId, antecedentId);
            return null;
        });
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        ConsoleLogger.info("Suppression antécédent ID " + antecedentId + " du patient ID " + patientId);

        Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            repo.removeAntecedentFromPatient(patientId, antecedentId);
            return null;
        });
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        ConsoleLogger.info("Suppression de tous les antécédents du patient ID " + patientId);

        Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            repo.removeAllAntecedentsFromPatient(patientId);
            return null;
        });
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.getAntecedentsOfPatient(patientId);
        });
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.getPatientsByAntecedent(antecedentId);
        });
    }

    // =========================
    //   Statistiques dashboard (par cabinet)
    // =========================

    @Override
    public long countPatientsByCabinet(Long cabinetId) {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.countPatientsByCabinet(cabinetId);
        });
    }

    @Override
    public int countNouveauxPatientsDuMois(Long cabinetId, int annee, int mois) {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.countNouveauxPatientsDuMois(cabinetId, annee, mois);
        });
    }

    @Override
    public List<Object[]> getRepartitionPatientsParTrancheAge(Long cabinetId) {
        return Transaction.initTransaction(cnx -> {
            PatientRepository repo = patientRepoFactory.create(cnx);
            return repo.getRepartitionPatientsParTrancheAge(cabinetId);
        });
    }
    @Override
    public ma.WhiteLab.mvc.dto.PatientDTO update(ma.WhiteLab.mvc.dto.PatientDTO dto) throws ma.WhiteLab.common.exceptions.ValidationException {
        // 1. Validation
        java.util.Map<String, String> errors = validator.validate(dto);
        if (!errors.isEmpty()) {
            throw new ma.WhiteLab.common.exceptions.ValidationException("Erreur de validation patient (Update)");
        }
        
        if (dto.getId() == null) throw new IllegalArgumentException("ID requis pour update");

        // 2. Mapping DTO -> Entity
        ma.WhiteLab.entities.patient.Patient patient = mapToEntity(dto);
        // S'assurer de garder l'ID
        patient.setId(dto.getId());

        // 3. Appel méthode existante
        ma.WhiteLab.entities.patient.Patient updated = update(patient);

        // 4. Retour DTO
        return mapToDto(updated);
    }

    // --- Helpers Mapping (A déplacer dans un Mapper si possible) ---
    private ma.WhiteLab.entities.patient.Patient mapToEntity(ma.WhiteLab.mvc.dto.PatientDTO dto) {
        return ma.WhiteLab.entities.patient.Patient.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .telephone(dto.getTelephone())
                .adresse(dto.getAdresse())
                .dateNaissance(dto.getDateNaissance() != null ? java.time.LocalDate.parse(dto.getDateNaissance()) : null)
                .sexe(dto.getSexe() != null ? ma.WhiteLab.entities.enums.Sexe.valueOf(dto.getSexe()) : null)
                .assurance(dto.getAssurance() != null ? ma.WhiteLab.entities.enums.Assurance.valueOf(dto.getAssurance()) : null)
                .build();
    }

    private ma.WhiteLab.mvc.dto.PatientDTO mapToDto(ma.WhiteLab.entities.patient.Patient entity) {
        return ma.WhiteLab.mvc.dto.PatientDTO.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .email(entity.getEmail())
                .telephone(entity.getTelephone())
                .adresse(entity.getAdresse())
                .dateNaissance(entity.getDateNaissance() != null ? entity.getDateNaissance().toString() : null)
                .sexe(entity.getSexe() != null ? entity.getSexe().name() : null)
                .assurance(entity.getAssurance() != null ? entity.getAssurance().name() : null)
                .nomComplet(entity.getNomComplet())
                .age(entity.getDateNaissance() != null ? java.time.Period.between(entity.getDateNaissance(), java.time.LocalDate.now()).getYears() : 0)
                .build();
    }
}