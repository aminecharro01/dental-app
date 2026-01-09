package ma.WhiteLab.service.modules.consultation.dto;

import ma.WhiteLab.service.modules.consultation.dto.InterventionDTO;
import ma.WhiteLab.service.modules.consultation.dto.ActeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ConsultationDTO {
    private Long id;
    private String dateConsultation; // ISO
    private String motif;
    private String diagnostic;
    private double montant;
    
    // Links
    private Long dossierMedicalId;
    private Long medecinId;

    // Nested Lists
    private List<InterventionDTO> interventions;
    private List<ActeDTO> actes;
}
