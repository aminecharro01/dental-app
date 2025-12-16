package ma.WhiteLab.repository.modules.user.api;

import ma.WhiteLab.entities.user.Admin;
import ma.WhiteLab.repository.common.CrudRepository;

import java.util.Optional;
import java.util.List;

public interface AdminRepository extends CrudRepository<Admin, Long> {

    // Recherche avancée
    Optional<Admin> findByEmail(String email);

    // Optionnel : lister tous les admins (hérité de CrudRepository)
    List<Admin> findAll();
    List<Admin> findByNomOrPrenom(String username);
}
