package ma.WhiteLab.repository.modules.notifications.api;

import ma.WhiteLab.entities.user.Notification;
import ma.WhiteLab.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;
//auteur : Aymane Akarbich
public interface NotificationRepository extends CrudRepository<Notification, Long> {

    Optional<Notification> findByTitre(String titre);

    List<Notification> findByType(String type);

    List<Notification> findByPriorite(String priorite);

    long count();
}
