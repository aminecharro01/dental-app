package ma.WhiteLab.repository.modules.user.api;

import ma.WhiteLab.entities.user.Notification;
import ma.WhiteLab.entities.user.Role;
import ma.WhiteLab.entities.user.Utilisateur;
import ma.WhiteLab.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;
//auteur : Aymane Akarbich

public interface UtilisateurRepository<T extends Utilisateur> extends CrudRepository<T, Long> {

    Optional<T> findByEmail(String email);

    Optional<T> findByTelephone(String telephone);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    long count();

    // ---- UTILISATEUR ↔ ROLE ----
    void addRoleToUtilisateur(Long utilisateurId, Long roleId);
    void removeRoleFromUtilisateur(Long utilisateurId, Long roleId);
    List<Role> getRolesOfUtilisateur(Long utilisateurId);

    // ---- UTILISATEUR ↔ NOTIFICATION ----
    void addNotificationToUtilisateur(Long utilisateurId, Long notificationId);
    void removeNotificationFromUtilisateur(Long utilisateurId, Long notificationId);
    List<Notification> getNotificationsOfUtilisateur(Long utilisateurId);

    // ---- DELETE CASCADE ----
    void deleteAllRolesForUtilisateur(Long utilisateurId);
    void deleteAllNotificationsForUtilisateur(Long utilisateurId);
}
