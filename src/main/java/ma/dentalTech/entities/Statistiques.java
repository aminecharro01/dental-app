package ma.dentalTech.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class Statistiques implements Serializable {

    private Long id;
    private String nom;
    private Categorie categorie;
    private Double chiffre;
    private LocalDate dateCalcul;

    public enum Categorie {
        FINANCIERE,
        PATIENT,
        ACTIVITE
    }
}
