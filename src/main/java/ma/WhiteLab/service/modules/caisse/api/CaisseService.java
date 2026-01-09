package ma.WhiteLab.service.modules.caisse.api;

import ma.WhiteLab.service.modules.caisse.dto.CaisseReportDTO;

public interface CaisseService {

    // Bilan global
    CaisseReportDTO getCaisseStats(Long cabinetId);

    // Profit global
    double calculateProfit(Long cabinetId);

    // Rapport mensuel
    CaisseReportDTO getMonthlyCaisseReport(Long cabinetId, int year, int month);
}
