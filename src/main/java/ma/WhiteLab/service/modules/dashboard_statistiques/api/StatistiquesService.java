package ma.WhiteLab.service.modules.dashboard_statistiques.api;

import ma.WhiteLab.entities.cabinet.Statistiques;

import java.time.LocalDate;
import java.util.List;

public interface StatistiquesService {

    /* ================= CRUD ================= */

    Statistiques findById(Long id);

    List<Statistiques> findAll();

    List<Statistiques> findByCabinet(Long cabinetId);

    void creerStatistique(Statistiques statistiques);

    void modifierStatistique(Statistiques statistiques);

    void supprimerStatistique(Long id);

    /* ================= MÉTIER / DASHBOARD ================= */

    // 🔹 Chiffre d'affaires sur une période
    double calculerChiffreAffaires(
            Long cabinetId,
            LocalDate dateDebut,
            LocalDate dateFin
    );

    // 🔹 Nombre de statistiques par catégorie
    long compterParCategorie(
            Long cabinetId,
            String categorie
    );

    // 🔹 Statistiques d’un cabinet sur une période
    List<Statistiques> getStatistiquesParPeriode(
            Long cabinetId,
            LocalDate dateDebut,
            LocalDate dateFin
    );
}
