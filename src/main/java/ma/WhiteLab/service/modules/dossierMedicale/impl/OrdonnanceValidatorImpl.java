package ma.WhiteLab.service.modules.dossierMedicale.impl;

import ma.WhiteLab.entities.dossierMedical.Consultation;
import ma.WhiteLab.entities.dossierMedical.DossierMedical;
import ma.WhiteLab.entities.dossierMedical.Ordonnance;
import ma.WhiteLab.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.WhiteLab.service.modules.dossierMedicale.api.OrdonnanceValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class OrdonnanceValidatorImpl implements OrdonnanceValidator {

    private final DossierMedicalRepository dossierRepo;
    private final ConsultationRepository consultationRepo;

    // Constructeur avec injection manuelle des repositories nécessaires
    public OrdonnanceValidatorImpl(DossierMedicalRepository dossierRepo,
                                   ConsultationRepository consultationRepo) {
        this.dossierRepo = dossierRepo;
        this.consultationRepo = consultationRepo;
    }

    @Override
    public Map<String, String> validateCreation(Ordonnance ordonnance) {
        return validateOrdonnanceCommon(ordonnance, true);
    }

    @Override
    public Map<String, String> validateUpdate(Ordonnance ordonnance) {
        Map<String, String> errors = validateOrdonnanceCommon(ordonnance, false);

        // Pour update, l'ID est obligatoire
        if (ordonnance.getId() == null) {
            errors.put("id", "L'ID de l'ordonnance est obligatoire pour une mise à jour.");
        }

        return errors;
    }

    @Override
    public Map<String, String> validateOrdonnance(Ordonnance ordonnance) {
        return validateOrdonnanceCommon(ordonnance, false);
    }

    // Méthode commune pour éviter la duplication
    private Map<String, String> validateOrdonnanceCommon(Ordonnance ordonnance, boolean isCreation) {
        Map<String, String> errors = new HashMap<>();

        if (ordonnance == null) {
            errors.put("ordonnance", "L'ordonnance ne peut pas être null.");
            return errors;
        }

        // Date obligatoire
        LocalDate date = ordonnance.getDateOrdonnance();
        if (date == null) {
            errors.put("dateOrdonnance", "La date de l'ordonnance est obligatoire.");
        } else if (date.isAfter(LocalDate.now())) {
            errors.put("dateOrdonnance", "La date de l'ordonnance ne peut pas être dans le futur.");
        }

        // Au moins une référence : dossier médical OU consultation
        DossierMedical dossier = ordonnance.getDossierMedical();
        Consultation consultation = ordonnance.getConsultation();

        if (dossier == null && consultation == null) {
            errors.put("references", "L'ordonnance doit être liée à un dossier médical ou à une consultation.");
        }

        // Vérification existence dossier médical si présent
        if (dossier != null && dossier.getId() != null) {
            if (dossierRepo.findById(dossier.getId()) == null) {
                errors.put("dossierMedical", "Le dossier médical avec l'ID " + dossier.getId() + " n'existe pas.");
            }
        }

        // Vérification existence consultation si présente
        if (consultation != null && consultation.getId() != null) {
            if (consultationRepo.findById(consultation.getId()) == null) {
                errors.put("consultation", "La consultation avec l'ID " + consultation.getId() + " n'existe pas.");
            }
        }

        return errors;
    }
}