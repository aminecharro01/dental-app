package ma.WhiteLab.service.modules.patient.impl;

import ma.WhiteLab.common.validation.Validator;
import ma.WhiteLab.service.modules.patient.dto.PatientDTO;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.entities.enums.Assurance;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PatientValidator implements Validator<PatientDTO> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[5-7][0-9]{8}$"); // Exemple format marocain

    @Override
    public Map<String, String> validate(PatientDTO dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto == null) {
            errors.put("global", "Le patient ne peut pas être null");
            return errors;
        }

        // Nom & Prénom
        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            errors.put("nom", "Le nom est obligatoire");
        }
        if (dto.getPrenom() == null || dto.getPrenom().trim().isEmpty()) {
            errors.put("prenom", "Le prénom est obligatoire");
        }

        // Email
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
                errors.put("email", "Format email invalide");
            }
        }

        // Téléphone
        if (dto.getTelephone() != null && !dto.getTelephone().isEmpty()) {
             if (!PHONE_PATTERN.matcher(dto.getTelephone()).matches()) {
                errors.put("telephone", "Format téléphone invalide (Ex: 06...)");
            }
        } else {
             errors.put("telephone", "Le téléphone est obligatoire");
        }

        // Date de naissance
        if (dto.getDateNaissance() != null && !dto.getDateNaissance().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dto.getDateNaissance());
                if (date.isAfter(LocalDate.now())) {
                    errors.put("dateNaissance", "La date de naissance ne peut pas être dans le futur");
                }
            } catch (DateTimeParseException e) {
                errors.put("dateNaissance", "Format date invalide (Requis: yyyy-MM-dd)");
            }
        } else {
            errors.put("dateNaissance", "La date de naissance est obligatoire");
        }

        // Sexe
        if (dto.getSexe() != null && !dto.getSexe().isEmpty()) {
            try {
                Sexe.valueOf(dto.getSexe());
            } catch (IllegalArgumentException e) {
                errors.put("sexe", "Valeur sexe invalide (MALE/FEMALE)");
            }
        } else {
            errors.put("sexe", "Le sexe est obligatoire");
        }
        
        // Assurance
        if (dto.getAssurance() != null && !dto.getAssurance().isEmpty()) {
             try {
                Assurance.valueOf(dto.getAssurance());
            } catch (IllegalArgumentException e) {
                errors.put("assurance", "Type d'assurance inconnu");
            }
        }

        return errors;
    }
}
