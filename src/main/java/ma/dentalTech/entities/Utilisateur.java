package ma.dentalTech.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class Utilisateur implements Serializable {

    private Long idUser;
    private String nom;
    private String email;
    private String adresse;
    private String cin;
    private String tel;
    private Sexe sexe;
    private LocalDate dateNaissance;
    private String login;
    private String motDePasse;
    private LocalDate lastLoginDate;

    private List<Role> roles;
    private List<Notification> notifications;

    public enum Sexe {
        HOMME, FEMME
    }
}
