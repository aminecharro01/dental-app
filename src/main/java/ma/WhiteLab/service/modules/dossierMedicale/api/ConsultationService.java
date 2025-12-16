package ma.WhiteLab.service.modules.dossierMedicale.api;

import ma.WhiteLab.entities.dossierMedical.Consultation;
import ma.WhiteLab.entities.enums.StatusConsultation;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;

import java.util.List;

public interface ConsultationService {

    /**
     * Crée une nouvelle consultation pour un patient.
     *
     * @param patientId l'ID du patient
     * @param consultation la consultation à créer
     * @return la consultation créée
     * @throws ValidationException si les données sont invalides
     * @throws ServiceException si le patient ou son dossier n'existe pas
     */
    Consultation creerConsultationPourPatient(Long patientId, Consultation consultation) throws ValidationException, ServiceException;

    /**
     * Met à jour les informations d'une consultation.
     *
     * @param consultation la consultation à mettre à jour
     * @return la consultation mise à jour
     * @throws ValidationException si les données sont invalides
     * @throws ServiceException si la consultation n'existe pas
     */
    Consultation modifierConsultation(Consultation consultation) throws ValidationException, ServiceException;

    /**
     * Change le statut d'une consultation.
     *
     * @param consultationId l'ID de la consultation
     * @param nouveauStatus le nouveau statut
     * @throws ServiceException si la consultation n'existe pas
     */
    void changerStatusConsultation(Long consultationId, StatusConsultation nouveauStatus) throws ServiceException;

    /**
     * Récupère une consultation par son ID.
     *
     * @param consultationId l'ID de la consultation
     * @return la consultation, ou null si non trouvée
     * @throws ServiceException en cas d'erreur de récupération
     */
    Consultation consulterConsultation(Long consultationId) throws ServiceException;

    /**
     * Liste toutes les consultations d'un patient.
     *
     * @param patientId l'ID du patient
     * @return la liste des consultations
     * @throws ServiceException si le patient n'existe pas
     */
    List<Consultation> listerConsultationsParPatient(Long patientId) throws ServiceException;
}
