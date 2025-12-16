package ma.WhiteLab.service.modules.dossierMedicale.impl;

import ma.WhiteLab.entities.dossierMedical.Certificat;
import ma.WhiteLab.entities.dossierMedical.Consultation;
import ma.WhiteLab.repository.modules.dossierMedical.api.CertificatRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.common.validation.Validators;
import ma.WhiteLab.service.modules.dossierMedicale.api.CertificatService;

import java.time.LocalDateTime;
import java.util.List;

public class CertificatServiceImpl implements CertificatService {

    private final CertificatRepository certificatRepository;
    private final ConsultationRepository consultationRepository;

    public CertificatServiceImpl(CertificatRepository certificatRepository, ConsultationRepository consultationRepository) {
        this.certificatRepository = certificatRepository;
        this.consultationRepository = consultationRepository;
    }

    @Override
    public Certificat genererCertificatPourConsultation(Long consultationId, Certificat certificat) throws ValidationException, ServiceException {
        if (consultationId == null) {
            throw new ServiceException("L'ID de la consultation ne peut pas être null.");
        }
        if (certificat == null) {
            throw new ValidationException("Le certificat ne peut pas être null.");
        }

        Consultation consultation = consultationRepository.findById(consultationId);
        if (consultation == null) {
            throw new ServiceException("La consultation avec l'ID " + consultationId + " n'existe pas.");
        }

        certificat.setConsultation(consultation);
        certificat.setDossierMedical(consultation.getDossierMedical());
        validerDonneesCertificat(certificat);

        certificat.setDateCreation(LocalDateTime.now());
        certificat.setDateMiseAJour(LocalDateTime.now());

        try {
            certificatRepository.create(certificat);
            return certificat;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la génération du certificat: " + e.getMessage(), e);
        }
    }

    @Override
    public Certificat modifierCertificat(Certificat certificat) throws ValidationException, ServiceException {
        if (certificat == null || certificat.getId() == null) {
            throw new ValidationException("Le certificat à modifier doit avoir un ID.");
        }

        if (certificatRepository.findById(certificat.getId()) == null) {
            throw new ServiceException("Le certificat avec l'ID " + certificat.getId() + " n'existe pas.");
        }

        validerDonneesCertificat(certificat);
        certificat.setDateMiseAJour(LocalDateTime.now());

        try {
            certificatRepository.update(certificat);
            return certificat;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la modification du certificat: " + e.getMessage(), e);
        }
    }

    @Override
    public void supprimerCertificat(Long certificatId) throws ServiceException {
        if (certificatId == null) {
            throw new ServiceException("L'ID du certificat ne peut pas être null.");
        }

        if (certificatRepository.findById(certificatId) == null) {
            throw new ServiceException("Le certificat avec l'ID " + certificatId + " n'existe pas.");
        }

        try {
            certificatRepository.deleteById(certificatId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la suppression du certificat: " + e.getMessage(), e);
        }
    }

    @Override
    public Certificat consulterCertificat(Long certificatId) throws ServiceException {
        if (certificatId == null) {
            return null;
        }
        try {
            return certificatRepository.findById(certificatId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la consultation du certificat: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Certificat> listerCertificatsParPatient(Long patientId) throws ServiceException {
        if (patientId == null) {
            throw new ServiceException("L'ID du patient ne peut pas être null.");
        }
        // This requires a join or a more complex query. For simplicity, we can iterate through consultations.
        // A better approach would be to add a method in the repository: findByPatientId.
        // For now, let's assume we need to implement it here.
        throw new UnsupportedOperationException("Lister les certificats par patient n'est pas encore implémenté.");
    }

    private void validerDonneesCertificat(Certificat certificat) throws ValidationException {
        Validators.notBlank(certificat.getContenu(), "Le contenu du certificat");
        if (certificat.getDateDebut() == null) {
            throw new ValidationException("La date de début du certificat est obligatoire.");
        }
        if (certificat.getDureeRepos() <= 0) {
            throw new ValidationException("La durée du repos doit être positive.");
        }
        if (certificat.getConsultation() == null || certificat.getDossierMedical() == null) {
            throw new ValidationException("Le certificat doit être lié à une consultation et un dossier médical.");
        }
    }
}
