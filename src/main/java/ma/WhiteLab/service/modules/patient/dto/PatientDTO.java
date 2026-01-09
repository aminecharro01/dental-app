package ma.WhiteLab.service.modules.patient.dto;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class PatientDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String sexe; // String pour simplifier le transport, conversion vers Enum dans le service
    private String email;
    private String dateNaissance; // String (format ISO yyyy-MM-dd) ou LocalDate
    private String adresse;
    private String telephone;
    private String assurance; // String pour Enum Assurance

    // Champs calculés / affichage
    private String nomComplet;
    private int age;
    private String dateCreationFormatee;
}
