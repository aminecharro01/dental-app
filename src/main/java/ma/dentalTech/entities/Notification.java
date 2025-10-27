package ma.dentalTech.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class Notification implements Serializable {

    private Long id;
    private Titre titre;
    private String message;
    private LocalDate date;
    private LocalTime time;
    private Type type;
    private Priorite priorite;

    public enum Titre {
        RAPPEL_RDV,
        CONFIRMATION_RDV,
        ANNULATION_RDV,
        NOUVEAU_MESSAGE
    }

    public enum Type {
        INFO,
        ALERTE,
        ERREUR
    }

    public enum Priorite {
        HAUTE,
        MOYENNE,
        BASSE
    }
}
