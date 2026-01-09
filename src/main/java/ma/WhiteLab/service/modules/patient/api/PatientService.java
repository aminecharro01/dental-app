package ma.WhiteLab.service.modules.patient.api;

import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.service.modules.patient.dto.PatientDTO;
import ma.WhiteLab.common.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

/**
 * Interface du service métier pour la gestion des patients.
 * Elle expose toutes les opérations nécessaires au niveau métier/application.
 */
public interface PatientService {

    // =========================
    //        CRUD de base
    // =========================

    List<Patient> findAll();

    Patient findById(Long id);

    Patient create(Patient patient);
    
    // INFO: Méthode avec DTO et Validation
    PatientDTO create(PatientDTO patientDto) throws ValidationException;

    Patient update(Patient patient);

    // INFO: Méthode avec DTO et Validation
    PatientDTO update(PatientDTO patientDto) throws ValidationException;

    void delete(Patient patient);

    void deleteById(Long id);

    // =========================
    //        Recherches spécifiques
    // =========================

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByTelephone(String telephone);

    List<Patient> searchByNomPrenom(String keyword);

    boolean existsById(Long id);

    long count();

    List<Patient> findPage(int limit, int offset);

    // =========================
    //      Gestion des antécédents (Many-to-Many)
    // =========================

    void addAntecedentToPatient(Long patientId, Long antecedentId);

    void removeAntecedentFromPatient(Long patientId, Long antecedentId);

    void removeAllAntecedentsFromPatient(Long patientId);

    List<Antecedent> getAntecedentsOfPatient(Long patientId);

    List<Patient> getPatientsByAntecedent(Long antecedentId);

    // =========================
    //   Statistiques dashboard (par cabinet)
    // =========================

    /**
     * Compte le nombre total de patients distincts rattachés à un cabinet médical
     * (via les dossiers médicaux des médecins du cabinet).
     */
    long countPatientsByCabinet(Long cabinetId);

    /**
     * Compte le nombre de nouveaux patients créés dans un mois donné pour un cabinet.
     */
    int countNouveauxPatientsDuMois(Long cabinetId, int annee, int mois);

    /**
     * Retourne la répartition des patients par tranche d'âge pour un cabinet.
     * Chaque entrée du tableau : [trancheAge (String), nombre (Long)]
     */
    List<Object[]> getRepartitionPatientsParTrancheAge(Long cabinetId);
}