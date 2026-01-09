package ma.WhiteLab.service.modules.certificat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CertificatDTO {
    private Long id;
    private String dateDebut; // String ISO
    private String dateFin; // String ISO
    private int dureeRepos;
    private String contenu;
    
    // IDs de liaison
    private Long consultationId;
    private Long dossierMedicalId;
    private String nomMedecin; // Pour l'impression
    private String nomPatient; // Pour l'impression
}
