package ma.WhiteLab.service.modules.certificat.impl;

import ma.WhiteLab.common.validation.Validator;
import ma.WhiteLab.service.modules.certificat.dto.CertificatDTO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class CertificatValidator implements Validator<CertificatDTO> {
    @Override
    public Map<String, String> validate(CertificatDTO dto) {
        Map<String, String> errors = new HashMap<>();
        if (dto == null) {
            errors.put("global", "Certificat null");
            return errors;
        }

        if (dto.getDureeRepos() <= 0) {
            errors.put("dureeRepos", "La durée doit être positive");
        }
        
        // Validation dates
        if (dto.getDateDebut() == null) {
            errors.put("dateDebut", "Date de début requise");
        } else {
             try {
                LocalDate.parse(dto.getDateDebut());
            } catch (DateTimeParseException e) {
                errors.put("dateDebut", "Format date invalide");
            }
        }

        return errors;
    }
}
