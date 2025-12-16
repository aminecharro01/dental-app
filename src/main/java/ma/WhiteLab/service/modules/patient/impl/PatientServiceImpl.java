package ma.WhiteLab.service.modules.patient.impl;

import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.common.validation.Validators;
import ma.WhiteLab.repository.modules.patient.api.PatientRepository;
import ma.WhiteLab.service.modules.patient.api.PatientService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implémentation du service métier pour la gestion des patients
 * 
 * Cette classe contient toute la logique métier pour gérer les patients.
 * Elle utilise le repository pour accéder à la base de données.
 * 
 * Les méthodes vérifient les données avant de les enregistrer
 * et lancent des exceptions si quelque chose ne va pas.
 */
public class PatientServiceImpl implements PatientService {

    // Le repository qu'on utilise pour accéder à la base de données
    private final PatientRepository patientRepository;

    /**
     * Constructeur : on reçoit le repository en paramètre
     * (injection de dépendance)
     * 
     * @param patientRepository Le repository pour accéder aux données patients
     */
    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // ============================================
    //  MÉTHODES DE GESTION DES PATIENTS
    // ============================================

    @Override
    public Patient enregistrerPatient(Patient patient) throws ValidationException, ServiceException {
        // 1. Vérifier que le patient n'est pas null
        if (patient == null) {
            throw new ValidationException("Le patient ne peut pas être null");
        }

        // 2. Valider les données obligatoires
        validerDonneesPatient(patient);

        // 3. Vérifier que l'email n'est pas déjà utilisé (s'il est fourni)
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            Patient existant = rechercherPatientParEmail(patient.getEmail());
            if (existant != null) {
                throw new ValidationException("Un patient avec cet email existe déjà");
            }
        }

        // 4. Vérifier que le téléphone n'est pas déjà utilisé (s'il est fourni)
        if (patient.getTelephone() != null && !patient.getTelephone().trim().isEmpty()) {
            Patient existant = rechercherPatientParTelephone(patient.getTelephone());
            if (existant != null) {
                throw new ValidationException("Un patient avec ce téléphone existe déjà");
            }
        }

        // 5. Définir la date de création si elle n'est pas définie
        if (patient.getDateCreation() == null) {
            patient.setDateCreation(LocalDateTime.now());
        }

        // 6. Enregistrer le patient dans la base de données
        try {
            patientRepository.create(patient);
            return patient;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de l'enregistrement du patient : " + e.getMessage(), e);
        }
    }

    @Override
    public Patient modifierPatient(Patient patient) throws ValidationException, ServiceException {
        // 1. Vérifier que le patient n'est pas null
        if (patient == null) {
            throw new ValidationException("Le patient ne peut pas être null");
        }

        // 2. Vérifier que le patient a un ID
        if (patient.getId() == null) {
            throw new ValidationException("L'ID du patient est obligatoire pour la modification");
        }

        // 3. Vérifier que le patient existe
        if (!patientExiste(patient.getId())) {
            throw new ServiceException("Le patient avec l'ID " + patient.getId() + " n'existe pas");
        }

        // 4. Valider les données
        validerDonneesPatient(patient);

        // 5. Vérifier que l'email n'est pas utilisé par un autre patient
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            Patient existant = rechercherPatientParEmail(patient.getEmail());
            if (existant != null && !existant.getId().equals(patient.getId())) {
                throw new ValidationException("Un autre patient utilise déjà cet email");
            }
        }

        // 6. Vérifier que le téléphone n'est pas utilisé par un autre patient
        if (patient.getTelephone() != null && !patient.getTelephone().trim().isEmpty()) {
            Patient existant = rechercherPatientParTelephone(patient.getTelephone());
            if (existant != null && !existant.getId().equals(patient.getId())) {
                throw new ValidationException("Un autre patient utilise déjà ce téléphone");
            }
        }

        // 7. Mettre à jour le patient
        try {
            patientRepository.update(patient);
            return patient;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la modification du patient : " + e.getMessage(), e);
        }
    }

    @Override
    public void supprimerPatient(Long patientId) throws ServiceException {
        // 1. Vérifier que l'ID n'est pas null
        if (patientId == null) {
            throw new ServiceException("L'ID du patient ne peut pas être null");
        }

        // 2. Vérifier que le patient existe
        if (!patientExiste(patientId)) {
            throw new ServiceException("Le patient avec l'ID " + patientId + " n'existe pas");
        }

        // 3. Supprimer le patient
        try {
            patientRepository.deleteById(patientId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la suppression du patient : " + e.getMessage(), e);
        }
    }

    @Override
    public Patient consulterPatient(Long patientId) {
        if (patientId == null) {
            return null;
        }
        return patientRepository.findById(patientId);
    }

    @Override
    public List<Patient> listerTousLesPatients() {
        return patientRepository.findAll();
    }

    @Override
    public List<Patient> rechercherPatient(String motCle) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return listerTousLesPatients();
        }
        return patientRepository.searchByNomPrenom(motCle.trim());
    }

    @Override
    public Patient rechercherPatientParEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return patientRepository.findByEmail(email.trim()).orElse(null);
    }

    @Override
    public Patient rechercherPatientParTelephone(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            return null;
        }
        return patientRepository.findByTelephone(telephone.trim()).orElse(null);
    }

    @Override
    public boolean patientExiste(Long patientId) {
        if (patientId == null) {
            return false;
        }
        return patientRepository.existsById(patientId);
    }

    @Override
    public long compterPatients() {
        return patientRepository.count();
    }

    // ============================================
    //  MÉTHODES DE GESTION DES ANTÉCÉDENTS
    // ============================================

    @Override
    public void ajouterAntecedent(Long patientId, Long antecedentId) throws ServiceException {
        // 1. Vérifier que les IDs ne sont pas null
        if (patientId == null) {
            throw new ServiceException("L'ID du patient ne peut pas être null");
        }
        if (antecedentId == null) {
            throw new ServiceException("L'ID de l'antécédent ne peut pas être null");
        }

        // 2. Vérifier que le patient existe
        if (!patientExiste(patientId)) {
            throw new ServiceException("Le patient avec l'ID " + patientId + " n'existe pas");
        }

        // 3. Vérifier que l'antécédent existe (on suppose qu'il y a un repository pour ça)
        // Pour l'instant, on fait juste l'ajout

        // 4. Ajouter l'antécédent au patient
        try {
            patientRepository.addAntecedentToPatient(patientId, antecedentId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de l'ajout de l'antécédent : " + e.getMessage(), e);
        }
    }

    @Override
    public void retirerAntecedent(Long patientId, Long antecedentId) throws ServiceException {
        // 1. Vérifier que les IDs ne sont pas null
        if (patientId == null) {
            throw new ServiceException("L'ID du patient ne peut pas être null");
        }
        if (antecedentId == null) {
            throw new ServiceException("L'ID de l'antécédent ne peut pas être null");
        }

        // 2. Vérifier que le patient existe
        if (!patientExiste(patientId)) {
            throw new ServiceException("Le patient avec l'ID " + patientId + " n'existe pas");
        }

        // 3. Retirer l'antécédent du patient
        try {
            patientRepository.removeAntecedentFromPatient(patientId, antecedentId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du retrait de l'antécédent : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Antecedent> consulterAntecedentsPatient(Long patientId) throws ServiceException {
        // 1. Vérifier que l'ID n'est pas null
        if (patientId == null) {
            throw new ServiceException("L'ID du patient ne peut pas être null");
        }

        // 2. Vérifier que le patient existe
        if (!patientExiste(patientId)) {
            throw new ServiceException("Le patient avec l'ID " + patientId + " n'existe pas");
        }

        // 3. Récupérer les antécédents
        try {
            return patientRepository.getAntecedentsOfPatient(patientId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération des antécédents : " + e.getMessage(), e);
        }
    }

    @Override
    public void retirerTousLesAntecedents(Long patientId) throws ServiceException {
        // 1. Vérifier que l'ID n'est pas null
        if (patientId == null) {
            throw new ServiceException("L'ID du patient ne peut pas être null");
        }

        // 2. Vérifier que le patient existe
        if (!patientExiste(patientId)) {
            throw new ServiceException("Le patient avec l'ID " + patientId + " n'existe pas");
        }

        // 3. Retirer tous les antécédents
        try {
            patientRepository.removeAllAntecedentsFromPatient(patientId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du retrait des antécédents : " + e.getMessage(), e);
        }
    }

    // ============================================
    //  MÉTHODES PRIVÉES (UTILITAIRES)
    // ============================================

    /**
     * Valide les données d'un patient avant enregistrement/modification
     * 
     * @param patient Le patient à valider
     * @throws ValidationException Si les données sont invalides
     */
    private void validerDonneesPatient(Patient patient) throws ValidationException {
        // Le nom est obligatoire
        Validators.notBlank(patient.getNom(), "Le nom");

        // Le prénom est obligatoire
        Validators.notBlank(patient.getPrenom(), "Le prénom");

        // Le sexe est obligatoire
        if (patient.getSexe() == null) {
            throw new ValidationException("Le sexe est obligatoire");
        }

        // L'assurance est obligatoire
        if (patient.getAssurance() == null) {
            throw new ValidationException("L'assurance est obligatoire");
        }

        // Valider l'email s'il est fourni
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            Validators.email(patient.getEmail());
        }

        // Valider le téléphone s'il est fourni
        if (patient.getTelephone() != null && !patient.getTelephone().trim().isEmpty()) {
            Validators.phone(patient.getTelephone());
        }
    }
}

