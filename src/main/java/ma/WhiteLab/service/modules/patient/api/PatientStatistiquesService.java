package ma.WhiteLab.service.modules.patient.api;

import ma.WhiteLab.entities.enums.Assurance;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.common.exceptions.ServiceException;

import java.time.LocalDate;
import java.util.Map;

/**
 * Interface du service métier pour les statistiques sur les patients
 * 
 * Ce service fournit des statistiques et analyses sur les patients
 * en utilisant le module Patient.
 * 
 * Les méthodes sont nommées de manière verbale pour être claires.
 */
public interface PatientStatistiquesService {

    // ============================================
    //  STATISTIQUES GÉNÉRALES
    // ============================================

    /**
     * Compte le nombre total de patients
     * 
     * @return Le nombre total de patients
     */
    long compterTotalPatients();

    /**
     * Compte le nombre de patients par sexe
     * 
     * @return Une map avec le sexe comme clé et le nombre comme valeur
     * @throws ServiceException Si une erreur survient
     */
    Map<Sexe, Long> compterPatientsParSexe() throws ServiceException;

    /**
     * Compte le nombre de patients par assurance
     * 
     * @return Une map avec l'assurance comme clé et le nombre comme valeur
     * @throws ServiceException Si une erreur survient
     */
    Map<Assurance, Long> compterPatientsParAssurance() throws ServiceException;

    /**
     * Compte le nombre de nouveaux patients enregistrés dans une période
     * 
     * @param dateDebut Date de début de la période
     * @param dateFin Date de fin de la période
     * @return Le nombre de nouveaux patients
     * @throws ServiceException Si une erreur survient
     */
    long compterNouveauxPatients(LocalDate dateDebut, LocalDate dateFin) throws ServiceException;

    // ============================================
    //  STATISTIQUES PAR ÂGE
    // ============================================

    /**
     * Compte le nombre de patients par tranche d'âge
     * 
     * @return Une map avec la tranche d'âge comme clé (ex: "0-18", "19-30", etc.) et le nombre comme valeur
     * @throws ServiceException Si une erreur survient
     */
    Map<String, Long> compterPatientsParTrancheAge() throws ServiceException;

    /**
     * Calcule l'âge moyen des patients
     * 
     * @return L'âge moyen (en années)
     * @throws ServiceException Si une erreur survient
     */
    double calculerAgeMoyen() throws ServiceException;

    /**
     * Trouve l'âge minimum parmi les patients
     * 
     * @return L'âge minimum (en années), ou -1 si aucun patient n'a de date de naissance
     * @throws ServiceException Si une erreur survient
     */
    int trouverAgeMinimum() throws ServiceException;

    /**
     * Trouve l'âge maximum parmi les patients
     * 
     * @return L'âge maximum (en années), ou -1 si aucun patient n'a de date de naissance
     * @throws ServiceException Si une erreur survient
     */
    int trouverAgeMaximum() throws ServiceException;

    // ============================================
    //  STATISTIQUES AVANCÉES
    // ============================================

    /**
     * Compte le nombre de patients avec antécédents médicaux
     * 
     * @return Le nombre de patients ayant au moins un antécédent
     * @throws ServiceException Si une erreur survient
     */
    long compterPatientsAvecAntecedents() throws ServiceException;

    /**
     * Compte le nombre de patients sans antécédents médicaux
     * 
     * @return Le nombre de patients sans antécédents
     * @throws ServiceException Si une erreur survient
     */
    long compterPatientsSansAntecedents() throws ServiceException;

    /**
     * Calcule le pourcentage de patients avec email
     * 
     * @return Le pourcentage (entre 0 et 100)
     * @throws ServiceException Si une erreur survient
     */
    double calculerPourcentagePatientsAvecEmail() throws ServiceException;

    /**
     * Calcule le pourcentage de patients avec téléphone
     * 
     * @return Le pourcentage (entre 0 et 100)
     * @throws ServiceException Si une erreur survient
     */
    double calculerPourcentagePatientsAvecTelephone() throws ServiceException;
}



