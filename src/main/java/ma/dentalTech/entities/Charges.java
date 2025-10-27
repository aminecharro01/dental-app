package ma.dentalTech.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class Charges implements Serializable {

    private String titre;
    private String description;
    private Double montant;
    private LocalDateTime date;
}
