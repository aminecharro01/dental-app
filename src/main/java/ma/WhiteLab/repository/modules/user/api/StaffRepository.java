package ma.WhiteLab.repository.modules.user.api;

import ma.WhiteLab.entities.user.Staff;
import ma.WhiteLab.repository.common.CrudRepository;

import java.util.List;

public interface StaffRepository extends CrudRepository<Staff, Long> {

    // Recherche spécifique par cabinet
    List<Staff> findByCabinetId(Long cabinetId);

    // Lister tous les staff (déjà dans CrudRepository)
    List<Staff> findAll();
}
