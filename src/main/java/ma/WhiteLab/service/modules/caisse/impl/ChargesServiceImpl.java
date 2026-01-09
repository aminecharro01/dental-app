package ma.WhiteLab.service.modules.caisse.impl;

import ma.WhiteLab.entities.cabinet.Charges;
import ma.WhiteLab.repository.modules.cabinet.api.ChargesRepository;
import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.ChargesRepositoryImpl;
import ma.WhiteLab.service.modules.caisse.api.ChargesService;

import java.time.LocalDateTime;
import java.util.List;

public class ChargesServiceImpl implements ChargesService {

    private final ChargesRepository chargesRepo;

    public ChargesServiceImpl() {
        this.chargesRepo = new ChargesRepositoryImpl();
    }

    // ==========================
    // READ
    // ==========================

    @Override
    public List<Charges> findAll() {
        return chargesRepo.findAll();
    }

    @Override
    public Charges findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID invalide");
        }
        return chargesRepo.findById(id);
    }

    @Override
    public List<Charges> findByCabinetId(Long cabinetId) {
        if (cabinetId == null) {
            throw new IllegalArgumentException("Cabinet ID invalide");
        }
        return chargesRepo.findByCabinetId(cabinetId);
    }

    @Override
    public List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Dates invalides");
        }
        return chargesRepo.findByDateBetween(start, end);
    }

    // ==========================
    // WRITE
    // ==========================

    @Override
    public void create(Charges charges) {
        if (charges == null) {
            throw new IllegalArgumentException("Charge invalide");
        }

        charges.setDateCreation(LocalDateTime.now());
        chargesRepo.create(charges);
    }

    @Override
    public void update(Charges charges) {
        if (charges == null || charges.getId() == null) {
            throw new IllegalArgumentException("Charge invalide");
        }

        charges.setDateMiseAJour(LocalDateTime.now());
        chargesRepo.update(charges);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID invalide");
        }
        chargesRepo.deleteById(id);
    }

    // ==========================
    // BUSINESS
    // ==========================

    @Override
    public double calculateTotalCharges(Long cabinetId) {
        if (cabinetId == null) {
            throw new IllegalArgumentException("Cabinet ID invalide");
        }

        return chargesRepo.findByCabinetId(cabinetId)
                .stream()
                .mapToDouble(c -> c.getMontant() != null ? c.getMontant() : 0.0)
                .sum();
    }
}
