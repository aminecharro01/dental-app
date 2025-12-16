package ma.WhiteLab.repository.test;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.entities.agenda.*;
import ma.WhiteLab.entities.cabinet.*;
import ma.WhiteLab.entities.dossierMedical.*;
import ma.WhiteLab.entities.enums.*;
import ma.WhiteLab.entities.patient.*;
import ma.WhiteLab.entities.user.*;
import ma.WhiteLab.repository.modules.agenda.api.*;
import ma.WhiteLab.repository.modules.cabinet.api.*;
import ma.WhiteLab.repository.modules.dossierMedical.api.*;
import ma.WhiteLab.repository.modules.notifications.api.NotificationRepository;
import ma.WhiteLab.repository.modules.patient.api.*;
import ma.WhiteLab.repository.modules.user.api.*;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;


public class Test {

    // IDs globaux pour chaînage
    private static Long cabinetId, adminId, medecinId, secretaireId;
    private static Long patientId, dossierId, consultationId, acteId, interventionId;
    private static Long factureId, situationId, rdvId, agendaId, medicamentId, ordonnanceId;
    private static Long statId;

    // Repositories
    private static CabinetMedicaleRepository cabinetRepo;
    private static ChargesRepository chargesRepo;
    private static RevenusRepository revenusRepo;
    private static StatistiquesRepository statsRepo;

    private static AdminRepository adminRepo;
    private static MedecinRepository medecinRepo;
    private static SecretaireRepository secretaireRepo;
    private static UtilisateurRepository utilisateurRepo;

    private static PatientRepository patientRepo;
    private static AntecedentRepository antecedentRepo;

    private static DossierMedicalRepository dossierRepo;
    private static ConsultationRepository consultationRepo;
    private static ActeMedicalRepository acteRepo;
    private static InterventionMedecinRepository interventionRepo;
    private static SituationFinanciereRepository situationRepo;
    private static FactureRepository factureRepo;
    private static CertificatRepository certificatRepo;

    private static AgendaMensuelRepository agendaRepo;
    private static RendezVousRepository rdvRepo;

    private static MedicamentRepository medicamentRepo;
    private static OrdonnanceRepository ordonnanceRepo;
    private static PrescriptionRepository prescriptionRepo;

    private static RoleRepository roleRepo;
    private static NotificationRepository notifRepo;

    public static void main(String[] args) {
        ApplicationContext ctx = new ApplicationContext("config/beans.properties");

        // Injection de tous les repositories
        cabinetRepo = ctx.getBean(CabinetMedicaleRepository.class);
        chargesRepo = ctx.getBean(ChargesRepository.class);
        revenusRepo = ctx.getBean(RevenusRepository.class);
        statsRepo = ctx.getBean(StatistiquesRepository.class);

        adminRepo = ctx.getBean(AdminRepository.class);
        medecinRepo = ctx.getBean(MedecinRepository.class);
        secretaireRepo = ctx.getBean(SecretaireRepository.class);
        utilisateurRepo = ctx.getBean(UtilisateurRepository.class);

        patientRepo = ctx.getBean(PatientRepository.class);
        antecedentRepo = ctx.getBean(AntecedentRepository.class);

        dossierRepo = ctx.getBean(DossierMedicalRepository.class);
        consultationRepo = ctx.getBean(ConsultationRepository.class);
        acteRepo = ctx.getBean(ActeMedicalRepository.class);
        interventionRepo = ctx.getBean(InterventionMedecinRepository.class);
        situationRepo = ctx.getBean(SituationFinanciereRepository.class);
        factureRepo = ctx.getBean(FactureRepository.class);
        certificatRepo = ctx.getBean(CertificatRepository.class);

        agendaRepo = ctx.getBean(AgendaMensuelRepository.class);
        rdvRepo = ctx.getBean(RendezVousRepository.class);

        medicamentRepo = ctx.getBean(MedicamentRepository.class);
        ordonnanceRepo = ctx.getBean(OrdonnanceRepository.class);
        prescriptionRepo = ctx.getBean(PrescriptionRepository.class);

        roleRepo = ctx.getBean(RoleRepository.class);
        notifRepo = ctx.getBean(NotificationRepository.class);

        try {
            insertionProcess();
            updateProcess();
            selectProcess();
            deleteProcess();
        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE PENDANT L'INSERTION :");
            e.printStackTrace();
        }
    }

    private static void insertionProcess() {
        section("DÉMARRAGE DE L'INSERTION COMPLÈTE DES DONNÉES DE TEST - WHITELAB 2025");

        // 1. Création du cabinet médical
        section("1. Création du Cabinet Médical");
        CabinetMedicale cabinet = CabinetMedicale.builder()
                .nom("Clinique WhiteLab - Casablanca Centre")
                .email("contact@whitelab.ma")
                .tel1("0522123456").tel2("0661123456")
                .siteWeb("https://whitelab.ma").instagram("@whitelab_ma")
                .facebook("whitelab.ma").description("Clinique moderne multidisciplinaire")
                .categorie("Générale").creePar("test-full")
                .build();
        cabinetRepo.create(cabinet);
        cabinetId = cabinet.getId();
        println("Cabinet créé → ID = " + cabinetId);

        // 2. Création des utilisateurs (Admin, Médecin, Secrétaire)
        section("2. Création des Utilisateurs");
        Admin admin = Admin.builder().nom("Admin").prenom("Principal").email("admin@whitelab.ma")
                .cin("AA111111").telephone("0600000000").dateNaissance(LocalDate.of(1970, 1, 1))
                .sexe(Sexe.Homme).motDePasse("admin2025").creePar("test-full").build();
        adminRepo.create(admin);
        adminId = admin.getId();
        println("Admin créé → ID = " + adminId);

        Medecin medecin = Medecin.builder().nom("Benali").prenom("Fatima").email("f.benali@whitelab.ma")
                .cin("BB222222").telephone("0600000001").dateNaissance(LocalDate.of(1985, 4, 12))
                .sexe(Sexe.Femme).motDePasse("med2025").salaire(18000.0).prime(3000.0)
                .dateRecrutement(LocalDate.of(2018, 6, 1)).soldeConge(28).cabinetMedicale(cabinet)
                .specialite("Pédiatrie").creePar("test-full").build();
        medecinRepo.create(medecin);
        medecinId = medecin.getId();
        println("Médecin créé → ID = " + medecinId);

        Secretaire secretaire = Secretaire.builder().nom("Rahali").prenom("Youssef").email("y.rahali@whitelab.ma")
                .cin("CC333333").telephone("0600000002").dateNaissance(LocalDate.of(1993, 11, 8))
                .sexe(Sexe.Homme).motDePasse("sec2025").salaire(8500.0).prime(1000.0)
                .dateRecrutement(LocalDate.of(2021, 9, 15)).soldeConge(22).cabinetMedicale(cabinet)
                .numCNS("CNS998877").commission(600.0).creePar("test-full").build();
        secretaireRepo.create(secretaire);
        secretaireId = secretaire.getId();
        println("Secrétaire créé → ID = " + secretaireId);

        // 3. Création du patient + antécédent
        section("3. Création du Patient et Antécédent");
        Patient patient = Patient.builder().nom("El Amrani").prenom("Amina").sexe(Sexe.Femme)
                .dateNaissance(LocalDate.of(1990, 7, 20)).telephone("0612345678")
                .email("amina.elamrani@gmail.com").adresse("Résidence Al Amal, Casablanca")
                .assurance(Assurance.CNSS).creePar("test-full").build();
        patientRepo.create(patient);
        patientId = patient.getId();
        println("Patient créé → ID = " + patientId);

        Antecedent diabete = Antecedent.builder().nom("Diabète de type 2").description("Sous metformine")
                .categorie(CategorieAntecedent.ANTECEDENT_CHIRURGICAL).niveauDeRisk(NiveauDeRisk.FAIBLE)
                .creePar("test-full").build();
        antecedentRepo.create(diabete);
        patientRepo.addAntecedentToPatient(patientId, diabete.getId());
        println("Antécédent 'Diabète' ajouté au patient");

        // 4. Dossier médical
        section("4. Création du Dossier Médical");
        DossierMedical dossier = DossierMedical.builder().pat(patient).medcine(medecin)
                .historique("Création automatique - Test complet 2025").creePar("test-full").build();
        dossierRepo.create(dossier);
        dossierId = dossier.getId();
        println("Dossier médical créé → ID = " + dossierId);

        // 5. Consultation + Acte + Intervention + Certificat
        section("5. Consultation, Acte, Intervention et Certificat");
        Consultation consultation = Consultation.builder().dossierMedical(dossier).date(LocalDateTime.now())
                .status(StatusConsultation.TERMINEE).notes("Consultation pédiatrique")
                .observationsMedecin("Croissance normale, vaccins à jour").creePar("test-full").build();
        consultationRepo.create(consultation);
        consultationId = consultation.getId();
        println("Consultation créée → ID = " + consultationId);

        Certificat certificat = Certificat.builder().dateDebut(LocalDate.now()).dateFin(LocalDate.now().plusDays(3))
                .dureeRepos(3).contenu("Repos médical prescrit pour fatigue")
                .consultation(consultation).dossierMedical(dossier).creePar("test-full").build();
        certificatRepo.create(certificat);
        println("Certificat médical créé → ID = " + certificat.getId());

        ActeMedical acte = ActeMedical.builder().libelle("Consultation pédiatrique").categorie("Pédiatrie")
                .prixDeBase(500.0f).creePar("test-full").build();
        acteRepo.create(acte);
        acteId = acte.getId();
        println("Acte médical créé → ID = " + acteId);

        InterventionMedecin intervention = InterventionMedecin.builder().consultation(consultation)
                .acteMedical(acte).prixDePatient(500.0).creePar("test-full").build();
        interventionRepo.create(intervention);
        interventionId = intervention.getId();
        println("Intervention créée → ID = " + interventionId);

        // 6. Agenda et Rendez-vous
        section("6. Agenda Mensuel et Rendez-vous");
        AgendaMensuel agenda = AgendaMensuel.builder().medecin(medecin).mois(Mois.JUIN)
                .joursNonDisponible(List.of(Jour.SAMEDI, Jour.DIMANCHE)).creePar("test-full").build();
        agendaRepo.create(agenda);
        agendaId = agenda.getId();
        println("Agenda mensuel créé → ID = " + agendaId);

        RendezVous rdv = RendezVous.builder().dossierMed(dossier).date(LocalDateTime.now().plusDays(10))
                .time(LocalTime.of(9, 30)).motif("Vaccination DTC").status(Status.ACTIVE)
                .consultation(consultation).creePar("test-full").build();
        rdvRepo.create(rdv);
        rdvId = rdv.getId();
        println("Rendez-vous créé → ID = " + rdvId);

        // 7. Médicaments, Ordonnance et Prescription
        section("7. Médicaments, Ordonnance et Prescription");
        Medicament paracetamol = Medicament.builder().nom("Doliprane 1000mg").labo("Sanofi")
                .forme(Forme.COMPRIME).prixUnitaire(18.0).remboursable(true).creePar("test-full").build();
        medicamentRepo.create(paracetamol);
        medicamentId = paracetamol.getId();
        println("Médicament créé → ID = " + medicamentId);

        Ordonnance ordonnance = Ordonnance.builder().dateOrdonnance(LocalDate.now())
                .consultation(consultation).dossierMedical(dossier).creePar("test-full").build();
        ordonnanceRepo.create(ordonnance);
        ordonnanceId = ordonnance.getId();
        println("Ordonnance créée → ID = " + ordonnanceId);

        Prescription prescription = Prescription.builder().ordonnance(ordonnance).medicament(paracetamol)
                .qte(20).frequence("1 matin et soir").duree(5).build();
        prescriptionRepo.create(prescription);
        println("Prescription ajoutée (20 Doliprane)");

        // 8. Statistiques
        section("8. Statistiques du Cabinet");
        Statistiques stat = Statistiques.builder()
                .nom("Chiffre d'affaires mensuel")
                .categorie(CategorieStatistique.FINANCIER)
                .chiffre(185000.0)
                .dateCalcul(LocalDate.now())
                .cabinetMedicale(cabinet)
                .creePar("test")
                .build();
        statsRepo.create(stat);
        statId = stat.getId();
        println("Statistiques créées → ID = " + statId);

        // 9. Finances (Charges, Revenus, Situation, Facture)
        section("9. Données Financières");
        Charges loyer = Charges.builder().titre("Loyer clinique").montant(25000.0)
                .date(LocalDateTime.now()).cabinetMedicale(cabinet).creePar("test-full").build();
        chargesRepo.create(loyer);
        println("Charge (loyer) enregistrée");

        Revenus revenus = Revenus.builder().titre("Consultations mai").montant(210000.0)
                .date(LocalDateTime.now()).cabinetMedicale(cabinet).creePar("test-full").build();
        revenusRepo.create(revenus);
        println("Revenu enregistré");

        SituationFinanciere sf = SituationFinanciere.builder().dossierMedical(dossier)
                .totalDesActes(5000f).totalPaye(3000f).credit(2000f).status(Status.ACTIVE)
                .enPromo(PromoStatus.AUCUNE).creePar("test-full").build();
        situationRepo.create(sf);
        situationId = sf.getId();
        println("Situation financière créée → ID = " + situationId);

        Facture facture = Facture.builder().consultation(consultation).sf(sf)
                .totalFact(5000f).totalPaye(3000f).reste(2000f)
                .date(LocalDate.now()).statut(StatutFacture.EN_ATTENTE).creePar("test-full").build();
        factureRepo.create(facture);
        factureId = facture.getId();
        println("Facture créée → ID = " + factureId);

        // 10. Rôles et Notifications + Liaisons
        section("10. Rôles et Notifications");
        Role roleAdmin = Role.builder().libelle(RoleR.ADMIN).privileges(List.of("ALL")).creePar("test").build();
        Role roleMed = Role.builder().libelle(RoleR.MEDECIN).privileges(List.of("PRESCRIRE", "CONSULTER_DOSSIER")).creePar("test").build();
        Role roleSec = Role.builder().libelle(RoleR.SECRETAIRE).privileges(List.of("GERER_RDV")).creePar("test").build();
        roleRepo.create(roleAdmin);
        roleRepo.create(roleMed);
        roleRepo.create(roleSec);

        utilisateurRepo.addRoleToUtilisateur(adminId, roleAdmin.getId());
        utilisateurRepo.addRoleToUtilisateur(medecinId, roleMed.getId());
        utilisateurRepo.addRoleToUtilisateur(secretaireId, roleSec.getId());
        println("Rôles assignés aux utilisateurs");

        Notification notif1 = Notification.builder().titre(TitreNotification.RAPPEL).message("RDV demain à 10h")
                .type(TypeNotification.SYSTEME).priorite(PrioriteNotification.HAUTE).creePar("test").build();
        Notification notif2 = Notification.builder().titre(TitreNotification.INFO).message("Mise à jour disponible")
                .type(TypeNotification.SYSTEME).priorite(PrioriteNotification.BASSE).creePar("test").build();
        notifRepo.create(notif1);
        notifRepo.create(notif2);

        utilisateurRepo.addNotificationToUtilisateur(medecinId, notif1.getId());
        utilisateurRepo.addNotificationToUtilisateur(secretaireId, notif2.getId());
        println("Notifications créées et assignées");

        // Résumé final
        section("RÉSUMÉ FINAL DES IDs");
        println("Cabinet ID         : " + cabinetId);
        println("Admin ID           : " + adminId);
        println("Médecin ID         : " + medecinId);
        println("Secrétaire ID      : " + secretaireId);
        println("Patient ID         : " + patientId);
        println("Dossier ID         : " + dossierId);
        println("Consultation ID    : " + consultationId);
        println("Facture ID         : " + factureId);
        println("RDV ID             : " + rdvId);
        println("Statistiques ID    : " + statId);
        println("Total patients     : " + patientRepo.count());
    }

    private static void section(String title) {
        System.out.println("\n" + "═".repeat(90));
        System.out.println("  " + title);
        System.out.println("═".repeat(90));
    }

    private static void println(String msg) {
        System.out.println(" → " + msg);
    }

    private static void updateProcess() {
        section("MISE À JOUR DE TOUTES LES ENTITÉS (UPDATE TEST)");
        // 1. Cabinet
        CabinetMedicale cabinetToUpdate = cabinetRepo.findById(cabinetId);
        cabinetToUpdate.setNom("Clinique WhiteLab - Casablanca Centre (MIS À JOUR)");
        cabinetToUpdate.setEmail("nouveau.contact@whitelab.ma");
        cabinetToUpdate.setTel1("0522998877");
        cabinetToUpdate.setSiteWeb("https://nouveau.whitelab.ma");
        cabinetToUpdate.setDescription("Clinique rénovée en 2025 - Nouveaux équipements");
        cabinetToUpdate.setModifierPar("test-update");
        cabinetRepo.update(cabinetToUpdate);
        println("Cabinet mis à jour");

        // 2. Admin
        Admin adminToUpdate = adminRepo.findById(adminId);
        adminToUpdate.setPrenom("SuperAdmin");
        adminToUpdate.setTelephone("0600000099");
        adminToUpdate.setMotDePasse("nouveauPass2025");
        adminToUpdate.setModifierPar("test-update");
        adminRepo.update(adminToUpdate);
        println("Admin mis à jour (mot de passe changé)");

        // 3. Médecin
        Medecin medToUpdate = medecinRepo.findById(medecinId);
        medToUpdate.setPrenom("Fatima-Zohra");
        medToUpdate.setSpecialite("Pédiatrie & Néonatalogie");
        medToUpdate.setSalaire(19500.0);
        medToUpdate.setPrime(4000.0);
        medToUpdate.setSoldeConge(35);
        medToUpdate.setModifierPar("test-update");
        medecinRepo.update(medToUpdate);
        println("Médecin mis à jour (nouvelle spécialité + augmentation)");

        // 4. Secrétaire
        Secretaire secToUpdate = secretaireRepo.findById(secretaireId);
        secToUpdate.setPrenom("Youssef-Mohammed");
        secToUpdate.setCommission(800.0);
        secToUpdate.setSalaire(9500.0);
        secToUpdate.setModifierPar("test-update");
        secretaireRepo.update(secToUpdate);
        println("Secrétaire mis à jour (augmentation + commission)");

        // 5. Patient
        Patient patientToUpdate = patientRepo.findById(patientId);
        patientToUpdate.setTelephone("0622334455");
        patientToUpdate.setEmail("amina.elamrani.updated@gmail.com");
        patientToUpdate.setAdresse("Appartement 12, Résidence Al Amal, Casablanca");
        patientToUpdate.setModifierPar("test-update");
        patientRepo.update(patientToUpdate);
        println("Patient mis à jour (nouveau téléphone et adresse)");

        // 6. Antécédent
        List<Antecedent> antecedents = patientRepo.getAntecedentsOfPatient(patientId);
        if (!antecedents.isEmpty()) {
            Antecedent anteToUpdate = antecedents.get(0);
            anteToUpdate.setDescription("Diabète de type 2 - Glycémie stabilisée sous metformine 1000mg/jour");
            anteToUpdate.setNiveauDeRisk(NiveauDeRisk.MOYEN);
            antecedentRepo.update(anteToUpdate);
            println("Antécédent mis à jour (diabète stabilisé)");
        }
        // 6. Dossier médical
        DossierMedical dossierToUpdate = dossierRepo.findById(dossierId);
        dossierToUpdate.setHistorique(dossierToUpdate.getHistorique() +
                "\n[MISE À JOUR " + LocalDateTime.now() + "] Patient suivi pour diabète - glycémie stabilisée");
        dossierToUpdate.setModifierPar("Dr. " + medToUpdate.getPrenom() + " " + medToUpdate.getNom());
        dossierRepo.update(dossierToUpdate);
        println("Dossier médical mis à jour (nouvelle note clinique)");

        // 7. Consultation
        Consultation consultToUpdate = consultationRepo.findById(consultationId);
        consultToUpdate.setStatus(StatusConsultation.TERMINEE);
        consultToUpdate.setObservationsMedecin("Croissance normale, vaccins à jour, patient en bonne santé générale");
        consultToUpdate.setNotes("Consultation de contrôle - Tout est parfait !");
        consultToUpdate.setModifierPar("test-update");
        consultationRepo.update(consultToUpdate);
        println("Consultation mise à jour (terminée avec observations)");

        // 8. Certificat
        Certificat certToUpdate = certificatRepo.findAll().get(0); // ou findByConsultationId
        certToUpdate.setDureeRepos(5);
        certToUpdate.setDateFin(LocalDate.now().plusDays(5));
        certToUpdate.setContenu("Prolongation de repos médical suite à fatigue persistante - Recommandation : repos + suivi");
        certToUpdate.setModifierPar("test-update");
        certificatRepo.update(certToUpdate);
        println("Certificat prolongé à 5 jours");

        // 9. Acte médical
        ActeMedical acteToUpdate = acteRepo.findById(acteId);
        acteToUpdate.setPrixDeBase(550.0f);
        acteToUpdate.setLibelle("Consultation pédiatrique complète (mise à jour tarif 2025)");
        acteToUpdate.setModifierPar("test-update");
        acteRepo.update(acteToUpdate);
        println("Acte médical : prix passé à 550 DH");

        // 10. Intervention
        InterventionMedecin interToUpdate = interventionRepo.findById(interventionId);
        interToUpdate.setPrixDePatient(550.0);
        interToUpdate.setModifierPar("test-update");
        interventionRepo.update(interToUpdate);
        println("Intervention : prix synchronisé avec l'acte");

        // 12. AgendaMensuel + AgendaMensuel_Jour (table de liaison)
        AgendaMensuel agendaToUpdate = agendaRepo.findById(agendaId);
        agendaToUpdate.setMois(Mois.JUILLET);
        agendaToUpdate.setJoursNonDisponible(List.of(Jour.VENDREDI, Jour.SAMEDI, Jour.DIMANCHE));
        agendaToUpdate.setModifierPar("test-update");
        agendaRepo.update(agendaToUpdate);
        println("AgendaMensuel mis à jour → Juillet + vendredi off");

        // 11. Rendez-vous
        RendezVous rdvToUpdate = rdvRepo.findById(rdvId);
        rdvToUpdate.setDate(rdvToUpdate.getDate().plusDays(2));
        rdvToUpdate.setTime(LocalTime.of(14, 0));
        rdvToUpdate.setMotif("Vaccination DTC + Consultation de contrôle (reporté)");
        rdvToUpdate.setStatus(Status.AUCUNE);
        rdvToUpdate.setModifierPar("test-update");
        rdvRepo.update(rdvToUpdate);
        println("Rendez-vous reporté à 14h dans 12 jours");

        // 12. Médicament
        Medicament medocToUpdate = medicamentRepo.findById(medicamentId);
        medocToUpdate.setPrixUnitaire(22.0);
        medocToUpdate.setDescription("Antalgique et antipyrétique - Dosage 1000mg (nouveau conditionnement)");
        medocToUpdate.setModifierPar("test-update");
        medicamentRepo.update(medocToUpdate);
        println("Prix Doliprane passé à 22 DH");

        // 13. Ordonnance
        Ordonnance ordToUpdate = ordonnanceRepo.findById(ordonnanceId);
        ordToUpdate.setDateOrdonnance(LocalDate.now().plusDays(1));
        ordToUpdate.setModifierPar("test-update");
        ordonnanceRepo.update(ordToUpdate);
        println("Date ordonnance mise à jour");

        // 16. Prescription
        Prescription prescToUpdate = prescriptionRepo.findAll().get(0);
        prescToUpdate.setQte(30);
        prescToUpdate.setFrequence("1 matin, midi et soir");
        prescToUpdate.setDuree(10);
        prescriptionRepo.update(prescToUpdate);
        println("Prescription modifiée → 30 comprimés sur 10 jours");

        // 17. Charges
        List<Charges> chargesList = chargesRepo.findAll();
        if (!chargesList.isEmpty()) {
            Charges chargeToUpdate = chargesList.get(0);
            chargeToUpdate.setMontant(28000.0);
            chargeToUpdate.setTitre("Loyer clinique (augmentation 2025)");
            chargeToUpdate.setDescription("Nouvelle grille tarifaire propriétaire");
            chargeToUpdate.setModifierPar("test-update");
            chargesRepo.update(chargeToUpdate);
            println("Charge (loyer) augmentée à 28 000 DH");
        }

        // 18. Revenus
        List<Revenus> revenusList = revenusRepo.findAll();
        if (!revenusList.isEmpty()) {
            Revenus revenuToUpdate = revenusList.get(0);
            revenuToUpdate.setMontant(235000.0);
            revenuToUpdate.setTitre("Consultations + actes juin 2025");
            revenuToUpdate.setModifierPar("test-update");
            revenusRepo.update(revenuToUpdate);
            println("Revenu mis à jour → 235 000 DH");
        }

        // 14. Facture
        Facture factureToUpdate = factureRepo.findById(factureId);
        factureToUpdate.setTotalPaye(4500f);
        factureToUpdate.setReste(500f);
        factureToUpdate.setStatut(StatutFacture.EN_ATTENTE);
        factureToUpdate.setModifierPar("test-update");
        factureRepo.update(factureToUpdate);
        println("Facture : 1500 DH payés en plus → reste = 500 DH");

        // 15. Situation financière
        SituationFinanciere sfToUpdate = situationRepo.findById(situationId);
        sfToUpdate.setTotalPaye(4500f);
        sfToUpdate.setCredit(500f);
        sfToUpdate.setModifierPar("test-update");
        situationRepo.update(sfToUpdate);
        println("Situation financière synchronisée avec la facture");

        // 16. Statistiques
        Statistiques statToUpdate = statsRepo.findById(statId);
        statToUpdate.setChiffre(215000.0);
        statToUpdate.setDateCalcul(LocalDate.now());
        statToUpdate.setNom("Chiffre d'affaires mis à jour (après mise à jour 2025)");
        statToUpdate.setModifierPar("test-update");
        statsRepo.update(statToUpdate);
        println("Statistiques mises à jour → CA = 215 000 DH");

        // 20. Role
        Role roleToUpdate = roleRepo.findAll().stream()
                .filter(r -> r.getLibelle() == RoleR.MEDECIN)
                .findFirst().orElse(null);
        if (roleToUpdate != null) {
            roleToUpdate.setPrivileges(List.of("PRESCRIRE", "CONSULTER_DOSSIER", "VALIDER_CERTIFICAT"));
            roleToUpdate.setModifierPar("test-update");
            roleRepo.update(roleToUpdate);
            println("Rôle MEDECIN : nouveau privilège ajouté");
        }

        // 21. Notification
        List<Notification> notifs = notifRepo.findAll();
        if (!notifs.isEmpty()) {
            Notification notifToUpdate = notifs.get(0);
            notifToUpdate.setMessage("MISE À JOUR TARIFS 2025 APPLIQUÉE - Merci de vérifier vos factures");
            notifToUpdate.setPriorite(PrioriteNotification.HAUTE);
            notifToUpdate.setModifierPar("test-update");
            notifRepo.update(notifToUpdate);
            println("Notification modifiée → alerte tarifaire");

        }
        // 22. Utilisateur (via Médecin - exemple de mise à jour d'un champ Utilisateur)
        Medecin medFinalUpdate = medecinRepo.findById(medecinId);
        medFinalUpdate.setEmail("fatima.benali.updated@whitelab.ma");
        medFinalUpdate.setLastLoginDate(LocalDateTime.now());
        medFinalUpdate.setModifierPar("test-update");
        medecinRepo.update(medFinalUpdate);
        println("Champ Utilisateur mis à jour (email + lastLoginDate)");
        println("\nTOUTES LES MISES À JOUR ONT ÉTÉ RÉUSSIES !");

    }

    private static void selectProcess() {
        section("DÉMARRAGE DES REQUÊTES DE SÉLECTION ET VÉRIFICATION DES DONNÉES DE TEST - WHITELAB 2025");

        // 1. Vérification du Cabinet Médical
        section("1. Vérification du Cabinet Médical");
        CabinetMedicale cabinetRetrieved = cabinetRepo.findById(cabinetId);
        if (cabinetRetrieved != null) {
            println("Cabinet retrouvé → Nom: " + cabinetRetrieved.getNom());
            println("Email: " + cabinetRetrieved.getEmail() + " | Téléphone: " + cabinetRetrieved.getTel1());
            println("Site web: " + cabinetRetrieved.getSiteWeb());
        } else {
            println("ERREUR : Cabinet non trouvé avec l'ID " + cabinetId);
        }

        // 2. Liste des Utilisateurs et leurs rôles
        section("2. Liste des Utilisateurs et leurs Rôles");
        println("Admin → " + adminRepo.findById(adminId));
        println("Médecin → " + medecinRepo.findById(medecinId));
        println("Secrétaire → " + secretaireRepo.findById(secretaireId));

        List<Role> rolesAdmin = utilisateurRepo.getRolesOfUtilisateur(adminId);
        List<Role> rolesMed = utilisateurRepo.getRolesOfUtilisateur(medecinId);
        List<Role> rolesSec = utilisateurRepo.getRolesOfUtilisateur(secretaireId);
        println("Rôles de l'Admin : " + rolesAdmin.stream().map(Role::getLibelle).collect(Collectors.toList()));
        println("Rôles du Médecin : " + rolesMed.stream().map(Role::getLibelle).collect(Collectors.toList()));
        println("Rôles de la Secrétaire : " + rolesSec.stream().map(Role::getLibelle).collect(Collectors.toList()));

        // 3. Patient et ses Antécédents
        section("3. Vérification du Patient et ses Antécédents");
        Patient patientRetrieved = patientRepo.findById(patientId);
        if (patientRetrieved != null) {
            println("Patient retrouvé → " + patientRetrieved.getNom() + " " + patientRetrieved.getPrenom());
            println("Téléphone: " + patientRetrieved.getTelephone() + " | Assurance: " + patientRetrieved.getAssurance());
        }

        List<Antecedent> antecedents = patientRepo.getAntecedentsOfPatient(patientId);
        println("Nombre d'antécédents : " + antecedents.size());
        antecedents.forEach(a -> println("Antécédent : " + a.getNom() + " (" + a.getCategorie() + ") - Risque: " + a.getNiveauDeRisk()));

        // 4. Dossier Médical et Consultation associée
        section("4. Vérification du Dossier Médical et Consultation");
        DossierMedical dossierRetrieved = dossierRepo.findById(dossierId);
        if (dossierRetrieved != null) {
            println("Dossier médical retrouvé pour le patient ID " + dossierRetrieved.getPat().getId());
            println("Médecin responsable : " + dossierRetrieved.getMedcine().getNom());
        }

        Consultation consultationRetrieved = consultationRepo.findById(consultationId);
        if (consultationRetrieved != null) {
            println("Consultation du " + consultationRetrieved.getDate() + " → Statut: " + consultationRetrieved.getStatus());
            println("Observations médecin : " + consultationRetrieved.getObservationsMedecin());
        }

        // 5. Actes, Interventions et Certificat
        section("5. Actes, Interventions et Certificat");
        ActeMedical acteRetrieved = acteRepo.findById(acteId);
        println("Acte médical : " + acteRetrieved.getLibelle() + " - Prix de base: " + acteRetrieved.getPrixDeBase() + " DH");

        InterventionMedecin interventionRetrieved = interventionRepo.findById(interventionId);
        if (interventionRetrieved != null) {
            println("Intervention liée à la consultation → Prix payé par patient: " + interventionRetrieved.getPrixDePatient() + " DH");
        }

        List<Certificat> certificats = certificatRepo.findByConsultationId(consultationId);
        certificats.forEach(c -> {
            println("Certificat médical : Repos de " + c.getDureeRepos() + " jours (du " + c.getDateDebut() + " au " + c.getDateFin() + ")");
        });

        // 6. Agenda et Rendez-vous
        section("6. Agenda et Rendez-vous");
        AgendaMensuel agendaRetrieved = agendaRepo.findById(agendaId);
        if (agendaRetrieved != null) {
            println("Agenda du médecin " + agendaRetrieved.getMedecin().getNom() + " pour le mois de " + agendaRetrieved.getMois());
            println("Jours non disponibles : " + agendaRetrieved.getJoursNonDisponible());
        }

        RendezVous rdvRetrieved = rdvRepo.findById(rdvId);
        if (rdvRetrieved != null) {
            println("Rendez-vous du " + rdvRetrieved.getDate() + " à " + rdvRetrieved.getTime() + " → Motif: " + rdvRetrieved.getMotif());
            println("Statut RDV : " + rdvRetrieved.getStatus());
        }

        // 7. Ordonnance et Prescriptions
        section("7. Ordonnance et Prescriptions");
        Ordonnance ordonnanceRetrieved = ordonnanceRepo.findById(ordonnanceId);
        if (ordonnanceRetrieved != null) {
            println("Ordonnance du " + ordonnanceRetrieved.getDateOrdonnance() + " liée à la consultation ID " + ordonnanceRetrieved.getConsultation().getId());
        }

        List<Prescription> prescriptions = prescriptionRepo.findByOrdonnanceId(ordonnanceId);
        println("Nombre de prescriptions : " + prescriptions.size());
        prescriptions.forEach(p -> {
            println("Prescription : " + p.getQte() + " " + p.getMedicament().getNom() +
                    " | Fréquence: " + p.getFrequence() + " | Durée: " + p.getDuree() + " jours");
        });

        // 8. Statistiques et Finances
        section("8. Statistiques et Données Financières");
        Statistiques statRetrieved = statsRepo.findById(statId);
        if (statRetrieved != null) {
            println("Statistique : " + statRetrieved.getNom() + " = " + statRetrieved.getChiffre() + " DH (" + statRetrieved.getCategorie() + ")");
        }

        List<Charges> charges = chargesRepo.findByCabinetId(cabinetId);
        println("Nombre de charges enregistrées : " + charges.size());
        charges.forEach(c -> println("Charge : " + c.getTitre() + " - " + c.getMontant() + " DH"));

        List<Revenus> revenusList = revenusRepo.findByCabinetId(cabinetId);
        println("Nombre de revenus enregistrés : " + revenusList.size());
        revenusList.forEach(r -> println("Revenu : " + r.getTitre() + " - " + r.getMontant() + " DH"));

        SituationFinanciere sfRetrieved = situationRepo.findById(situationId);
        if (sfRetrieved != null) {
            println("Situation financière → Total actes: " + sfRetrieved.getTotalDesActes() +
                    " | Payé: " + sfRetrieved.getTotalPaye() + " | Crédit: " + sfRetrieved.getCredit());
        }

        Facture factureRetrieved = factureRepo.findById(factureId);
        if (factureRetrieved != null) {
            println("Facture → Total: " + factureRetrieved.getTotalFact() +
                    " | Payé: " + factureRetrieved.getTotalPaye() + " | Reste: " + factureRetrieved.getReste() +
                    " | Statut: " + factureRetrieved.getStatut());
        }

        // 9. Notifications
        section("9. Notifications des Utilisateurs");
        List<Notification> notifsMedecin = utilisateurRepo.getNotificationsOfUtilisateur(medecinId);
        println("Notifications du médecin (" + notifsMedecin.size() + ") :");
        notifsMedecin.forEach(n -> println(n.getTitre() + " - " + n.getMessage() + " (Priorité: " + n.getPriorite() + ")"));

        List<Notification> notifsSecretaire = utilisateurRepo.getNotificationsOfUtilisateur(secretaireId);
        println("Notifications de la secrétaire (" + notifsSecretaire.size() + ") :");
        notifsSecretaire.forEach(n -> println(n.getTitre() + " - " + n.getMessage()));

    }
    private static void deleteProcess() {
        section("DÉMARRAGE DE LA SUPPRESSION COMPLÈTE DES DONNÉES DE TEST - WHITELABDB");

            // =====================================================
            // 1. Suppression des tables de liaison et entités feuilles
            // =====================================================

            // Suppression des liaisons Utilisateur ↔ Role & Notification
            println("Suppression des liaisons Utilisateur_Role et Utilisateur_Notification...");
            List<Utilisateur> allUsers = utilisateurRepo.findAll();
            for (Utilisateur user : allUsers) {
                utilisateurRepo.deleteAllRolesForUtilisateur(user.getId());
                utilisateurRepo.deleteAllNotificationsForUtilisateur(user.getId());
            }

            // Suppression des Prescriptions (feuille)
            println("Suppression des Prescriptions...");
            List<Prescription> prescriptions = prescriptionRepo.findAll();
            for (Prescription p : prescriptions) {
                prescriptionRepo.deleteById(p.getId());
            }

            // Suppression des Ordonnances
            println("Suppression des Ordonnances...");
            List<Ordonnance> ordonnances = ordonnanceRepo.findAll();
            for (Ordonnance o : ordonnances) {
                ordonnanceRepo.deleteById(o.getId());
            }

            // Suppression des InterventionsMedecin
            println("Suppression des InterventionsMedecin...");
            List<InterventionMedecin> interventions = interventionRepo.findAll();
            for (InterventionMedecin im : interventions) {
                interventionRepo.deleteById(im.getId());
            }

            // Suppression des Certificats
            println("Suppression des Certificats...");
            List<Certificat> certificats = certificatRepo.findAll();
            for (Certificat c : certificats) {
                certificatRepo.deleteById(c.getId());
            }

            // Suppression des Factures
            println("Suppression des Factures...");
            List<Facture> factures = factureRepo.findAll();
            for (Facture f : factures) {
                factureRepo.deleteById(f.getId());
            }

            // Suppression des Situations Financières
            println("Suppression des Situations Financières...");
            List<SituationFinanciere> situations =situationRepo.findAll();
            for (SituationFinanciere sf : situations) {
                situationRepo.deleteById(sf.getId());
            }

            // Suppression des RendezVous
            println("Suppression des RendezVous...");
            List<RendezVous> rendezVous = rdvRepo.findAll();
            for (RendezVous rv : rendezVous) {
                rdvRepo.deleteById(rv.getId());
            }

            // Suppression des Consultations
            println("Suppression des Consultations...");
            List<Consultation> consultations = consultationRepo.findAll();
            for (Consultation c : consultations) {
                consultationRepo.deleteById(c.getId());
            }

            // Suppression des liaisons Patient_Antecedent
            println("Suppression des liaisons Patient_Antecedent...");
            List<Patient> patients = patientRepo.findAll();
            for (Patient p : patients) {
                patientRepo.deleteById(p.getId()); // méthode à ajouter si besoin
            }

            // Suppression des Dossiers Médicaux
            println("Suppression des Dossiers Médicaux...");
            List<DossierMedical> dossiers = dossierRepo.findAll();
            for (DossierMedical dm : dossiers) {
                dossierRepo.deleteById(dm.getId());
            }

            // =====================================================
            // 2. Suppression des entités principales
            // =====================================================

            // Patients
            println("Suppression des Patients...");
            for (Patient p : patients) {
                patientRepo.deleteById(p.getId());
            }

            // Utilisateurs (Admin, Staff, Médecin, Secrétaire) → CASCADE gère les tables filles
            println("Suppression des Utilisateurs (Admin, Staff, Médecin, Secrétaire)...");
            for (Utilisateur u : allUsers) {
                utilisateurRepo.deleteById(u.getId());
            }

            // Médicaments
            println("Suppression des Médicaments...");
            List<Medicament> medicaments = medicamentRepo.findAll();
            for (Medicament m : medicaments) {
                medicamentRepo.deleteById(m.getId());
            }

            // Actes Médicaux
            println("Suppression des Actes Médicaux...");
            List<ActeMedical> actes = acteRepo.findAll();
            for (ActeMedical a : actes) {
                acteRepo.deleteById(a.getId());
            }

            // Agenda Mensuel (et jours non disponibles via CASCADE)
            println("Suppression des Agendas Mensuels...");
            List<AgendaMensuel> agendas = agendaRepo.findAll();
            for (AgendaMensuel a : agendas) {
                agendaRepo.deleteById(a.getId());
            }

            // Notifications & Rôles
            println("Suppression des Notifications...");
            List<Notification> notifications = notifRepo.findAll();
            for (Notification n : notifications) {
                notifRepo.deleteById(n.getId());
            }

            println("Suppression des Rôles...");
            List<Role> roles = roleRepo.findAll();
            for (Role r : roles) {
                roleRepo.deleteById(r.getId());
            }

            // Charges & Revenus
            println("Suppression des Charges et Revenus...");
            List<Charges> charges = chargesRepo.findAll();
            for (Charges ch : charges) {
                chargesRepo.deleteById(ch.getId());
            }
            List<Revenus> revenus = revenusRepo.findAll();
            for (Revenus r : revenus) {
                revenusRepo.deleteById(r.getId());
            }

            // Statistiques
            println("Suppression des Statistiques...");
            List<Statistiques> stats = statsRepo.findAll();
            for (Statistiques s : stats) {
                statsRepo.deleteById(s.getId());
            }

            // Cabinet Médical (dernier, car référencé par Staff, Charges, etc.)
            println("Suppression du Cabinet Médical...");
            List<CabinetMedicale> cabinets = cabinetRepo.findAll();
            for (CabinetMedicale cab : cabinets) {
                cabinetRepo.deleteById(cab.getId());
            }
            println("Suppression du Cabinet Antecedent...");
            List<Antecedent> antecedents = antecedentRepo.findAll();
            for (Antecedent a : antecedents) {
                antecedentRepo.deleteById(a.getId());
            }

    }
}
                


        


