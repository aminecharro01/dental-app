package ma.WhiteLab.service.modules.consultation.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class InterventionDTO {
    private Long id;
    private String description;
    private String type; // Chirurgie, Soin, etc.
    private String dent; // Optionnel (pour dentiste)
}
