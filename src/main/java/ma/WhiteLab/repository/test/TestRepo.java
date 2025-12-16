package ma.WhiteLab.repository.test;

import ma.WhiteLab.entities.cabinet.CabinetMedicale;
import ma.WhiteLab.entities.cabinet.Statistiques;
import ma.WhiteLab.entities.dossierMedical.*;
import ma.WhiteLab.entities.enums.*;
import ma.WhiteLab.entities.cabinet.Charges;
import ma.WhiteLab.entities.cabinet.Revenus;
import ma.WhiteLab.entities.dossierMedical.SituationFinanciere;
import ma.WhiteLab.entities.patient.*;
import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.entities.agenda.AgendaMensuel;
import ma.WhiteLab.entities.agenda.RendezVous;
import ma.WhiteLab.entities.user.Notification;
import ma.WhiteLab.entities.user.Role;
import ma.WhiteLab.entities.user.Utilisateur;
import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.CabinetMedicaleRepositoryImpl;
import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.ChargesRepositoryImpl;
import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.RevenusRepositoryImpl;
import ma.WhiteLab.repository.modules.cabinet.impl.mySQL.StatistiquesRepositoryImpl;
import ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL.*;
import ma.WhiteLab.repository.modules.patient.impl.mySQL.*;
import ma.WhiteLab.repository.modules.user.impl.mySQL.MedecinRepositoryImpl;
import ma.WhiteLab.repository.modules.agenda.api.RendezVousRepository;
import ma.WhiteLab.repository.modules.agenda.impl.mySQL.AgendaMensuelRepositoryImpl;
import ma.WhiteLab.repository.modules.agenda.impl.mySQL.RendezVousRepositoryImpl;
import ma.WhiteLab.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.OrdonnanceRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.WhiteLab.repository.modules.notifications.api.NotificationRepository;
import ma.WhiteLab.repository.modules.user.api.RoleRepository;
import ma.WhiteLab.repository.modules.notifications.impl.mySQL.NotificationRepositoryImpl;
import ma.WhiteLab.repository.modules.user.impl.mySQL.RoleRepositoryImpl;
import ma.WhiteLab.repository.modules.user.impl.mySQL.UtilisateurRepositoryImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestRepo {

    public static void main(String[] args) {

        // ========================== RÉPOSITORIES ==========================
        PatientRepositoryImpl patientRepo = new PatientRepositoryImpl();
        DossierMedicalRepositoryImpl dossierMedicalRepo = new DossierMedicalRepositoryImpl();
        AntecedentRepositoryImpl antecedentRepo = new AntecedentRepositoryImpl();
        MedecinRepositoryImpl medecinRepo = new MedecinRepositoryImpl();
        ConsultationRepositoryImpl consultationRepo = new ConsultationRepositoryImpl();
        ActeMedicalRepositoryImpl acteRepo = new ActeMedicalRepositoryImpl();
        CertificatRepositoryImpl certificatRepo = new CertificatRepositoryImpl();
        InterventionMedecinRepositoryImpl interventionRepo = new InterventionMedecinRepositoryImpl();
        CabinetMedicaleRepositoryImpl cabinetRepo = new CabinetMedicaleRepositoryImpl();
        StatistiquesRepositoryImpl statRepo = new StatistiquesRepositoryImpl();
        ChargesRepositoryImpl chargesRepo = new ChargesRepositoryImpl();
        RevenusRepositoryImpl revenusRepo = new RevenusRepositoryImpl();
        SituationFinanciereRepositoryImpl suiviFinancierRepo = new SituationFinanciereRepositoryImpl();
        FactureRepositoryImpl factureRepo = new FactureRepositoryImpl();
        AgendaMensuelRepositoryImpl agendaMensuelRepo = new AgendaMensuelRepositoryImpl();
        RendezVousRepository rendezVousRepo = new RendezVousRepositoryImpl();
        MedicamentRepository medicamentRepo = new MedicamentRepositoryImpl();
        OrdonnanceRepository ordonnanceRepo = new OrdonnanceRepositoryImpl();
        PrescriptionRepository prescriptionRepo = new PrescriptionRepositoryImpl();
        NotificationRepository repo = new NotificationRepositoryImpl();
        RoleRepository RoleRepo = new RoleRepositoryImpl();
        UtilisateurRepositoryImpl<Utilisateur> UserRepo = new UtilisateurRepositoryImpl<>(Utilisateur.class) {};





        // ============================================================
        // 1. CABINET MÉDICAL - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST CABINET MEDICALE REPOSITORY =====");

        CabinetMedicale cabinet = CabinetMedicale.builder()
                .nom("Cabinet Ibn Sina")
                .logo("logo.png")
                .description("Cabinet de référence")
                .tel1("0537000000")
                .tel2("0537000001")
                .email("contact@ibnsina.ma")
                .siteWeb("https://www.ibnsina.ma")
                .categorie("Général")
                .build();

        cabinetRepo.create(cabinet);
        System.out.println("Cabinet créé ID = " + cabinet.getId());

        // Test findAll
        List<CabinetMedicale> cabinetsList = cabinetRepo.findAll();
        System.out.println("Nombre total de cabinets : " + cabinetsList.size());

        // Test findById
        CabinetMedicale cabinetFound = cabinetRepo.findById(cabinet.getId());
        System.out.println("Cabinet trouvé : " + cabinetFound.getNom());

        // Test update
        cabinet.setDescription("Cabinet mis à jour pour test");
        cabinet.setModifierPar("admin");
        cabinetRepo.update(cabinet);
        System.out.println("Cabinet mis à jour");

        CabinetMedicale cabinetUpdated = cabinetRepo.findById(cabinet.getId());
        System.out.println("Description mise à jour : " + cabinetUpdated.getDescription());

        // Test findByNom
        Optional<CabinetMedicale> foundCabinetByNom = cabinetRepo.findByNom("Cabinet Ibn Sina");
        System.out.println("Cabinet trouvé par nom : " + (foundCabinetByNom.isPresent() ? "OUI" : "NON"));

        // Test findByCategorie
        List<CabinetMedicale> cabinetsByCategorie = cabinetRepo.findByCategorie("Général");
        System.out.println("Cabinets par catégorie : " + cabinetsByCategorie.size());

        // Test findByEmail
        Optional<CabinetMedicale> cabinetByEmail = cabinetRepo.findByEmail("contact@ibnsina.ma");
        System.out.println("Cabinet trouvé par email : " + (cabinetByEmail.isPresent() ? "OUI" : "NON"));

        // Test findAllOrderByNom
        List<CabinetMedicale> cabinetsSorted = cabinetRepo.findAllOrderByNom();
        System.out.println("Cabinets triés par nom : " + cabinetsSorted.size());

        // ============================================================
        // 2. STATISTIQUES - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST STATISTIQUES REPOSITORY =====");

        Statistiques stat = Statistiques.builder()
                .nom("Consultations Mensuelles")
                .categorie(CategorieStatistique.FINANCIER)
                .chiffre(125.0)
                .dateCalcul(LocalDate.now())
                .cabinetMedicale(cabinet)
                .creePar("admin")
                .build();

        statRepo.create(stat);
        System.out.println("Statistique créée ID = " + stat.getId());

        // Test findAll
        List<Statistiques> statsList = statRepo.findAll();
        System.out.println("Nombre total de statistiques : " + statsList.size());

        // Test findById
        Statistiques statFound = statRepo.findById(stat.getId());
        System.out.println("Statistique trouvée : " + statFound.getNom() + " => " + statFound.getChiffre());

        // Test update
        stat.setChiffre(150.0);
        stat.setModifierPar("admin");
        statRepo.update(stat);
        System.out.println("Statistique mise à jour.");

        Statistiques statUpdated = statRepo.findById(stat.getId());
        System.out.println("Chiffre mis à jour : " + statUpdated.getChiffre());

        // Test findByCabinetId
        List<Statistiques> statsCabinet = statRepo.findByCabinetId(cabinet.getId());
        System.out.println("Stats pour le cabinet : " + statsCabinet.size());

        // ============================================================
        // 3. ACTE MÉDICAL - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST ACTE MEDICAL REPOSITORY =====");

        ActeMedical acte = ActeMedical.builder()
                .libelle("Extraction Dentaire")
                .categorie("Dentaire")
                .prixDeBase(250.0f)
                .creePar("admin")
                .build();

        acteRepo.create(acte);
        System.out.println("Acte créé ID = " + acte.getId());

        // Test findAll
        List<ActeMedical> actesList = acteRepo.findAll();
        System.out.println("Nombre total d'actes médicaux : " + actesList.size());

        // Test findById
        ActeMedical acteFound = acteRepo.findById(acte.getId());
        System.out.println("Acte trouvé : " + acteFound.getLibelle() + " - Prix : " + acteFound.getPrixDeBase());

        // Test update
        acte.setPrixDeBase(300.0f);
        acte.setModifierPar("admin");
        acteRepo.update(acte);
        System.out.println("Acte mis à jour.");

        ActeMedical acteUpdated = acteRepo.findById(acte.getId());
        System.out.println("Prix mis à jour : " + acteUpdated.getPrixDeBase());

        // Test findByLibelle
        Optional<ActeMedical> optActe = acteRepo.findByLibelle("Extraction Dentaire");
        System.out.println("Acte trouvé par libellé : " + (optActe.isPresent() ? optActe.get().getLibelle() : "NON TROUVÉ"));

        // ============================================================
        // 5. PATIENT - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST PATIENT REPOSITORY =====");

        Patient patient = Patient.builder()
                .nom("Aymane")
                .prenom("John")
                .adresse("123 rue Exemple, Casablanca")
                .telephone("0600000000")
                .email("john.doe@example.com")
                .dateNaissance(LocalDate.of(1990, 5, 20))
                .sexe(Sexe.Homme)
                .assurance(Assurance.CNSS)
                .creePar("admin")
                .build();

        patientRepo.create(patient);
        System.out.println("Patient créé ID = " + patient.getId());

        // Test findAll
        List<Patient> patientsList = patientRepo.findAll();
        System.out.println("Nombre total de patients : " + patientsList.size());

        // Test findById
        Patient patientFound = patientRepo.findById(patient.getId());
        System.out.println("Patient trouvé : " + patientFound.getNom() + " " + patientFound.getPrenom());

        // Test update
        patient.setAdresse("456 rue Modifiée, Rabat");
        patient.setModifierPar("admin");
        patientRepo.update(patient);
        System.out.println("Patient mis à jour.");

        Patient patientUpdated = patientRepo.findById(patient.getId());
        System.out.println("Adresse mise à jour : " + patientUpdated.getAdresse());

        // Test findByEmail
        Optional<Patient> byEmail = patientRepo.findByEmail("john.doe@example.com");
        System.out.println("Patient trouvé par email : " + (byEmail.isPresent() ? "OUI" : "NON"));

        // Test findByTelephone
        Optional<Patient> byTelephone = patientRepo.findByTelephone("0600000000");
        System.out.println("Patient trouvé par téléphone : " + (byTelephone.isPresent() ? "OUI" : "NON"));

        // Test searchByNomPrenom
        List<Patient> searchResults = patientRepo.searchByNomPrenom("Aym");
        System.out.println("Résultats recherche par nom/prénom : " + searchResults.size());

        // Test existsById
        boolean patientExists = patientRepo.existsById(patient.getId());
        System.out.println("Patient existe ? " + patientExists);

        // Test count
        long patientCount = patientRepo.count();
        System.out.println("Nombre total de patients en DB : " + patientCount);

        // Test findPage
        List<Patient> pagedPatients = patientRepo.findPage(10, 0);
        System.out.println("Patients page 1 (limit 10) : " + pagedPatients.size());

        // ============================================================
        // 5.2 ANTÉCÉDENTS - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST ANTECEDENT REPOSITORY =====");

        Antecedent antecedent1 = Antecedent.builder()
                .nom("Asthme")
                .description("Asthme léger depuis l'enfance")
                .categorie(CategorieAntecedent.ALLERGIE)
                .niveauDeRisk(NiveauDeRisk.MOYEN)
                .build();

        Antecedent antecedent2 = Antecedent.builder()
                .nom("Allergie aux arachides")
                .description("Allergie sévère")
                .categorie(CategorieAntecedent.HABITUDE_DE_VIE)
                .niveauDeRisk(NiveauDeRisk.ELEVE)
                .build();

        antecedentRepo.create(antecedent1);
        antecedentRepo.create(antecedent2);
        System.out.println("Antécédent 1 créé ID = " + antecedent1.getId());
        System.out.println("Antécédent 2 créé ID = " + antecedent2.getId());

        // Test findAll
        List<Antecedent> antecedentsList = antecedentRepo.findAll();
        System.out.println("Nombre total d'antécédents : " + antecedentsList.size());

        // Test findById
        Antecedent antecedentFound = antecedentRepo.findById(antecedent1.getId());
        System.out.println("Antécédent trouvé : " + antecedentFound.getNom());

        // Test update
        antecedent1.setDescription("Asthme modéré depuis l'adolescence");
        antecedent1.setNiveauDeRisk(NiveauDeRisk.ELEVE);
        antecedentRepo.update(antecedent1);
        System.out.println("Antécédent mis à jour.");

        // Add antecedents to patient
        patientRepo.addAntecedentToPatient(patient.getId(), antecedent1.getId());
        patientRepo.addAntecedentToPatient(patient.getId(), antecedent2.getId());
        System.out.println("Antécédents ajoutés au patient");

        // Get antecedents of patient
        List<Antecedent> antecedentsPatient = patientRepo.getAntecedentsOfPatient(patient.getId());
        System.out.println("Nb antécédents du patient : " + antecedentsPatient.size());

        // Remove antecedent from patient
        patientRepo.removeAntecedentFromPatient(patient.getId(), antecedent2.getId());
        System.out.println("Antécédent supprimé du patient");

        List<Antecedent> antecedentsPatientAfter = patientRepo.getAntecedentsOfPatient(patient.getId());
        System.out.println("Antécédents du patient après suppression : " + antecedentsPatientAfter.size());

        // ============================================================
        // 6. MÉDECIN - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST MEDECIN REPOSITORY =====");

        Medecin medecin = Medecin.builder()
                .nom("Smith")
                .prenom("Alice")
                .specialite("Cardiologie")
                .cabinetMedicale(cabinet)
                .build();

        medecinRepo.create(medecin);
        System.out.println("Médecin créé ID = " + medecin.getId());

        // Test findAll
        List<Medecin> medecinsList = medecinRepo.findAll();
        System.out.println("Nombre total de médecins : " + medecinsList.size());

        // Test findById
        Medecin medecinFound = medecinRepo.findById(medecin.getId());
        System.out.println("Médecin trouvé : " + medecinFound.getNom() + " " + medecinFound.getPrenom() + " (" + medecinFound.getSpecialite() + ")");

        // Test update
        medecin.setSpecialite("Chirurgie Dentaire");
        medecin.setModifierPar("admin");
        medecinRepo.update(medecin);
        System.out.println("Médecin mis à jour.");

        Medecin medecinUpdated = medecinRepo.findById(medecin.getId());
        System.out.println("Spécialité mise à jour : " + medecinUpdated.getSpecialite());

        // ============================================================
        // 6.1 DOSSIER MÉDICAL - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST DOSSIER MEDICAL REPOSITORY =====");

        DossierMedical dossier = DossierMedical.builder()
                .historique("Historique initial")
                .pat(patient)
                .medcine(medecin)
                .creePar("admin")
                .build();

        dossierMedicalRepo.create(dossier);
        patient.setDossierMed(dossier);
        System.out.println("Dossier médical créé ID = " + dossier.getId());

        // Test findAll
        List<DossierMedical> dossiersList = dossierMedicalRepo.findAll();
        System.out.println("Nombre total de dossiers médicaux : " + dossiersList.size());

        // Test findById
        DossierMedical dossierFound = dossierMedicalRepo.findById(dossier.getId());
        System.out.println("Dossier trouvé : " + dossierFound.getHistorique());

        // Test update
        dossier.setHistorique("Historique mis à jour avec nouvelles informations");
        dossier.setModifierPar("admin");
        dossierMedicalRepo.update(dossier);
        System.out.println("Dossier médical mis à jour.");

        DossierMedical dossierUpdated = dossierMedicalRepo.findById(dossier.getId());
        System.out.println("Historique mis à jour : " + dossierUpdated.getHistorique().substring(0, Math.min(40, dossierUpdated.getHistorique().length())) + "...");

        // Test findByPatientId
        Optional<DossierMedical> dossierByPatient = dossierMedicalRepo.findByPatientId(patient.getId());
        System.out.println("Dossier par patient ID : " + (dossierByPatient.isPresent() ? "TROUVÉ" : "NON TROUVÉ"));

        // Test findByMedecinId
        List<DossierMedical> dossiersByMedecin = dossierMedicalRepo.findByMedecinId(medecin.getId());
        System.out.println("Dossiers médicaux du médecin : " + dossiersByMedecin.size());

        // Test count
        long dossierCount = dossierMedicalRepo.count();
        System.out.println("Nombre total de dossiers en DB : " + dossierCount);

        // ============================================================
        // 7. CONSULTATION - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST CONSULTATION REPOSITORY =====");

        Consultation consultation = Consultation.builder()
                .dossierMedical(dossier)
                .date(LocalDateTime.now())
                .status(StatusConsultation.EN_COURS)
                .notes("Consultation initiale")
                .observationsMedecin("RAS")
                .creePar("admin")
                .build();

        consultationRepo.create(consultation);
        System.out.println("Consultation créée ID = " + consultation.getId());



        // Test update
        consultation.setStatus(StatusConsultation.TERMINEE);
        consultation.setNotes("Consultation terminée");
        consultation.setModifierPar("admin");
        consultationRepo.update(consultation);
        System.out.println("Consultation mise à jour.");

        Consultation consultationUpdated = consultationRepo.findById(consultation.getId());
        System.out.println("Consultation mise à jour : Status = " + consultationUpdated.getStatus());



        // ============================================================
        // 9. INTERVENTION MÉDECIN - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST INTERVENTION MEDECIN REPOSITORY =====");

        InterventionMedecin intervention = InterventionMedecin.builder()
                .prixDePatient(200.0)
                .numDent(12)
                .consultation(consultation)
                .acteMedical(acte)
                .creePar("admin")
                .build();

        interventionRepo.create(intervention);
        System.out.println("Intervention créée ID = " + intervention.getId());

        // Test findAll
        List<InterventionMedecin> interventionsList = interventionRepo.findAll();
        System.out.println("Nombre total d'interventions : " + interventionsList.size());

        // Test findById
        InterventionMedecin interventionFound = interventionRepo.findById(intervention.getId());
        System.out.println("Intervention trouvée : Dent " + interventionFound.getNumDent() + " - Prix : " + interventionFound.getPrixDePatient());

        // Test update
        intervention.setPrixDePatient(250.0);
        intervention.setModifierPar("admin");
        interventionRepo.update(intervention);
        System.out.println("Intervention mise à jour.");

        InterventionMedecin interventionUpdated = interventionRepo.findById(intervention.getId());
        System.out.println("Prix mis à jour : " + interventionUpdated.getPrixDePatient());

        // Test findByNumDent
        List<InterventionMedecin> interventionsDent = interventionRepo.findByNumDent(12);
        System.out.println("Interventions dent 12 : " + interventionsDent.size());

        // Test findByConsultationId
        List<InterventionMedecin> interventionsByConsultation = interventionRepo.findByConsultationId(consultation.getId());
        System.out.println("Interventions pour consultation : " + interventionsByConsultation.size());

        // Test findByActeMedicalId
        List<InterventionMedecin> interventionsByActe = interventionRepo.findByActeMedicalId(acte.getId());
        System.out.println("Interventions pour acte médical : " + interventionsByActe.size());

        // ============================================================
        // 10. FINANCES - CHARGES REPOSITORY
        // ============================================================
        System.out.println("\n===== TEST CHARGES REPOSITORY =====");

        Charges charge = Charges.builder()
                .titre("Electricité")
                .description("Facture décembre")
                .montant(1200.50)
                .date(LocalDateTime.now())
                .cabinetMedicale(cabinet)
                .creePar("admin")
                .build();
        chargesRepo.create(charge);
        System.out.println("Charge créée ID = " + charge.getId());

        // Test findAll
        List<Charges> chargesList = chargesRepo.findAll();
        System.out.println("Nombre total de charges : " + chargesList.size());

        // Test findById
        Charges chargeFound = chargesRepo.findById(charge.getId());
        System.out.println("Charge trouvée : " + chargeFound.getTitre() + " - " + chargeFound.getMontant());

        // Test update
        charge.setMontant(1500.00);
        charge.setDescription("Facture décembre - Corrigée");
        charge.setModifierPar("admin");
        chargesRepo.update(charge);
        System.out.println("Charge mise à jour.");

        Charges chargeUpdated = chargesRepo.findById(charge.getId());
        System.out.println("Montant mis à jour : " + chargeUpdated.getMontant());

        // Test findByCabinetId
        List<Charges> chargesByCabinet = chargesRepo.findByCabinetId(cabinet.getId());
        System.out.println("Charges du cabinet : " + chargesByCabinet.size());

        // Test findByDateBetween
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now().plusDays(10);
        List<Charges> chargesByDate = chargesRepo.findByDateBetween(startDate, endDate);
        System.out.println("Charges entre dates : " + chargesByDate.size());

        // ============================================================
        // 10.2 FINANCES - REVENUS REPOSITORY
        // ============================================================
        System.out.println("\n===== TEST REVENUS REPOSITORY =====");

        Revenus revenu = Revenus.builder()
                .titre("Consultations décembre")
                .montant(5000.0)
                .date(LocalDateTime.now())
                .cabinetMedicale(cabinet)
                .creePar("admin")
                .build();
        revenusRepo.create(revenu);
        System.out.println("Revenu créé ID = " + revenu.getId());

        // Test findAll
        List<Revenus> revenusList = revenusRepo.findAll();
        System.out.println("Nombre total de revenus : " + revenusList.size());

        // Test findById
        Revenus revenuFound = revenusRepo.findById(revenu.getId());
        System.out.println("Revenu trouvé : " + revenuFound.getTitre() + " - " + revenuFound.getMontant());

        // Test update
        revenu.setMontant(5500.0);
        revenu.setModifierPar("admin");
        revenusRepo.update(revenu);
        System.out.println("Revenu mis à jour.");

        Revenus revenuUpdated = revenusRepo.findById(revenu.getId());
        System.out.println("Montant revenu mis à jour : " + revenuUpdated.getMontant());

        // Test findByCabinetId
        List<Revenus> revenusByCabinet = revenusRepo.findByCabinetId(cabinet.getId());
        System.out.println("Revenus du cabinet : " + revenusByCabinet.size());

        // Test findByDateBetween
        List<Revenus> revenusByDate = revenusRepo.findByDateBetween(startDate, endDate);
        System.out.println("Revenus entre dates : " + revenusByDate.size());

        // Test findByTitre
        List<Revenus> revenusByTitre = revenusRepo.findByTitre("Consultations");
        System.out.println("Revenus avec titre contenant 'Consultations' : " + revenusByTitre.size());

        // ============================================================
        // 10.3 FINANCES - SITUATION FINANCIÈRE REPOSITORY
        // ============================================================
                System.out.println("\n===== TEST SITUATION FINANCIÈRE REPOSITORY =====");

        // Création d'une SituationFinanciere
                SituationFinanciere situation = SituationFinanciere.builder()
                        .totalDesActes(5000f)
                        .totalPaye(3500f)
                        .credit(1500f)                    // ← 5000 - 3500 = 1500 → credit calculé ou saisi manuellement
                        .enPromo(PromoStatus.AUCUNE)
                        .status(Status.ACTIVE)
                        .dossierMedical(dossier)          // dossier déjà créé et persistant
                        .creePar("admin")
                        .build();

        suiviFinancierRepo.create(situation);
                System.out.println("Situation financière créée avec ID = " + situation.getId());

        // Test findAll
                List<SituationFinanciere> toutesLesSituations = suiviFinancierRepo.findAll();
                System.out.println("Nombre total de situations financières : " + toutesLesSituations.size());

        // Test findById
                SituationFinanciere situationTrouvee = suiviFinancierRepo.findById(situation.getId());
                System.out.println("Situation trouvée → Total des actes = " + situationTrouvee.getTotalDesActes()
                        + " | Total payé = " + situationTrouvee.getTotalPaye()
                        + " | Crédit = " + situationTrouvee.getCredit());

        // Test update
                situation.setTotalPaye(4000f);           // Nouveau paiement → crédit doit baisser
                situation.setCredit(1000f);              // 5000 - 4000 = 1000
                situation.setModifierPar("admin");

        suiviFinancierRepo.update(situation);
                System.out.println("Situation financière mise à jour.");

        // Vérification après mise à jour
                SituationFinanciere situationMiseAJour = suiviFinancierRepo.findById(situation.getId());
                System.out.println("Après mise à jour → Total payé = " + situationMiseAJour.getTotalPaye()
                        + " | Crédit restant = " + situationMiseAJour.getCredit());

        // Test findByDossierId
                List<SituationFinanciere> situationsDuDossier = suiviFinancierRepo.findByDossierId(dossier.getId());
                System.out.println("Nombre de situations financières pour ce dossier : " + situationsDuDossier.size());

        // Test findByStatus
                List<SituationFinanciere> situationsActives = suiviFinancierRepo.findByStatus(Status.ACTIVE.name());
                System.out.println("Situations financières avec statut ACTIVE : " + situationsActives.size());

        // Test findByPromo
                List<SituationFinanciere> situationsSansPromo = suiviFinancierRepo.findByPromo(PromoStatus.AUCUNE.name());
                System.out.println("Situations financières sans promotion (AUCUNE) : " + situationsSansPromo.size());

        // ============================================================
        // 10.4 FINANCES - FACTURE REPOSITORY
        // ============================================================
        System.out.println("\n===== TEST FACTURE REPOSITORY =====");

        Facture facture = Facture.builder()
                .totalFact(2000.0f)
                .totalPaye(500.0f)
                .reste(1500.0f)
                .date(LocalDate.now())
                .statut(StatutFacture.EN_ATTENTE)
                .consultation(consultation)
                .sf(situation)
                .creePar("admin")
                .build();
        factureRepo.create(facture);
        System.out.println("Facture créée ID = " + facture.getId());

        // Test findAll
        List<Facture> facturesList = factureRepo.findAll();
        System.out.println("Nombre total de factures : " + facturesList.size());

        // Test findById
        Facture factureFound = factureRepo.findById(facture.getId());
        System.out.println("Facture trouvée : Total = " + factureFound.getTotalFact() + " - Payé = " + factureFound.getTotalPaye());

        // Test update
        facture.setTotalPaye(1000.0f);
        facture.setReste(1000.0f);
        facture.setStatut(StatutFacture.PAYEE);
        facture.setModifierPar("admin");
        factureRepo.update(facture);
        System.out.println("Facture mise à jour.");

        Facture factureUpdated = factureRepo.findById(facture.getId());
        System.out.println("Facture mise à jour : Total Payé = " + factureUpdated.getTotalPaye() + " - Statut = " + factureUpdated.getStatut());

        // Test findByConsultationId
        List<Facture> facturesByConsultation = factureRepo.findByConsultationId(consultation.getId());
        System.out.println("Factures pour consultation : " + facturesByConsultation.size());

        // Test findByStatut
        List<Facture> facturesByStatut = factureRepo.findByStatut(StatutFacture.EN_ATTENTE.name());
        System.out.println("Factures EN_ATTENTE : " + facturesByStatut.size());

        // Test findByDateBetween
        LocalDate dateStart = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().plusDays(30);
        List<Facture> facturesByDateRange = factureRepo.findByDateBetween(dateStart, dateEnd);
        System.out.println("Factures entre dates : " + facturesByDateRange.size());

        // ============================================================
        // 11. CERTIFICAT - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST CERTIFICAT REPOSITORY =====");

        Certificat certificat = Certificat.builder()
                .dateDebut(LocalDate.of(2025, 12, 1))
                .dateFin(LocalDate.of(2025, 12, 5))
                .dureeRepos(5)
                .contenu("Repos médical prescrit")
                .creePar("admin")
                .consultation(consultation)
                .dossierMedical(dossier)
                .build();

        certificatRepo.create(certificat);
        System.out.println("Certificat créé ID = " + certificat.getId());

        // Test findAll
        List<Certificat> certificatsList = certificatRepo.findAll();
        System.out.println("Nombre total de certificats : " + certificatsList.size());

        // Test findById
        Certificat certificatFound = certificatRepo.findById(certificat.getId());
        System.out.println("Certificat trouvé : Durée = " + certificatFound.getDureeRepos() + " jours");

        // Test update
        certificat.setDureeRepos(7);
        certificat.setContenu("Repos médical prolongé");
        certificat.setModifierPar("admin");
        certificatRepo.update(certificat);
        System.out.println("Certificat mis à jour.");

        Certificat certificatUpdated = certificatRepo.findById(certificat.getId());
        System.out.println("Durée mise à jour : " + certificatUpdated.getDureeRepos() + " jours - Contenu : " + certificatUpdated.getContenu());

        // Test findByConsultationId
        List<Certificat> certificatsByConsultation = certificatRepo.findByConsultationId(consultation.getId());
        System.out.println("Certificats pour consultation : " + certificatsByConsultation.size());

        // Test findByDossierId
        List<Certificat> certificatsByDossier = certificatRepo.findByDossierMedicalId(dossier.getId());
        System.out.println("Certificats pour dossier : " + certificatsByDossier.size());

        // ============================================================
        // 12. AGENDA MENSUEL - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST AGENDA MENSUEL REPOSITORY =====");

        // Création d'un agenda mensuel
        AgendaMensuel agenda = AgendaMensuel.builder()
                .mois(Mois.DECEMBRE) // mois correspondant
                .joursNonDisponible(List.of(
                        Jour.LUNDI,
                        Jour.MARDI
                )) // enum Jour, pas des String
                .medecin(medecin) // assume que medecin est déjà créé et persistant
                .creePar("admin")
                .modifierPar("admin")
                .build();

        // Création dans la DB
        agendaMensuelRepo.create(agenda);
        System.out.println("AgendaMensuel créé ID = " + agenda.getId());

        // Test findAll
        List<AgendaMensuel> agendasList = agendaMensuelRepo.findAll();
        System.out.println("Nombre total d'agendas : " + agendasList.size());

        // Test findById
        AgendaMensuel foundAgenda = agendaMensuelRepo.findById(agenda.getId());
        if (foundAgenda != null) {
            System.out.println("AgendaMensuel trouvé : mois = " + foundAgenda.getMois() +
                    ", jours non dispo = " + foundAgenda.getJoursNonDisponible());
        }

        // Test update
        agenda.setJoursNonDisponible(List.of(
                Jour.MERCREDI,
                Jour.VENDREDI
        ));
        agendaMensuelRepo.update(agenda);

        // Recharger pour vérifier
        foundAgenda = agendaMensuelRepo.findById(agenda.getId());
        System.out.println("AgendaMensuel mis à jour : jours non dispo = " + foundAgenda.getJoursNonDisponible());

        // Test findByMedecinId
        List<AgendaMensuel> byMedecin = agendaMensuelRepo.findByMedecinId(medecin.getId());
        System.out.println("Nombre d'agendas pour le médecin : " + byMedecin.size());

        // Test findByMedecinIdAndMois
        List<AgendaMensuel> byMedecinMois = agendaMensuelRepo.findByMedecinIdAndMois(
                medecin.getId(),
                Mois.DECEMBRE.name()
        );
        System.out.println("Nombre d'agendas pour le médecin en décembre : " + byMedecinMois.size());

        // ============================================================
        // 13. RENDEZ-VOUS - COMPREHENSIVE TESTS
        // ============================================================
        System.out.println("\n===== TEST RENDEZ-VOUS REPOSITORY =====");

        RendezVous rv = RendezVous.builder()
                .date(LocalDateTime.of(2025, 12, 25, 0, 0))    // date du rendez-vous
                .time(LocalTime.of(14, 30))                    // heure du RDV
                .motif("Consultation générale")
                .status(Status.AUCUNE)
                .noteMedecin("Première consultation du patient")
                .dossierMed(dossier)
                .consultation(consultation)  // tu peux tester avec ou sans
                .build();

        rendezVousRepo.create(rv);
        System.out.println("RDV créé ID = " + rv.getId());

        // Test findAll
        List<RendezVous> rdvList = rendezVousRepo.findAll();
        System.out.println("Nombre total de RDV : " + rdvList.size());

        // Test findById
        RendezVous found = rendezVousRepo.findById(rv.getId());
        if (found != null) {
            System.out.println("RDV trouvé : " +
                    found.getMotif() + " | " + found.getStatus() +
                    " | " + found.getDate() + " " + found.getTime());
        }

        // ============================================================
        // 3. UPDATE
        // ============================================================
        rv.setMotif("Consultation modifiée");
        rv.setStatus(Status.ACTIVE);
        rv.setNoteMedecin("Note modifiée par le médecin");

        rendezVousRepo.update(rv);
        System.out.println("RDV mis à jour.");

        // Vérification
        RendezVous updated = rendezVousRepo.findById(rv.getId());
        System.out.println("Nouveau motif = " + updated.getMotif());
        System.out.println("Nouveau status = " + updated.getStatus());
        System.out.println("Nouvelle note = " + updated.getNoteMedecin());

        // ============================================================
        // 4. FIND BY DOSSIER MEDICAL
        // ============================================================
        List<RendezVous> byDossier = rendezVousRepo.findByDossierMedId(dossier.getId());
        System.out.println("RDV trouvés pour dossier " + dossier.getId() + " = " + byDossier.size());

        // ============================================================
        // 5. FIND BY CONSULTATION (si tu veux tester)
        // ============================================================
        // List<RendezVous> byConsult = rendezVousRepo.findByConsultationId(consultation.getId());
        // System.out.println("RDV trouvés pour cette consultation = " + byConsult.size());

        // ============================================================
        // 6. FIND BY STATUS
        // ============================================================
        List<RendezVous> byStatus = rendezVousRepo.findByStatus(Status.ACTIVE.name());
        System.out.println("RDV CONFIRMÉS = " + byStatus.size());

        // ============================================================
        // 7. DELETE
        // ============================================================
        // rendezVousRepo.delete(rv);
        // System.out.println("RDV supprimé.");


        // ============================================================
        // TEST MEDICAMENT REPOSITORY
        // ============================================================


        // ============================================================
        // 1. CREATE
        // ============================================================

        Medicament m = Medicament.builder()
                .nom("Doliprane 500")
                .labo("Sanofi")
                .type("Antalgique")
                .forme(Forme.COMPRIME)  // adapte selon ton enum
                .remboursable(true)
                .prixUnitaire(12.5)
                .description("Antidouleur et antipyrétique")
                .build();

        medicamentRepo.create(m);
        System.out.println("Medicament créé ID = " + m.getId());

        // ============================================================
        // 2. FIND BY ID
        // ============================================================

        Medicament MFound = medicamentRepo.findById(m.getId());
        if (found != null) {
            System.out.println("Trouvé : " + MFound.getNom() + " (" + MFound.getLabo() + ")");
        }

        // ============================================================
        // 3. UPDATE
        // ============================================================

        m.setNom("Doliprane 500 mg");
        m.setPrixUnitaire(13.0);
        m.setDescription("Dose modifiée");

        medicamentRepo.update(m);
        System.out.println("Medicament mis à jour.");

        // Vérification
        Medicament Mupdated = medicamentRepo.findById(m.getId());
        System.out.println("Nouveau nom = " + Mupdated.getNom());
        System.out.println("Nouveau prix = " + Mupdated.getPrixUnitaire());
        System.out.println("Nouvelle description = " + Mupdated.getDescription());

        // ============================================================
        // 4. FIND ALL
        // ============================================================

        List<Medicament> all = medicamentRepo.findAll();
        System.out.println("Nombre total de médicaments : " + all.size());

        // ============================================================
        // 5. SEARCH BY NAME (EXACT)
        // ============================================================

        List<Medicament> exact = medicamentRepo.searchByName("Doliprane 500 mg");
        System.out.println("Search exact results : " + exact.size());

        // ============================================================
        // 6. SEARCH BY NAME LIKE
        // ============================================================

        List<Medicament> like = medicamentRepo.searchByNameLike("Doli");
        System.out.println("Search LIKE results : " + like.size());

        // ============================================================
        // 7. FIND BY LABO
        // ============================================================

        List<Medicament> byLabo = medicamentRepo.findByLabo("Sanofi");
        System.out.println("Médicaments Sanofi : " + byLabo.size());

        // ============================================================
        // 8. FIND REMBOURSABLES
        // ============================================================

        List<Medicament> remb = medicamentRepo.findRemboursables();
        System.out.println("Médicaments remboursables : " + remb.size());

        // ============================================================
        // 9. FIND BY FORME
        // ============================================================

        List<Medicament> byForme = medicamentRepo.findByForme("FORME_COMPRIME"); // adapte enum string
        System.out.println("Médicaments par forme : " + byForme.size());

        // ============================================================
        // 10. FIND BY PRIX BETWEEN
        // ============================================================

        List<Medicament> byPrice = medicamentRepo.findByPrixBetween(5, 20);
        System.out.println("Médicaments entre 5 et 20 MAD : " + byPrice.size());

        // ============================================================
        // 11. DELETE
        // ============================================================

        // medicamentRepo.delete(m);
        // System.out.println("Medicament supprimé.");

        // ============================================================
        // TEST ORDONNANCE REPOSITORY
        // ============================================================


        // ============================================================
        // FAKE CONSULTATION + DOSSIER pour les FK
        // ============================================================

        Consultation fakeConsult = new Consultation();
        fakeConsult.setId(1L);  // assure-toi que cet ID existe en DB

        DossierMedical fakeDossier = new DossierMedical();
        fakeDossier.setId(1L);  // assure-toi que cet ID existe en DB

        // ============================================================
        // 1. CREATE
        // ============================================================

        Ordonnance o = Ordonnance.builder()
                .dateOrdonnance(LocalDate.now())
                .consultation(fakeConsult)
                .dossierMedical(fakeDossier)
                .build();

        ordonnanceRepo.create(o);
        System.out.println("Ordonnance créée ID = " + o.getId());

        // ============================================================
        // 2. FIND BY ID
        // ============================================================

        Ordonnance Ofound = ordonnanceRepo.findById(o.getId());
        if (found != null) {
            System.out.println("Ordonnance trouvée : " + found.getId() +
                    " | Date = " + Ofound.getDateOrdonnance());
        }

        // ============================================================
        // 3. UPDATE
        // ============================================================

        o.setDateOrdonnance(LocalDate.now().minusDays(2));
        ordonnanceRepo.update(o);
        System.out.println("Ordonnance mise à jour.");

        // Vérification update
        Ordonnance Oupdated = ordonnanceRepo.findById(o.getId());
        System.out.println("Nouvelle date = " + Oupdated.getDateOrdonnance());

        // ============================================================
        // 4. FIND ALL
        // ============================================================

        List<Ordonnance> allOrd = ordonnanceRepo.findAll();
        System.out.println("Nombre total d’ordonnances : " + allOrd.size());

        // ============================================================
        // 5. FIND BY DOSSIER
        // ============================================================

        List<Ordonnance> ObyDossier = ordonnanceRepo.findByDossierId(fakeDossier.getId());
        System.out.println("Ordonnances pour dossier = " + ObyDossier.size());

        // ============================================================
        // 6. FIND BY CONSULTATION
        // ============================================================

        List<Ordonnance> byConsult = ordonnanceRepo.findByConsultationId(fakeConsult.getId());
        System.out.println("Ordonnances pour consultation = " + byConsult.size());

        // ============================================================
        // 7. FIND BY DATE EXACT
        // ============================================================

        List<Ordonnance> byDate = ordonnanceRepo.findByDate(Oupdated.getDateOrdonnance());
        System.out.println("Ordonnances à cette date = " + byDate.size());

        // ============================================================
        // 8. FIND BETWEEN DATES
        // ============================================================

        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();

        List<Ordonnance> between = ordonnanceRepo.findBetweenDates(start, end);
        System.out.println("Ordonnances entre " + start + " et " + end + " = " + between.size());

        // ============================================================
        // 9. DELETE
        // ============================================================

        // ordonnanceRepo.delete(o);
        // System.out.println("Ordonnance supprimée.");


        // ============================================================
        // TEST PRESCRIPTION REPOSITORY
        // ============================================================


        // ============================================================
        // FAKE OBJETS POUR LES FK
        // Assure-toi que ces IDs existent dans ta base !
        // ============================================================

        Ordonnance fakeOrd = new Ordonnance();
        fakeOrd.setId(1L);

        Medicament fakeMed = new Medicament();
        fakeMed.setId(1L);

        // ============================================================
        // 1. CREATE
        // ============================================================

        Prescription p = Prescription.builder()
                .qte(2)
                .frequence("2 fois par jour")
                .duree(7)
                .ordonnance(fakeOrd)
                .medicament(fakeMed)
                .build();

        prescriptionRepo.create(p);
        System.out.println("Prescription créée ID = " + p.getId());

        // ============================================================
        // 2. FIND BY ID
        // ============================================================

        Prescription Pfound = prescriptionRepo.findById(p.getId());
        if (found != null) {
            System.out.println("Prescription trouvée : " + found.getId()
                    + " | qte = " + Pfound.getQte()
                    + " | freq = " + Pfound.getFrequence());
        }

        // ============================================================
        // 3. UPDATE
        // ============================================================

        p.setFrequence("1 fois / jour");
        p.setDuree(10);
        prescriptionRepo.update(p);
        System.out.println("Prescription mise à jour.");

        // Vérif update
        Prescription Pupdated = prescriptionRepo.findById(p.getId());
        System.out.println("Nouvelle frequence = " + Pupdated.getFrequence());
        System.out.println("Nouvelle durée = " + Pupdated.getDuree());

        // ============================================================
        // 4. FIND ALL
        // ============================================================

        List<Prescription> Pall = prescriptionRepo.findAll();
        System.out.println("Nombre total de prescriptions : " + Pall.size());

        // ============================================================
        // 5. FIND BY ORDONNANCE
        // ============================================================

        List<Prescription> byOrd = prescriptionRepo.findByOrdonnanceId(fakeOrd.getId());
        System.out.println("Prescriptions pour ordonnance = " + byOrd.size());

        // ============================================================
        // 6. FIND BY MEDICAMENT
        // ============================================================

        List<Prescription> byMed = prescriptionRepo.findByMedicamentId(fakeMed.getId());
        System.out.println("Prescriptions pour medicament = " + byMed.size());

        // ============================================================
        // 7. FIND BY FREQUENCE
        // ============================================================

        List<Prescription> byFreq = prescriptionRepo.findByFrequence("jour");
        System.out.println("Prescriptions contenant 'jour' : " + byFreq.size());

        // ============================================================
        // 8. CHECK IF EXISTS (ordonnance + medicament)
        // ============================================================

        boolean exists = prescriptionRepo.exists(fakeOrd.getId(), fakeMed.getId());
        System.out.println("Existe (ordonnance + medicament) ? " + exists);

        // ============================================================
        // 8) DELETE
        // ============================================================
        // prescriptionRepo.delete(p);
        // System.out.println("Prescription supprimée.");


        // ============================================================
        // ADDITIONAL DELETE & UTILITY TESTS FOR ALL ENTITIES
        // ============================================================
        System.out.println("\n===== COMPREHENSIVE DELETE & UTILITY TESTS =====");

        // Cabinet Tests - Delete
        System.out.println("Cabinet test - Ready for delete (ID: " + cabinet.getId() + ")");
        // cabinetRepo.delete(cabinet); // Commented to avoid cascade issues

        // Patient Tests - Delete
        System.out.println("Patient test - Ready for delete (ID: " + patient.getId() + ")");
        // patientRepo.delete(patient); // Commented to avoid cascade issues
        // Total count already tested above

        // Antecedent Tests - Delete
        System.out.println("Antécédent 1 test - Ready for delete (ID: " + antecedent1.getId() + ")");
        // antecedentRepo.delete(antecedent1);

        // ActeMedical Tests - Delete
        System.out.println("Acte médical test - Ready for delete (ID: " + acte.getId() + ")");
        // acteRepo.delete(acte);

        // Consultation Tests - Delete
        System.out.println("Consultation test - Ready for delete (ID: " + consultation.getId() + ")");
        // consultationRepo.delete(consultation);

        // Charges Tests - Delete
        System.out.println("Charge test - Ready for delete (ID: " + charge.getId() + ")");
        // chargesRepo.delete(charge);

        // Revenus Tests - Delete
        System.out.println("Revenu test - Ready for delete (ID: " + revenu.getId() + ")");
        // revenusRepo.delete(revenu);

        // Facture Tests - Delete
        System.out.println("Facture test - Ready for delete (ID: " + facture.getId() + ")");
        // factureRepo.delete(facture);

        // SuiviFinancier Tests - Delete
        System.out.println("Suivi financier test - Ready for delete (ID: " + situation.getId() + ")");
        // suiviFinancierRepo.delete(suivi);

        // Certificat Tests - Delete
        System.out.println("Certificat test - Ready for delete (ID: " + certificat.getId() + ")");
        // certificatRepo.delete(certificat);

        // Medicament Tests - Delete
        System.out.println("Medicament test - Ready for delete (ID: " + m.getId() + ")");
        // medicamentRepo.delete(m);

        // Ordonnance Tests - Delete
        System.out.println("Ordonnance test - Ready for delete (ID: " + o.getId() + ")");
        // ordonnanceRepo.delete(o);

        // Prescription Tests - Delete
        System.out.println("Prescription test - Ready for delete (ID: " + p.getId() + ")");
        // prescriptionRepo.delete(p);

        // AgendaMensuel Tests - Delete
        System.out.println("AgendaMensuel test - Ready for delete (ID: " + agenda.getId() + ")");
        // agendaMensuelRepo.delete(agenda);

        // RendezVous Tests - Delete
        System.out.println("RendezVous test - Ready for delete (ID: " + rv.getId() + ")");
        // rendezVousRepo.delete(rv);


        System.out.println("===== TEST NOTIFICATION REPOSITORY =====");

        // -------------------------------------------------------------
        // 1) CREATE
        // -------------------------------------------------------------
        Notification n1 = Notification.builder()
                .titre(TitreNotification.RAPPEL)
                .message("Test notification 1")
                .date(LocalDate.now())
                .time(LocalTime.now())
                .type(TypeNotification.SYSTEME)
                .priorite(PrioriteNotification.HAUTE)
                .dateCreation(LocalDateTime.now())
                .creePar("SYSTEM")
                .build();

        repo.create(n1);
        System.out.println("Créé : " + n1);

        // -------------------------------------------------------------
        // 2) FIND ALL
        // -------------------------------------------------------------
        System.out.println("\n--- findAll() ---");
        List<Notification> Nall = repo.findAll();
        Nall.forEach(System.out::println);

        // -------------------------------------------------------------
        // 3) FIND BY ID
        // -------------------------------------------------------------
        System.out.println("\n--- findById() ---");
        Notification find = repo.findById(n1.getId());
        System.out.println(find);

        // -------------------------------------------------------------
        // 4) UPDATE
        // -------------------------------------------------------------
        System.out.println("\n--- update() ---");
        n1.setMessage("Message mis à jour !");
        n1.setModifierPar("ADMIN");
        repo.update(n1);

        Notification Nupdated = repo.findById(n1.getId());
        System.out.println(Nupdated);

        // -------------------------------------------------------------
        // 5) findByTitre
        // -------------------------------------------------------------
        System.out.println("\n--- findByTitre() ---");
        Optional<Notification> findTitre = repo.findByTitre(n1.getTitre().name());
        findTitre.ifPresent(System.out::println);

        // -------------------------------------------------------------
        // 6) findByType
        // -------------------------------------------------------------
        System.out.println("\n--- findByType() ---");
        List<Notification> byType = repo.findByType(n1.getType().name());
        byType.forEach(System.out::println);

        // -------------------------------------------------------------
        // 7) findByPriorite
        // -------------------------------------------------------------
        System.out.println("\n--- findByPriorite() ---");
        List<Notification> byPriorite = repo.findByPriorite(n1.getPriorite().name());
        byPriorite.forEach(System.out::println);

        // -------------------------------------------------------------
        // 8) count()
        // -------------------------------------------------------------
        System.out.println("\n--- count() ---");
        long count = repo.count();
        System.out.println("Total notifications = " + count);

        // -------------------------------------------------------------
        // 9) DELETE
        // -------------------------------------------------------------
        //System.out.println("\n--- deleteById() ---");
        //repo.deleteById(n1.getId());

        //Notification deletedCheck = repo.findById(n1.getId());
        //System.out.println("Après suppression: " + deletedCheck);


        System.out.println("===== TEST ROLE REPOSITORY =====");

        // -------------------------------------------------------------
        // 1) CREATE
        // -------------------------------------------------------------
        Role r1 = Role.builder()
                .libelle(RoleR.ADMIN)
                .privileges(Arrays.asList("CREATE_USER", "DELETE_USER", "READ_ALL"))
                .dateCreation(LocalDateTime.now())
                .creePar("SYSTEM")
                .build();

        RoleRepo.create(r1);
        System.out.println("Créé : " + r1);

        // -------------------------------------------------------------
        // 2) FIND ALL
        // -------------------------------------------------------------
        System.out.println("\n--- findAll() ---");
        List<Role> Rall = RoleRepo.findAll();
        Rall.forEach(System.out::println);

        // -------------------------------------------------------------
        // 3) FIND BY ID
        // -------------------------------------------------------------
        System.out.println("\n--- findById() ---");
        Role Rfind = RoleRepo.findById(r1.getId());
        System.out.println(Rfind);

        // -------------------------------------------------------------
        // 4) UPDATE
        // -------------------------------------------------------------
        System.out.println("\n--- update() ---");
        r1.setPrivileges(Arrays.asList("READ_ALL", "UPDATE_USER"));
        r1.setModifierPar("ADMIN");
        RoleRepo.update(r1);

        Role Rupdated = RoleRepo.findById(r1.getId());
        System.out.println(Rupdated);

        // -------------------------------------------------------------
        // 5) findByLibelle()
        // -------------------------------------------------------------
        System.out.println("\n--- findByLibelle() ---");
        Optional<Role> findLabel = RoleRepo.findByLibelle(RoleR.ADMIN);
        findLabel.ifPresent(System.out::println);

        // -------------------------------------------------------------
        // 6) existsByLibelle()
        // -------------------------------------------------------------
        System.out.println("\n--- existsByLibelle() ---");
        boolean Rexists = RoleRepo.existsByLibelle(RoleR.ADMIN);
        System.out.println("Role ADMIN existe ? " + Rexists);

        // -------------------------------------------------------------
        // 7) count()
        // -------------------------------------------------------------
        System.out.println("\n--- count() ---");
        long Rcount = RoleRepo.count();
        System.out.println("Total roles = " + Rcount);

        // -------------------------------------------------------------
        // 8) DELETE
        // -------------------------------------------------------------
        //System.out.println("\n--- deleteById() ---");
        //repo.deleteById(r1.getId());

        // Role deletedCheck = RoleRepo.findById(r1.getId());
        //System.out.println("Après suppression: " + deletedCheck);

        System.out.println("\n===== TEST RELATIONS UTILISATEUR ↔ ROLE & NOTIFICATION =====");

// 1. Créer un utilisateur (ex: un admin)
        Utilisateur admin = new Utilisateur() {};
        admin.setNom("Admin");
        admin.setPrenom("Super");
        admin.setEmail("admin@whitelab.ma");
        admin.setTelephone("0600000001");
        admin.setCin("AB123456");
        admin.setDateNaissance(LocalDate.of(1985, 3, 15));
        admin.setSexe(Sexe.Homme);
        admin.setMotDePasse("admin123");
        admin.setCreePar("SYSTEM");
        admin.setDateCreation(LocalDateTime.now());

        UserRepo.create(admin);
        System.out.println("Utilisateur créé ID = " + admin.getId());

// 2. Créer deux rôles
        Role roleAdmin = Role.builder()
                .libelle(RoleR.ADMIN)
                .privileges(Arrays.asList("GESTION_UTILISATEURS", "GESTION_CABINET", "TOUT"))
                .creePar("SYSTEM")
                .build();

        Role roleSecretaire = Role.builder()
                .libelle(RoleR.SECRETAIRE)
                .privileges(Arrays.asList("GERER_RDV", "VOIR_PATIENTS"))
                .creePar("SYSTEM")
                .build();

        RoleRepo.create(roleAdmin);
        RoleRepo.create(roleSecretaire);
        System.out.println("Rôle ADMIN ID = " + roleAdmin.getId());
        System.out.println("Rôle SECRETAIRE ID = " + roleSecretaire.getId());

// 3. Créer deux notifications
        Notification notif1 = Notification.builder()
                .titre(TitreNotification.RAPPEL)
                .message("Bienvenue dans WhiteLab !")
                .date(LocalDate.now())
                .time(LocalTime.now())
                .type(TypeNotification.SYSTEME)
                .priorite(PrioriteNotification.HAUTE)
                .creePar("SYSTEM")
                .build();

        Notification notif2 = Notification.builder()
                .titre(TitreNotification.INFO)
                .message("Mise à jour disponible")
                .date(LocalDate.now())
                .time(LocalTime.now().plusMinutes(30))
                .type(TypeNotification.SYSTEME)
                .priorite(PrioriteNotification.BASSE)
                .creePar("SYSTEM")
                .build();

        repo.create(notif1);
        repo.create(notif2);
        System.out.println("Notif 1 ID = " + notif1.getId());
        System.out.println("Notif 2 ID = " + notif2.getId());

// 4. Lier les rôles à l'utilisateur
        UserRepo.addRoleToUtilisateur(admin.getId(), roleAdmin.getId());
        UserRepo.addRoleToUtilisateur(admin.getId(), roleSecretaire.getId());
        System.out.println("Rôles ajoutés à l'utilisateur");

// 5. Lier les notifications à l'utilisateur
        UserRepo.addNotificationToUtilisateur(admin.getId(), notif1.getId());
        UserRepo.addNotificationToUtilisateur(admin.getId(), notif2.getId());
        System.out.println("Notifications ajoutées à l'utilisateur");


// 7. Test suppression d'un rôle
        //System.out.println("\nSuppression du rôle SECRETAIRE...");
        //UserRepo.removeRoleFromUtilisateur(admin.getId(), roleSecretaire.getId());



// 8. Test cascade delete (suppression de l'utilisateur)
        //System.out.println("\nSuppression de l'utilisateur (doit supprimer les relations)");
        //UserRepo.delete(admin); // ou UserRepo.deleteById(admin.getId());




        // ============================================================
        // FIN DES TESTS
        // ============================================================
        System.out.println("\n=== TOUS LES TESTS ONT RÉUSSI ===");
        System.out.println("Patient ID        : " + patient.getId());
        System.out.println("Dossier Médical ID: " + dossier.getId());
        System.out.println("Consultation ID   : " + consultation.getId());
        System.out.println("Cabinet ID        : " + cabinet.getId());
        System.out.println("Antécédents du patient : " + patientRepo.getAntecedentsOfPatient(patient.getId()).size());
    }
}