package ma.WhiteLab.service.modules.dossierMedicale.api;

import ma.WhiteLab.entities.dossierMedical.Certificat;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;

import java.util.List;

public interface CertificatService {

    /**
     * Génère un nouveau certificat médical pour une consultation.
     *
     * @param consultationId l'ID de la consultation associée
     * @param certificat le certificat à créer
     * @return le certificat créé
     * @throws ValidationException si les données sont invalides
     * @throws ServiceException si la consultation n'existe pas
     */
    Certificat genererCertificatPourConsultation(Long consultationId, Certificat certificat) throws ValidationException, ServiceException;

    /**
     * Met à jour un certificat existant.
     *
     * @param certificat le certificat à mettre à jour
     * @return le certificat mis à jour
     * @throws ValidationException si les données sont invalides
     * @throws ServiceException si le certificat n'existe pas
     */
    Certificat modifierCertificat(Certificat certificat) throws ValidationException, ServiceException;

    /**
     * Supprime un certificat.
     *
     * @param certificatId l'ID du certificat à supprimer
     * @throws ServiceException si le certificat n'existe pas
     */
    void supprimerCertificat(Long certificatId) throws ServiceException;

    /**
     * Récupère un certificat par son ID.
     *
     * @param certificatId l'ID du certificat
     * @return le certificat, ou null si non trouvé
     * @throws ServiceException en cas d'erreur de récupération
     */
    Certificat consulterCertificat(Long certificatId) throws ServiceException;

    /**
     * Liste tous les certificats d'un patient.
     *
     * @param patientId l'ID du patient
     * @return la liste des certificats du patient
     * @throws ServiceException si le patient n'existe pas
     */
    List<Certificat> listerCertificatsParPatient(Long patientId) throws ServiceException;
}
