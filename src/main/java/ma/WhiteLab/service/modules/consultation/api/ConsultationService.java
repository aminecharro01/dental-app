package ma.WhiteLab.service.modules.consultation.api;

import ma.WhiteLab.service.modules.consultation.dto.ConsultationDTO;
import ma.WhiteLab.common.exceptions.ValidationException;

public interface ConsultationService {
    ConsultationDTO create(ConsultationDTO dto) throws ValidationException;
    ConsultationDTO findById(Long id);
    
    // Gestion spécifique
    void addIntervention(Long consultationId, ma.WhiteLab.service.modules.consultation.dto.InterventionDTO interventionDto);
    void addActe(Long consultationId, ma.WhiteLab.service.modules.consultation.dto.ActeDTO acteDto);
}
