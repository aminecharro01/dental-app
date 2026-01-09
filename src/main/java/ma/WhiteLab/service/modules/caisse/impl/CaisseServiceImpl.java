package ma.WhiteLab.service.modules.caisse.impl;

import ma.WhiteLab.entities.cabinet.Charges;
import ma.WhiteLab.entities.cabinet.Revenus;
import ma.WhiteLab.repository.modules.cabinet.api.ChargesRepository;
import ma.WhiteLab.repository.modules.cabinet.api.RevenusRepository;
import ma.WhiteLab.service.modules.caisse.api.CaisseService;
import ma.WhiteLab.service.modules.caisse.api.ChargesService;
import ma.WhiteLab.service.modules.caisse.api.RevenusService;
import ma.WhiteLab.service.modules.caisse.dto.CaisseReportDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class CaisseServiceImpl implements CaisseService {

    private final RevenusService revenusService;
    private final ChargesService chargesService;
    private final RevenusRepository revenusRepository;
    private final ChargesRepository chargesRepository;

    public CaisseServiceImpl(RevenusService revenusService,
                             ChargesService chargesService,
                             RevenusRepository revenusRepository,
                             ChargesRepository chargesRepository) {
        this.revenusService = revenusService;
        this.chargesService = chargesService;
        this.revenusRepository = revenusRepository;
        this.chargesRepository = chargesRepository;
    }

    // ======================
    // GLOBAL STATS
    // ======================
    @Override
    public CaisseReportDTO getCaisseStats(Long cabinetId) {
        if (cabinetId == null || cabinetId <= 0) {
            throw new IllegalArgumentException("Invalid cabinet ID");
        }

        BigDecimal totalRevenus = BigDecimal.valueOf(
                revenusService.calculateTotalRevenus(cabinetId)
        );

        BigDecimal totalCharges = BigDecimal.valueOf(
                chargesService.calculateTotalCharges(cabinetId)
        );

        CaisseReportDTO dto = new CaisseReportDTO();
        dto.setTotalRevenus(totalRevenus);
        dto.setTotalCharges(totalCharges);
        dto.setSolde(totalRevenus.subtract(totalCharges));

        return dto;
    }

    @Override
    public double calculateProfit(Long cabinetId) {
        return getCaisseStats(cabinetId).getSolde().doubleValue();
    }

    // ======================
    // MONTHLY REPORT
    // ======================
    @Override
    public CaisseReportDTO getMonthlyCaisseReport(Long cabinetId, int year, int month) {
        if (cabinetId == null || cabinetId <= 0) {
            throw new IllegalArgumentException("Invalid cabinet ID");
        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Revenus> revenus = revenusRepository.findByCabinetId(cabinetId).stream()
                .filter(r -> r.getDate() != null &&
                        !r.getDate().isBefore(start) &&
                        !r.getDate().isAfter(end))
                .collect(Collectors.toList());

        List<Charges> charges = chargesRepository.findByCabinetId(cabinetId).stream()
                .filter(c -> c.getDate() != null &&
                        !c.getDate().isBefore(start) &&
                        !c.getDate().isAfter(end))
                .collect(Collectors.toList());

        BigDecimal totalRevenus = revenus.stream()
                .map(r -> BigDecimal.valueOf(r.getMontant() != null ? r.getMontant() : 0))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCharges = charges.stream()
                .map(c -> BigDecimal.valueOf(c.getMontant() != null ? c.getMontant() : 0))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CaisseReportDTO dto = new CaisseReportDTO();
        dto.setYear(year);
        dto.setMonth(month);
        dto.setTotalRevenus(totalRevenus);
        dto.setTotalCharges(totalCharges);
        dto.setSolde(totalRevenus.subtract(totalCharges));

        return dto;
    }
}
