package ma.WhiteLab.service.modules.dossierMedicale.impl; // Placing in existing package structure if possible, or new consultation package

// The user structure had service/modules/dossierMedicale/api - I should probably put Consultation there or create new module.
// User said "Consultation" service. I'll put it in `service/modules/consultation` to stay clean as per my plan.

import ma.WhiteLab.common.validation.Validator;
import ma.WhiteLab.service.modules.consultation.dto.ConsultationDTO;
import java.util.HashMap;
import java.util.Map;

public class ConsultationValidator implements Validator<ConsultationDTO> {
    
    @Override
    public Map<String, String> validate(ConsultationDTO dto) {
        Map<String, String> errors = new HashMap<>();
        
        if (dto == null) {
            errors.put("global", "Consultation null");
            return errors;
        }
        
        if (dto.getDateConsultation() == null) {
             errors.put("dateConsultation", "Date requise");
        }
        
        if (dto.getMotif() == null || dto.getMotif().isEmpty()) {
             errors.put("motif", "Motif requis");
        }

        // Validation des listes si nécessaire (ex: pas d'intervention sans description)
        if (dto.getInterventions() != null) {
            for (int i = 0; i < dto.getInterventions().size(); i++) {
                if (dto.getInterventions().get(i).getDescription() == null) {
                     errors.put("intervention_" + i, "Description intervention requise");
                }
            }
        }
        
        return errors;
    }
}
