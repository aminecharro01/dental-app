package ma.WhiteLab.service.modules.caisse.impl;

import ma.WhiteLab.entities.dossierMedical.Facture;
import ma.WhiteLab.entities.enums.StatutFacture;
import ma.WhiteLab.repository.modules.dossierMedical.api.FactureRepository;
import ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL.FactureRepositoryImpl;
import ma.WhiteLab.service.modules.caisse.api.FactureService;

import java.time.LocalDate;
import java.util.List;

public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;

    public FactureServiceImpl() {
        this.factureRepository = new FactureRepositoryImpl();
    }

    // ======================
    // CRUD
    // ======================
    @Override
    public List<Facture> findAll() {
        return factureRepository.findAll();
    }

    @Override
    public Facture findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid facture ID");
        }
        return factureRepository.findById(id);
    }

    @Override
    public void create(Facture facture) {
        if (facture == null) {
            throw new IllegalArgumentException("Facture is null");
        }

        calculateReste(facture);
        factureRepository.create(facture);
    }

    @Override
    public void update(Facture facture) {
        if (facture == null || facture.getId() == null) {
            throw new IllegalArgumentException("Facture or ID is null");
        }

        calculateReste(facture);
        factureRepository.update(facture);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid facture ID");
        }
        factureRepository.deleteById(id);
    }

    // ======================
    // FINDERS
    // ======================
    @Override
    public List<Facture> findByConsultationId(Long consultationId) {
        if (consultationId == null || consultationId <= 0) {
            throw new IllegalArgumentException("Invalid consultation ID");
        }
        return factureRepository.findByConsultationId(consultationId);
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Statut is null");
        }
        return factureRepository.findByStatut(statut.name());
    }

    @Override
    public List<Facture> findByDateBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return factureRepository.findByDateBetween(start, end);
    }

    // ======================
    // BUSINESS
    // ======================
    @Override
    public void markAsPaid(Long factureId) {
        Facture facture = findById(factureId);

        facture.setTotalPaye(facture.getTotalFact());
        facture.setReste(0f);
        facture.setStatut(StatutFacture.PAYEE);

        factureRepository.update(facture);
    }

    public void calculateReste(Facture facture) {

        float totalFact = facture.getTotalFact();   // jamais null
        float totalPaye = facture.getTotalPaye();   // jamais null

        float reste = totalFact - totalPaye;
        facture.setReste(reste);

        if (reste <= 0f) {
            facture.setStatut(StatutFacture.PAYEE);
        } else {
            facture.setStatut(StatutFacture.EN_ATTENTE);
        }
    }
}
