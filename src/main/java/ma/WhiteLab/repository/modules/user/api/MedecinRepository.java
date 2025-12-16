package ma.WhiteLab.repository.modules.user.api;

import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MedecinRepository extends CrudRepository<Medecin, Long> {

    // Recherche par email
    Optional<Medecin> findByEmail(String email);

    // Recherche par spécialité
    List<Medecin> findBySpecialite(String specialite);

    // Lister tous les médecins (hérité de CrudRepository)
    List<Medecin> findAll();
}
