package ma.WhiteLab.service.modules.cabinet.impl;

import ma.WhiteLab.entities.cabinet.CabinetMedicale;
import ma.WhiteLab.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.WhiteLab.service.modules.cabinet.api.CabinetMedicalService;
import ma.WhiteLab.service.modules.cabinet.api.CabinetMedicalValidator;
import ma.WhiteLab.service.modules.cabinet.dto.CabinetSummaryDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des cabinets médicaux.
 */
public class CabinetMedicalServiceImpl implements CabinetMedicalService {

    private final CabinetMedicaleRepository cabinetRepo;
    private final CabinetMedicalValidator cabinetValidator;

    public CabinetMedicalServiceImpl(
            CabinetMedicaleRepository cabinetRepo,
            CabinetMedicalValidator cabinetValidator) {
        this.cabinetRepo = cabinetRepo;
        this.cabinetValidator = cabinetValidator;
    }

    // ============================
    // READ
    // ============================

    @Override
    public List<CabinetMedicale> findAll() {
        return cabinetRepo.findAll();
    }

    @Override
    public List<CabinetMedicale> findAllOrderByNom() {
        return cabinetRepo.findAllOrderByNom();
    }

    @Override
    public List<CabinetSummaryDTO> getAllSummaries() {
        return cabinetRepo.findAll().stream()
                .map(cabinet -> new CabinetSummaryDTO(
                        cabinet.getId(),
                        cabinet.getNom(),
                        cabinet.getCategorie(),
                        cabinet.getEmail(),
                        cabinet.getTel1(),
                        cabinet.getTel2(),
                        cabinet.getLogo() != null && !cabinet.getLogo().trim().isEmpty(),
                        cabinet.getSiteWeb() != null && !cabinet.getSiteWeb().trim().isEmpty(),
                        (cabinet.getInstagram() != null && !cabinet.getInstagram().trim().isEmpty()) ||
                                (cabinet.getFacebook() != null && !cabinet.getFacebook().trim().isEmpty())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CabinetMedicale> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cabinetRepo.findById(id));
    }

    @Override
    public Optional<CabinetMedicale> findByNom(String nom) {
        if (nom == null || nom.trim().isBlank()) return Optional.empty();
        return cabinetRepo.findByNom(nom.trim());
    }

    @Override
    public Optional<CabinetMedicale> findByEmail(String email) {
        if (email == null || email.trim().isBlank()) return Optional.empty();
        return cabinetRepo.findByEmail(email.trim());
    }

    @Override
    public List<CabinetMedicale> findByCategorie(String categorie) {
        if (categorie == null || categorie.trim().isBlank()) return List.of();
        return cabinetRepo.findByCategorie(categorie.trim());
    }

    @Override
    public List<CabinetMedicale> search(String keyword) {
        // 1. Vérification si le mot-clé est vide
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String key = keyword.trim().toLowerCase();

        // 2. Filtrage avec Stream
        return cabinetRepo.findAll().stream()
                .filter(c ->
                        (c.getNom() != null && c.getNom().toLowerCase().contains(key)) ||
                                (c.getEmail() != null && c.getEmail().toLowerCase().contains(key)) ||
                                (c.getTel1() != null && c.getTel1().contains(key)) || // .toLowerCase() inutile pour les numéros
                                (c.getTel2() != null && c.getTel2().contains(key))    // Suppression du || final
                )
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        try {
            return cabinetRepo.count(); // Si repo implémente COUNT SQL
        } catch (UnsupportedOperationException e) {
            return cabinetRepo.findAll().size(); // Fallback si non dispo
        }
    }


    // ============================
    // WRITE
    // ============================

    @Override
    public CabinetMedicale create(CabinetMedicale cabinet, String creePar) {
        if (cabinet == null) throw new IllegalArgumentException("Le cabinet ne peut pas être null");
        if (creePar == null || creePar.isBlank()) throw new IllegalArgumentException("Utilisateur créateur requis");

        cabinetValidator.validateForCreate(cabinet);

        cabinet.setDateCreation(LocalDateTime.now());
        cabinet.setCreePar(creePar);

        cabinetRepo.create(cabinet);
        return cabinet;
    }

    @Override
    public CabinetMedicale update(CabinetMedicale cabinet, String modifierPar) {
        if (cabinet == null || cabinet.getId() == null)
            throw new IllegalArgumentException("Cabinet invalide ou ID manquant");
        if (modifierPar == null || modifierPar.isBlank())
            throw new IllegalArgumentException("Utilisateur modificateur requis");

        CabinetMedicale existing = getCabinetOrThrow(cabinet.getId());

        cabinetValidator.validateForUpdate(cabinet);

        cabinet.setDateMiseAJour(LocalDateTime.now());
        cabinet.setModifierPar(modifierPar);

        cabinetRepo.update(cabinet);
        return cabinet;
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID requis pour la suppression");
        getCabinetOrThrow(id); // lève exception si inexistant
        cabinetRepo.deleteById(id);
    }

    // ============================
    // Méthode utilitaire interne
    // ============================

    private CabinetMedicale getCabinetOrThrow(Long id) {
        CabinetMedicale cabinet = cabinetRepo.findById(id);
        if (cabinet == null) throw new IllegalArgumentException("Cabinet introuvable avec l'ID : " + id);
        return cabinet;
    }

    // ============================
    // DTO interne pour countByCategorie
    // ============================

    public static class CabinetCategorieCountDTO {
        private final String categorie;
        private final long count;

        public CabinetCategorieCountDTO(String categorie, long count) {
            this.categorie = categorie;
            this.count = count;
        }

        public String getCategorie() {
            return categorie;
        }

        public long getCount() {
            return count;
        }
    }
}
