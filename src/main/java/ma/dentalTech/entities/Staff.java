package ma.dentalTech.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class Staff extends Utilisateur implements Serializable {

    private Double salaire;
    private Double prime;
    private LocalDate dateRecrutement;
    private int soldeConge;
}
