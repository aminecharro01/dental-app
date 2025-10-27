package ma.dentalTech.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class Role implements Serializable {

    private Long idRole;
    private Libelle libelle;
    private List<String> privileges;

    public enum Libelle {
        ADMIN,
        MEDECIN,
        SECRETAIRE
    }
}
