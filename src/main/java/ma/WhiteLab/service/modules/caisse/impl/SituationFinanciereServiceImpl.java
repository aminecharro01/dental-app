package ma.WhiteLab.service.modules.caisse.impl;

import ma.WhiteLab.entities.dossierMedical.SituationFinanciere;
import ma.WhiteLab.entities.enums.PromoStatus;
import ma.WhiteLab.entities.enums.Status;
import ma.WhiteLab.repository.modules.dossierMedical.api.SituationFinanciereRepository;
import ma.WhiteLab.service.modules.caisse.api.SituationFinanciereService;

import java.util.List;
import java.util.Optional;

public class SituationFinanciereServiceImpl implements SituationFinanciereService {

    private final SituationFinanciereRepository repo;

    public SituationFinanciereServiceImpl(SituationFinanciereRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<SituationFinanciere> findAll() {
        return repo.findAll();
    }

    @Override
    public SituationFinanciere findById(Long id) {
        if (id == null) return null;
        return repo.findById(id);
    }

    @Override
    public void create(SituationFinanciere sf) {
        if (sf == null) throw new IllegalArgumentException("SituationFinanciere invalide");
        repo.create(sf);
    }

    @Override
    public void update(SituationFinanciere sf) {
        if (sf == null || sf.getId() == null) throw new IllegalArgumentException("SituationFinanciere invalide");
        repo.update(sf);
    }

    @Override
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("ID invalide");
        repo.deleteById(id);
    }

    @Override
    public List<SituationFinanciere> findByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return repo.findByDossierId(dossierId);
    }

    @Override
    public List<SituationFinanciere> findByStatus(Status status) {
        if (status == null) return List.of();
        return repo.findByStatus(status.name());
    }

    @Override
    public List<SituationFinanciere> findByPromo(PromoStatus promoStatus) {
        if (promoStatus == null) return List.of();
        return repo.findByPromo(promoStatus.name());
    }

    // ============================
    // Business methods
    // ============================
    @Override
    public void calculateCredit(SituationFinanciere sf) {
        if (sf == null) return;

        // assure que totalDesActes et totalPaye ne sont pas null
        Float totalDesActes = Optional.ofNullable(sf.getTotalDesActes()).orElse(0f);
        Float totalPaye = Optional.ofNullable(sf.getTotalPaye()).orElse(0f);

        // calcul du crédit
        sf.setCredit(totalDesActes - totalPaye);
    }



    @Override
    public SituationFinanciere findByDossierMedicalId(Long dossierId) {
        List<SituationFinanciere> list = findByDossierId(dossierId);
        return list.isEmpty() ? null : list.get(0);
    }
}
