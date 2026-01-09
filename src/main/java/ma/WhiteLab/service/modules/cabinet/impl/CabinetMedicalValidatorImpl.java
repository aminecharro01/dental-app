package ma.WhiteLab.service.modules.cabinet.impl;

import ma.WhiteLab.entities.cabinet.CabinetMedicale;
import ma.WhiteLab.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.CabinetMedicaleRepositoryImpl;
import ma.WhiteLab.service.modules.cabinet.api.CabinetMedicalValidator;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Optional;

public class CabinetMedicalValidatorImpl implements CabinetMedicalValidator {

    private final CabinetMedicaleRepository cabinetRepo;

    public CabinetMedicalValidatorImpl() {
        this.cabinetRepo = new CabinetMedicaleRepositoryImpl();
    }

    @Override
    public void validateForCreate(CabinetMedicale cabinet) {
        validateCommonFields(cabinet);
        checkNomUnique(cabinet.getNom(), null);
        checkEmailUnique(cabinet.getEmail(), null);
    }

    @Override
    public void validateForUpdate(CabinetMedicale cabinet) {
        if (cabinet.getId() == null) {
            throw new IllegalArgumentException("L'ID du cabinet est requis pour la mise à jour");
        }
        validateCommonFields(cabinet);
        checkNomUnique(cabinet.getNom(), cabinet.getId());
        checkEmailUnique(cabinet.getEmail(), cabinet.getId());
    }

    @Override
    public void validateCommonFields(CabinetMedicale cabinet) {
        if (cabinet == null) {
            throw new IllegalArgumentException("Le cabinet ne peut pas être null");
        }
        if (cabinet.getNom() == null || cabinet.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du cabinet est obligatoire");
        }
        if (cabinet.getEmail() == null || cabinet.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email du cabinet est obligatoire");
        }
        if (!EmailValidator.getInstance().isValid(cabinet.getEmail())) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
    }

    @Override
    public void checkEmailUnique(String email, Long excludeId) {
        Optional<CabinetMedicale> existing = cabinetRepo.findByEmail(email);
        if (existing.isPresent() &&
                (excludeId == null || !existing.get().getId().equals(excludeId))) {
            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre cabinet");
        }
    }

    @Override
    public void checkNomUnique(String nom, Long excludeId) {
        Optional<CabinetMedicale> existing = cabinetRepo.findByNom(nom);
        if (existing.isPresent() &&
                (excludeId == null || !existing.get().getId().equals(excludeId))) {
            throw new IllegalArgumentException("Ce nom de cabinet existe déjà");
        }
    }
}
