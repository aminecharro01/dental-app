package ma.WhiteLab.service.modules.dossierMedicale.impl;

import ma.WhiteLab.entities.dossierMedical.Consultation;
import ma.WhiteLab.entities.dossierMedical.DossierMedical;
import ma.WhiteLab.entities.enums.StatusConsultation;
import ma.WhiteLab.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.WhiteLab.repository.modules.patient.api.PatientRepository;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.common.validation.Validators;
import ma.WhiteLab.service.modules.dossierMedicale.api.ConsultationService;

import java.time.LocalDateTime;
import java.util.List;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final DossierMedicalRepository dossierMedicalRepository;

    public ConsultationServiceImpl(ConsultationRepository consultationRepository, PatientRepository patientRepository, DossierMedicalRepository dossierMedicalRepository) {
        this.consultationRepository = consultationRepository;
        this.patientRepository = patientRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
    }

    @Override
    public Consultation creerConsultationPourPatient(Long patientId, Consultation consultation) throws ValidationException, ServiceException {
        if (patientId == null) {
            throw new ServiceException("L'ID du patient ne peut pas être null.");
        }
        if (consultation == null) {
            throw new ValidationException("La consultation ne peut pas être nulle.");
        }

        if (!patientRepository.existsById(patientId)) {
            throw new ServiceException("Le patient avec l'ID " + patientId + " n'existe pas.");
        }

        DossierMedical dossier = dossierMedicalRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ServiceException("Le dossier médical pour le patient " + patientId + " est introuvable."));

        consultation.setDossierMedical(dossier);
        consultation.setDate(LocalDateTime.now());
        consultation.setStatus(StatusConsultation.PLANIFIEE);
        validerDonneesConsultation(consultation);

        try {
            consultationRepository.create(consultation);
            return consultation;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la création de la consultation: " + e.getMessage(), e);
        }
    }

    @Override
    public Consultation modifierConsultation(Consultation consultation) throws ValidationException, ServiceException {
        if (consultation == null || consultation.getId() == null) {
            throw new ValidationException("La consultation à modifier doit avoir un ID.");
        }

        if (consultationRepository.findById(consultation.getId()) == null) {
            throw new ServiceException("La consultation avec l'ID " + consultation.getId() + " n'existe pas.");
        }

        validerDonneesConsultation(consultation);
        consultation.setDateMiseAJour(LocalDateTime.now());

        try {
            consultationRepository.update(consultation);
            return consultation;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la modification de la consultation: " + e.getMessage(), e);
        }
    }

    @Override
    public void changerStatusConsultation(Long consultationId, StatusConsultation nouveauStatus) throws ServiceException {
        if (consultationId == null || nouveauStatus == null) {
            throw new ServiceException("L'ID de la consultation et le nouveau statut ne peuvent pas être nuls.");
        }

        Consultation consultation = consulterConsultation(consultationId);
        if (consultation == null) {
            throw new ServiceException("La consultation avec l'ID " + consultationId + " n'existe pas.");
        }

        consultation.setStatus(nouveauStatus);
        consultation.setDateMiseAJour(LocalDateTime.now());

        try {
            consultationRepository.update(consultation);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du changement de statut de la consultation: " + e.getMessage(), e);
        }
    }

    @Override
    public Consultation consulterConsultation(Long consultationId) throws ServiceException {
        if (consultationId == null) {
            return null;
        }
        try {
            return consultationRepository.findById(consultationId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la consultation: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Consultation> listerConsultationsParPatient(Long patientId) throws ServiceException {
        if (patientId == null) {
            throw new ServiceException("L'ID du patient ne peut pas être null.");
        }
        DossierMedical dossier = dossierMedicalRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ServiceException("Le dossier médical pour le patient " + patientId + " est introuvable."));

        try {
            return consultationRepository.findByDossierMedicalId(dossier.getId());
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération des consultations du patient: " + e.getMessage(), e);
        }
    }

    private void validerDonneesConsultation(Consultation consultation) throws ValidationException {
        if (consultation.getDate() == null) {
            throw new ValidationException("La date de la consultation est obligatoire.");
        }
        if (consultation.getStatus() == null) {
            throw new ValidationException("Le statut de la consultation est obligatoire.");
        }
        if (consultation.getDossierMedical() == null) {
            throw new ValidationException("La consultation doit être liée à un dossier médical.");
        }
    }
}
