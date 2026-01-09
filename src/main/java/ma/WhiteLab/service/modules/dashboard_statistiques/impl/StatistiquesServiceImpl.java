package ma.WhiteLab.service.modules.dashboard_statistiques.impl;

import ma.WhiteLab.entities.cabinet.Statistiques;
import ma.WhiteLab.repository.modules.cabinet.api.StatistiquesRepository;
import ma.WhiteLab.service.modules.dashboard_statistiques.api.StatistiquesService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class StatistiquesServiceImpl implements StatistiquesService {

    private final StatistiquesRepository statistiquesRepository;

    public StatistiquesServiceImpl(StatistiquesRepository statistiquesRepository) {
        this.statistiquesRepository = statistiquesRepository;
    }

    /* ================= CRUD ================= */

    @Override
    public Statistiques findById(Long id) {
        return statistiquesRepository.findById(id);
    }

    @Override
    public List<Statistiques> findAll() {
        return statistiquesRepository.findAll();
    }

    @Override
    public List<Statistiques> findByCabinet(Long cabinetId) {
        return statistiquesRepository.findByCabinetId(cabinetId);
    }

    @Override
    public void creerStatistique(Statistiques s) {
        statistiquesRepository.create(s);
    }

    @Override
    public void modifierStatistique(Statistiques s) {
        statistiquesRepository.update(s);
    }

    @Override
    public void supprimerStatistique(Long id) {
        statistiquesRepository.deleteById(id);
    }

    /* ================= MÉTIER ================= */

    @Override
    public double calculerChiffreAffaires(
            Long cabinetId,
            LocalDate dateDebut,
            LocalDate dateFin
    ) {
        return statistiquesRepository.findByCabinetId(cabinetId)
                .stream()
                .filter(s -> s.getDateCalcul() != null)
                .filter(s ->
                        !s.getDateCalcul().isBefore(dateDebut) &&
                                !s.getDateCalcul().isAfter(dateFin)
                )
                .mapToDouble(s -> s.getChiffre() != null ? s.getChiffre() : 0)
                .sum();
    }

    @Override
    public long compterParCategorie(Long cabinetId, String categorie) {
        return statistiquesRepository.findByCabinetId(cabinetId)
                .stream()
                .filter(s -> categorie.equalsIgnoreCase(
                        s.getCategorie() != null ? s.getCategorie().name() : "")
                )
                .count();
    }

    @Override
    public List<Statistiques> getStatistiquesParPeriode(
            Long cabinetId,
            LocalDate dateDebut,
            LocalDate dateFin
    ) {
        return statistiquesRepository.findByCabinetId(cabinetId)
                .stream()
                .filter(s -> s.getDateCalcul() != null)
                .filter(s ->
                        !s.getDateCalcul().isBefore(dateDebut) &&
                                !s.getDateCalcul().isAfter(dateFin)
                )
                .collect(Collectors.toList());
    }
}
