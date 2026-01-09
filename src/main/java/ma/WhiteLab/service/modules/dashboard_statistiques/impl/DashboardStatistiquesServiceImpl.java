package ma.WhiteLab.service.modules.dashboard_statistiques.impl;

import ma.WhiteLab.entities.cabinet.Statistiques;
import ma.WhiteLab.service.modules.dashboard_statistiques.api.DashboardStatistiquesService;
import ma.WhiteLab.service.modules.dashboard_statistiques.dto.*;
import ma.WhiteLab.service.modules.dashboard_statistiques.api.StatistiquesService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardStatistiquesServiceImpl implements DashboardStatistiquesService {

    private final StatistiquesService statistiquesService;

    public DashboardStatistiquesServiceImpl(StatistiquesService statistiquesService) {
        this.statistiquesService = statistiquesService;
    }

    /* ================= Statistiques globales ================= */

    @Override
    public DashboardGlobalDTO getStatistiquesGlobales(Long cabinetId, LocalDate dateDebut, LocalDate dateFin) {

        List<Statistiques> stats = statistiquesService.getStatistiquesParPeriode(cabinetId, dateDebut, dateFin);

        double caTotal = stats.stream()
                .mapToDouble(s -> s.getChiffre() != null ? s.getChiffre() : 0)
                .sum();

        long nombreActes = stats.size();
        long nombrePatients = stats.stream()
                .map(s -> s.getCabinetMedicale()) // ici remplacer par patientId si dispo
                .distinct()
                .count();

        long nombreRendezVous = stats.size(); // approximatif, remplacer si tu as un RendezVousService

        DashboardGlobalDTO dto = new DashboardGlobalDTO();
        dto.setChiffreAffairesTotal(caTotal);
        dto.setNombreActes(nombreActes);
        dto.setNombrePatients(nombrePatients);
        dto.setNombreRendezVous(nombreRendezVous);

        return dto;
    }

    /* ================= Chiffre d'affaires par mois ================= */

    @Override
    public List<ChiffreAffairesParMoisDTO> getChiffreAffairesParMois(Long cabinetId, int annee) {
        List<Statistiques> stats = statistiquesService.findByCabinet(cabinetId)
                .stream()
                .filter(s -> s.getDateCalcul() != null && s.getDateCalcul().getYear() == annee)
                .collect(Collectors.toList());

        Map<Integer, Double> caParMois = new HashMap<>();
        for (int mois = 1; mois <= 12; mois++) {
            int finalMois = mois;
            double total = stats.stream()
                    .filter(s -> s.getDateCalcul().getMonthValue() == finalMois)
                    .mapToDouble(s -> s.getChiffre() != null ? s.getChiffre() : 0)
                    .sum();
            caParMois.put(mois, total);
        }

        List<ChiffreAffairesParMoisDTO> list = new ArrayList<>();
        caParMois.forEach((mois, montant) -> list.add(new ChiffreAffairesParMoisDTO(mois, montant)));

        list.sort(Comparator.comparingInt(ChiffreAffairesParMoisDTO::getMois));
        return list;
    }

    /* ================= Top actes dentaires ================= */

    @Override
    public List<TopActeDTO> getTopActes(Long cabinetId, LocalDate dateDebut, LocalDate dateFin, int limit) {

        List<Statistiques> stats = statistiquesService.getStatistiquesParPeriode(cabinetId, dateDebut, dateFin);

        Map<String, TopActeDTO> actesMap = new HashMap<>();

        for (Statistiques s : stats) {
            String nomActe = s.getNom();
            if (nomActe == null) continue;

            TopActeDTO dto = actesMap.getOrDefault(nomActe, new TopActeDTO(nomActe, 0, 0.0));
            dto.setNombre(dto.getNombre() + 1);
            dto.setChiffreAffaires(dto.getChiffreAffaires() + (s.getChiffre() != null ? s.getChiffre() : 0));
            actesMap.put(nomActe, dto);
        }

        return actesMap.values()
                .stream()
                .sorted(Comparator.comparingLong(TopActeDTO::getNombre).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
