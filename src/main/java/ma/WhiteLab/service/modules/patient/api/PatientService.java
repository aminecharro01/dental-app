package ma.WhiteLab.service.modules.patient.api;

import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;

import java.util.List;

/**
 * Interface du service métier pour la gestion des patients
 * 
 * Cette interface définit toutes les opérations métier qu'on peut faire
 * sur les patients : enregistrer, modifier, supprimer, rechercher, etc.
 * 
 * Les méthodes sont nommées de manière verbale pour être claires
 * (ex: enregistrerPatient, rechercherPatient, etc.)
 */
public interface PatientService {

    // ============================================
    //  MÉTHODES DE GESTION DES PATIENTS
    // ============================================

    /**
     * Enregistre un nouveau patient dans le système
     * 
     * @param patient Le patient à enregistrer (doit avoir nom, prenom, etc.)
     * @return Le patient enregistré avec son ID généré
     * @throws ValidationException Si les données du patient sont invalides
     * @throws ServiceException Si une erreur survient lors de l'enregistrement
     */
    Patient enregistrerPatient(Patient patient) throws ValidationException, ServiceException;

    /**
     * Modifie les informations d'un patient existant
     * 
     * @param patient Le patient avec les nouvelles informations (doit avoir un ID)
     * @return Le patient modifié
     * @throws ValidationException Si les données sont invalides
     * @throws ServiceException Si le patient n'existe pas ou erreur lors de la modification
     */
    Patient modifierPatient(Patient patient) throws ValidationException, ServiceException;

    /**
     * Supprime un patient du système
     * 
     * @param patientId L'ID du patient à supprimer
     * @throws ServiceException Si le patient n'existe pas ou erreur lors de la suppression
     */
    void supprimerPatient(Long patientId) throws ServiceException;

    /**
     * Récupère un patient par son ID
     * 
     * @param patientId L'ID du patient
     * @return Le patient trouvé, ou null si non trouvé
     */
    Patient consulterPatient(Long patientId);

    /**
     * Récupère tous les patients enregistrés
     * 
     * @return La liste de tous les patients
     */
    List<Patient> listerTousLesPatients();

    /**
     * Recherche des patients par nom ou prénom
     * 
     * @param motCle Le mot-clé de recherche (nom ou prénom)
     * @return La liste des patients correspondants
     */
    List<Patient> rechercherPatient(String motCle);

    /**
     * Recherche un patient par son email
     * 
     * @param email L'email du patient
     * @return Le patient trouvé, ou null si non trouvé
     */
    Patient rechercherPatientParEmail(String email);

    /**
     * Recherche un patient par son numéro de téléphone
     * 
     * @param telephone Le numéro de téléphone
     * @return Le patient trouvé, ou null si non trouvé
     */
    Patient rechercherPatientParTelephone(String telephone);

    /**
     * Vérifie si un patient existe dans le système
     * 
     * @param patientId L'ID du patient
     * @return true si le patient existe, false sinon
     */
    boolean patientExiste(Long patientId);

    /**
     * Compte le nombre total de patients
     * 
     * @return Le nombre de patients
     */
    long compterPatients();

    // ============================================
    //  MÉTHODES DE GESTION DES ANTÉCÉDENTS
    // ============================================

    /**
     * Ajoute un antécédent médical à un patient
     * 
     * @param patientId L'ID du patient
     * @param antecedentId L'ID de l'antécédent à ajouter
     * @throws ServiceException Si le patient ou l'antécédent n'existe pas
     */
    void ajouterAntecedent(Long patientId, Long antecedentId) throws ServiceException;

    /**
     * Retire un antécédent d'un patient
     * 
     * @param patientId L'ID du patient
     * @param antecedentId L'ID de l'antécédent à retirer
     * @throws ServiceException Si le patient ou l'antécédent n'existe pas
     */
    void retirerAntecedent(Long patientId, Long antecedentId) throws ServiceException;

    /**
     * Récupère tous les antécédents d'un patient
     * 
     * @param patientId L'ID du patient
     * @return La liste des antécédents du patient
     * @throws ServiceException Si le patient n'existe pas
     */
    List<Antecedent> consulterAntecedentsPatient(Long patientId) throws ServiceException;

    /**
     * Retire tous les antécédents d'un patient
     * 
     * @param patientId L'ID du patient
     * @throws ServiceException Si le patient n'existe pas
     */
    void retirerTousLesAntecedents(Long patientId) throws ServiceException;
}
