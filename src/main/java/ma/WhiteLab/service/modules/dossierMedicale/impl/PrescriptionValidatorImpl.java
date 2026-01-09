package ma.WhiteLab.service.modules.dossierMedicale.impl;

import ma.WhiteLab.entities.dossierMedical.Ordonnance;
import ma.WhiteLab.entities.dossierMedical.Prescription;
import ma.WhiteLab.entities.dossierMedical.Medicament; // à adapter si ton package est différent
import ma.WhiteLab.repository.modules.dossierMedical.api.OrdonnanceRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.MedicamentRepository; // à adapter
import ma.WhiteLab.service.modules.dossierMedicale.api.PrescriptionValidator;

import java.util.HashMap;
import java.util.Map;

public class PrescriptionValidatorImpl implements PrescriptionValidator {

    private final OrdonnanceRepository ordonnanceRepository;
    private final MedicamentRepository medicamentRepository;
    private final PrescriptionRepository prescriptionRepository; // pour exists() si besoin

    public PrescriptionValidatorImpl(
            OrdonnanceRepository ordonnanceRepository,
            MedicamentRepository medicamentRepository,
            PrescriptionRepository prescriptionRepository) {

        this.ordonnanceRepository = ordonnanceRepository;
        this.medicamentRepository = medicamentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public Map<String, String> validateCreation(Prescription prescription) {
        return validateCommon(prescription, true);
    }

    @Override
    public Map<String, String> validateUpdate(Prescription prescription) {
        Map<String, String> errors = validateCommon(prescription, false);

        if (prescription.getId() == null) {
            errors.put("id", "L'ID de la prescription est obligatoire pour une mise à jour.");
        }

        return errors;
    }

    @Override
    public Map<String, String> validatePrescription(Prescription prescription) {
        return validateCommon(prescription, false);
    }

    private Map<String, String> validateCommon(Prescription prescription, boolean isCreation) {
        Map<String, String> errors = new HashMap<>();

        if (prescription == null) {
            errors.put("prescription", "La prescription ne peut pas être null.");
            return errors;
        }

        // Ordonnance obligatoire
        Ordonnance ordonnance = prescription.getOrdonnance();
        if (ordonnance == null || ordonnance.getId() == null) {
            errors.put("ordonnance", "L'ordonnance associée est obligatoire.");
        } else {
            Ordonnance existingOrdonnance = ordonnanceRepository.findById(ordonnance.getId());
            if (existingOrdonnance == null) {
                errors.put("ordonnance", "L'ordonnance avec l'ID " + ordonnance.getId() + " n'existe pas.");
            }
        }

        // Médicament obligatoire
        Medicament medicament = prescription.getMedicament();
        if (medicament == null || medicament.getId() == null) {
            errors.put("medicament", "Le médicament est obligatoire.");
        } else {
            Medicament existingMedicament = medicamentRepository.findById(medicament.getId());
            if (existingMedicament == null) {
                errors.put("medicament", "Le médicament avec l'ID " + medicament.getId() + " n'existe pas.");
            }
        }

        // Quantité
        Integer qte = prescription.getQte();
        if (qte == null || qte <= 0) {
            errors.put("qte", "La quantité doit être un nombre entier positif.");
        } else if (qte > 1000) {
            errors.put("qte", "La quantité ne peut pas dépasser 1000 unités.");
        }

        // Durée (en jours)
        Integer duree = prescription.getDuree();
        if (duree == null || duree <= 0) {
            errors.put("duree", "La durée du traitement doit être positive (en jours).");
        } else if (duree > 365) {
            errors.put("duree", "La durée ne peut pas dépasser 365 jours.");
        }

        // Fréquence / Posologie
        String frequence = prescription.getFrequence();
        if (frequence == null || frequence.trim().isEmpty()) {
            errors.put("frequence", "La fréquence (posologie) est obligatoire.");
        } else if (frequence.trim().length() > 200) {
            errors.put("frequence", "La posologie ne peut pas dépasser 200 caractères.");
        }

        // Optionnel : éviter les doublons dans la même ordonnance (même médicament)
        if (isCreation && ordonnance != null && medicament != null
                && ordonnance.getId() != null && medicament.getId() != null) {

            if (prescriptionRepository.exists(ordonnance.getId(), medicament.getId())) {
                errors.put("doublon", "Ce médicament est déjà prescrit dans cette ordonnance.");
            }
        }

        return errors;
    }
}