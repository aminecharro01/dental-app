package ma.WhiteLab.repository.modules.dossierMedical.api;

import ma.WhiteLab.entities.dossierMedical.ActeMedical;
import ma.WhiteLab.repository.common.CrudRepository;

import java.util.Optional;

public interface ActeMedicalRepository extends CrudRepository<ActeMedical, Long> {
    Optional<ActeMedical> findByLibelle(String libelle);
}
