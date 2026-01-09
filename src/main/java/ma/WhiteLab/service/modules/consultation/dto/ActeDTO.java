package ma.WhiteLab.service.modules.consultation.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ActeDTO {
    private Long id;
    private String libelle;
    private double prixBase;
    private String categorie;
}
