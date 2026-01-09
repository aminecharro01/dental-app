package ma.WhiteLab.service.modules.caisse.test;

import ma.WhiteLab.entities.dossierMedical.Consultation;
import ma.WhiteLab.entities.dossierMedical.Facture;
import ma.WhiteLab.entities.enums.StatutFacture;
import ma.WhiteLab.service.modules.caisse.api.FactureService;
import ma.WhiteLab.service.modules.caisse.impl.FactureServiceImpl;

import java.time.LocalDate;
import java.util.List;

public class TestFactureService {

    public static void main(String[] args) {

        FactureService factureService = new FactureServiceImpl();

        // ======================
        // Consultation existante
        // ======================
        Consultation consultation = new Consultation();
        consultation.setId(1L); // ⚠️ doit exister en DB

        // ======================
        // Création facture
        // ======================
        Facture facture = new Facture();
        facture.setConsultation(consultation);
        facture.setTotalFact(1000f);
        facture.setTotalPaye(300f);
        facture.setDate(LocalDate.now());
        facture.setCreePar("admin");

        factureService.create(facture);
        System.out.println("✅ Facture créée ID = " + facture.getId());
        System.out.println("💰 Reste = " + facture.getReste());
        System.out.println("📌 Statut = " + facture.getStatut());

        // ======================
        // Paiement partiel
        // ======================
        facture.setTotalPaye(700f);
        factureService.update(facture);

        Facture updated = factureService.findById(facture.getId());
        System.out.println("✏️ Reste après paiement = " + updated.getReste());
        System.out.println("📌 Statut = " + updated.getStatut());

        // ======================
        // Marquer comme payée
        // ======================
        factureService.markAsPaid(facture.getId());

        Facture paid = factureService.findById(facture.getId());
        System.out.println("✅ Facture payée");
        System.out.println("💰 Reste = " + paid.getReste());
        System.out.println("📌 Statut = " + paid.getStatut());

        // ======================
        // Recherche par statut
        // ======================
        List<Facture> payees = factureService.findByStatut(StatutFacture.PAYEE);
        System.out.println("📊 Factures PAYEES = " + payees.size());

        // ======================
        // Suppression
        // ======================
        factureService.delete(facture.getId());
        System.out.println("🗑️ Facture supprimée");

        System.out.println("✅ TEST FactureService terminé");
    }
}
