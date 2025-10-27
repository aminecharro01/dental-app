package ma.dentalTech.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Medecin extends Staff implements Serializable {

    private String specialite;
    private AgendaMensuel agendaMensuel;
}
