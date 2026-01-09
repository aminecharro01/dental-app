package ma.WhiteLab.service.modules.cabinet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CabinetSummaryDTO {

    private Long id;
    private String nom;
    private String categorie;
    private String email;
    private String tel1;
    private String tel2; // Ajout de tel2 pour cohérence avec le service
    private boolean hasLogo;
    private boolean hasSiteWeb;
    private boolean hasInstagramOrFacebook;

}
