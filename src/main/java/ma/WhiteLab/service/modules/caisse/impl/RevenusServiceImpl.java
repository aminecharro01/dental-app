package ma.WhiteLab.service.modules.caisse.impl;

import ma.WhiteLab.entities.cabinet.Revenus;
import ma.WhiteLab.repository.modules.cabinet.api.RevenusRepository;
import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.RevenusRepositoryImpl;
import ma.WhiteLab.service.modules.caisse.api.RevenusService;

import java.time.LocalDateTime;
import java.util.List;

public class RevenusServiceImpl implements RevenusService {

    private final RevenusRepository revenusRepository;

    public RevenusServiceImpl() {
        this.revenusRepository = new RevenusRepositoryImpl();
    }

    @Override
    public List<Revenus> findAll() {
        return revenusRepository.findAll();
    }

    @Override
    public Revenus findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid revenus ID");
        }
        return revenusRepository.findById(id);
    }

    @Override
    public void create(Revenus revenus) {
        if (revenus == null) {
            throw new IllegalArgumentException("Revenus is null");
        }
        revenusRepository.create(revenus);
    }

    @Override
    public void update(Revenus revenus) {
        if (revenus == null || revenus.getId() == null) {
            throw new IllegalArgumentException("Revenus or ID is null");
        }
        revenusRepository.update(revenus);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid revenus ID");
        }
        revenusRepository.deleteById(id);
    }

    @Override
    public List<Revenus> findByCabinetId(Long cabinetId) {
        if (cabinetId == null || cabinetId <= 0) {
            throw new IllegalArgumentException("Invalid cabinet ID");
        }
        return revenusRepository.findByCabinetId(cabinetId);
    }

    @Override
    public List<Revenus> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return revenusRepository.findByDateBetween(start, end);
    }

    @Override
    public List<Revenus> findByTitre(String titre) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Titre is empty");
        }
        return revenusRepository.findByTitre(titre);
    }

    // =======================
    // Business
    // =======================
    @Override
    public double calculateTotalRevenus(Long cabinetId) {
        return findByCabinetId(cabinetId)
                .stream()
                .mapToDouble(r -> r.getMontant() != null ? r.getMontant() : 0.0)
                .sum();
    }
}
