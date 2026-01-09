package ma.WhiteLab.service.modules.caisse.test;

import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.ChargesRepositoryImpl;
import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.RevenusRepositoryImpl;
import ma.WhiteLab.service.modules.caisse.api.CaisseService;
import ma.WhiteLab.service.modules.caisse.api.ChargesService;
import ma.WhiteLab.service.modules.caisse.api.RevenusService;
import ma.WhiteLab.service.modules.caisse.dto.CaisseReportDTO;
import ma.WhiteLab.service.modules.caisse.impl.CaisseServiceImpl;
import ma.WhiteLab.service.modules.caisse.impl.ChargesServiceImpl;
import ma.WhiteLab.service.modules.caisse.impl.RevenusServiceImpl;

public class TestCaisseService {

    public static void main(String[] args) {

        // ==========================
        // Initialisation des services
        // ==========================
        RevenusService revenusService = new RevenusServiceImpl();
        ChargesService chargesService = new ChargesServiceImpl();

        CaisseService caisseService = new CaisseServiceImpl(
                revenusService,
                chargesService,
                new RevenusRepositoryImpl(),
                new ChargesRepositoryImpl()
        );

        Long cabinetId = 23L; // ⚠️ doit exister en base

        // ==========================
        // 1. Statistiques globales
        // ==========================
        System.out.println("===== STATISTIQUES GLOBALES =====");

        CaisseReportDTO globalStats = caisseService.getCaisseStats(cabinetId);

        System.out.println("Total Revenus : " + globalStats.getTotalRevenus());
        System.out.println("Total Charges : " + globalStats.getTotalCharges());
        System.out.println("Solde         : " + globalStats.getSolde());

        // ==========================
        // 2. Calcul du profit
        // ==========================
        System.out.println("\n===== PROFIT =====");
        double profit = caisseService.calculateProfit(cabinetId);
        System.out.println("Profit : " + profit);

        // ==========================
        // 3. Rapport mensuel
        // ==========================
        System.out.println("\n===== RAPPORT MENSUEL =====");

        int year = 2025;
        int month = 1;

        CaisseReportDTO monthlyReport =
                caisseService.getMonthlyCaisseReport(cabinetId, year, month);

        System.out.println("Période       : " + month + "/" + year);
        System.out.println("Total Revenus : " + monthlyReport.getTotalRevenus());
        System.out.println("Total Charges : " + monthlyReport.getTotalCharges());
        System.out.println("Solde         : " + monthlyReport.getSolde());

        System.out.println("\n✅ Test CaisseService terminé avec succès");
    }
}
