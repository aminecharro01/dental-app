package ma.WhiteLab.service.modules.patient.api;

import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.enums.Assurance;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.common.exceptions.ServiceException;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface du service métier pour la recherche avancée de patients
 * 
 * Ce service permet de faire des recherches complexes sur les patients
 * avec plusieurs critères de filtrage en utilisant le module Patient.
 * 
 * Les méthodes sont nommées de manière verbale pour être claires.
 */
public interface PatientRechercheAvanceeService {

    // ============================================
    //  RECHERCHES PAR CRITÈRES SIMPLES
    // ============================================

    /**
     * Recherche des patients par sexe
     * 
     * @param sexe Le sexe recherché
     * @return La liste des patients correspondants
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherParSexe(Sexe sexe) throws ServiceException;

    /**
     * Recherche des patients par assurance
     * 
     * @param assurance L'assurance recherchée
     * @return La liste des patients correspondants
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherParAssurance(Assurance assurance) throws ServiceException;

    /**
     * Recherche des patients par tranche d'âge
     * 
     * @param ageMin Âge minimum (inclus)
     * @param ageMax Âge maximum (inclus)
     * @return La liste des patients correspondants
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherParTrancheAge(int ageMin, int ageMax) throws ServiceException;

    /**
     * Recherche des patients nés entre deux dates
     * 
     * @param dateDebut Date de début (inclus)
     * @param dateFin Date de fin (inclus)
     * @return La liste des patients correspondants
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherParDateNaissance(LocalDate dateDebut, LocalDate dateFin) throws ServiceException;

    // ============================================
    //  RECHERCHES AVEC CRITÈRES MULTIPLES
    // ============================================

    /**
     * Recherche des patients avec plusieurs critères
     * 
     * @param nom Nom (peut être null pour ignorer ce critère)
     * @param prenom Prénom (peut être null pour ignorer ce critère)
     * @param sexe Sexe (peut être null pour ignorer ce critère)
     * @param assurance Assurance (peut être null pour ignorer ce critère)
     * @return La liste des patients correspondants à tous les critères
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherAvecCriteres(
            String nom,
            String prenom,
            Sexe sexe,
            Assurance assurance
    ) throws ServiceException;

    /**
     * Recherche des patients avec antécédents médicaux
     * 
     * @param avecAntecedents true pour chercher les patients avec antécédents, false pour ceux sans
     * @return La liste des patients correspondants
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherParPresenceAntecedents(boolean avecAntecedents) throws ServiceException;

    /**
     * Recherche des patients enregistrés dans une période
     * 
     * @param dateDebut Date de début (inclus)
     * @param dateFin Date de fin (inclus)
     * @return La liste des patients correspondants
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherParDateEnregistrement(LocalDate dateDebut, LocalDate dateFin) throws ServiceException;

    // ============================================
    //  RECHERCHES SPÉCIALISÉES
    // ============================================

    /**
     * Recherche des patients avec email
     * 
     * @param avecEmail true pour chercher les patients avec email, false pour ceux sans
     * @return La liste des patients correspondants
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherParPresenceEmail(boolean avecEmail) throws ServiceException;

    /**
     * Recherche des patients avec téléphone
     * 
     * @param avecTelephone true pour chercher les patients avec téléphone, false pour ceux sans
     * @return La liste des patients correspondants
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherParPresenceTelephone(boolean avecTelephone) throws ServiceException;

    /**
     * Recherche des patients par ville (dans l'adresse)
     * 
     * @param ville Le nom de la ville à rechercher
     * @return La liste des patients correspondants
     * @throws ServiceException Si une erreur survient
     */
    List<Patient> rechercherParVille(String ville) throws ServiceException;
}



