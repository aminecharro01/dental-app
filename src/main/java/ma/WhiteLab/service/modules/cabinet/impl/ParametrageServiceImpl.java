package ma.WhiteLab.service.modules.cabinet.impl;

import ma.WhiteLab.entities.cabinet.CabinetMedicale;
import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.CabinetMedicaleRepositoryImpl;
import ma.WhiteLab.service.modules.cabinet.api.ParametrageService;

import java.time.LocalDateTime;
import java.util.List;

public class ParametrageServiceImpl implements ParametrageService {

    private final CabinetMedicaleRepositoryImpl repo;

    public ParametrageServiceImpl() {
        this.repo = new CabinetMedicaleRepositoryImpl();
    }

    @Override
    public CabinetMedicale getCabinetActuel() {
        List<CabinetMedicale> cabinets = repo.findAll();

        if (cabinets.isEmpty()) {
            throw new RuntimeException("Aucun cabinet configuré");
        }

        // Mono-cabinet → retourne le premier
        return cabinets.get(0);
    }

    @Override
    public CabinetMedicale updateParametresCabinet(CabinetMedicale cabinet, String modifierPar) {
        if (cabinet == null || cabinet.getId() == null) {
            throw new IllegalArgumentException("Cabinet invalide");
        }

        cabinet.setModifierPar(modifierPar);
        cabinet.setDateMiseAJour(LocalDateTime.now());
        repo.update(cabinet);

        return repo.findById(cabinet.getId());
    }

    @Override
    public CabinetMedicale updateLogo(String logoPath, String modifierPar) {
        if (logoPath == null || logoPath.isBlank()) {
            throw new IllegalArgumentException("Logo invalide");
        }

        CabinetMedicale cabinet = getCabinetActuel();
        cabinet.setLogo(logoPath);
        cabinet.setModifierPar(modifierPar);
        cabinet.setDateMiseAJour(LocalDateTime.now());
        repo.update(cabinet);

        return cabinet;
    }

    @Override
    public void removeLogo(String modifierPar) {
        CabinetMedicale cabinet = getCabinetActuel();
        cabinet.setLogo(null);
        cabinet.setModifierPar(modifierPar);
        cabinet.setDateMiseAJour(LocalDateTime.now());
        repo.update(cabinet);
    }

    @Override
    public boolean isCabinetConfigured() {
        return !repo.findAll().isEmpty();
    }

    @Override
    public CabinetMedicale initialiserCabinet(CabinetMedicale cabinet, String creePar) {
        if (cabinet == null) {
            throw new IllegalArgumentException("Cabinet ne peut pas être null");
        }
        if (creePar == null || creePar.isBlank()) {
            throw new IllegalArgumentException("Utilisateur créateur requis");
        }

        cabinet.setCreePar(creePar);
        cabinet.setDateCreation(LocalDateTime.now());
        repo.create(cabinet);

        return cabinet;
    }

    @Override
    public boolean isMonoCabinet() {
        return repo.findAll().size() <= 1;
    }
}
