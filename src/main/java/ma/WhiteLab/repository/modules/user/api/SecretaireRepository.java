package ma.WhiteLab.repository.modules.user.api;

import ma.WhiteLab.entities.user.Secretaire;
import ma.WhiteLab.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SecretaireRepository extends CrudRepository<Secretaire, Long> {

    // Recherche par numéro CNS
    Optional<Secretaire> findByNumCNS(String numCNS);

    // Lister tous les secrétaires (hérité de CrudRepository)
    List<Secretaire> findAll();
}
