package ma.WhiteLab.repository.test;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.entities.cabinet.CabinetMedicale;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.entities.user.*;
import ma.WhiteLab.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.WhiteLab.repository.modules.user.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestUsersComplete {

    private static MedecinRepository medecinRepo;
    private static SecretaireRepository secretaireRepo;
    private static AdminRepository adminRepo;
    private static CabinetMedicaleRepository cabinetRepo;

    public static void main(String[] args) {
        ApplicationContext ctx = new ApplicationContext("config/beans.properties");

        medecinRepo = ctx.getBean(MedecinRepository.class);
        secretaireRepo = ctx.getBean(SecretaireRepository.class);
        adminRepo = ctx.getBean(AdminRepository.class);
        cabinetRepo = ctx.getBean(CabinetMedicaleRepository.class);

        // Créer un cabinet pour les staff
        CabinetMedicale cabinet = CabinetMedicale.builder()
                .nom("Cabinet Central")
                .email("contact@cabinet.ma")
                .tel1("0600000000")
                .creePar("system")
                .build();
        cabinetRepo.create(cabinet);

        // ----------------- Admin -----------------
        Admin admin = Admin.builder()
                .nom("Admin")
                .prenom("Super")
                .email("admin@hopital.ma")
                .adresse("1 Rue Admin, Casablanca")
                .cin("EF987654")
                .telephone("0600000003")
                .dateNaissance(LocalDate.of(1975, 12, 5))
                .sexe(Sexe.Homme)
                .motDePasse("passwordAdmin")
                .dateCreation(LocalDateTime.now())
                .creePar("system")
                .build();
        adminRepo.create(admin);
        System.out.println("Admin créé → ID = " + admin.getId());

        // ----------------- Médecin -----------------
        Medecin medecin = Medecin.builder()
                .nom("Smith")
                .prenom("Alice")
                .email("alice.smith@hopital.ma")
                .adresse("123 Rue Cardio, Casablanca")
                .cin("AB123456")
                .telephone("0600000001")
                .dateNaissance(LocalDate.of(1980, 3, 15))
                .sexe(Sexe.Femme)
                .motDePasse("passwordMedecin")
                .dateCreation(LocalDateTime.now())
                .creePar("system")
                .salaire(12000.0)
                .prime(1500.0)
                .dateRecrutement(LocalDate.of(2010, 5, 1))
                .soldeConge(25)
                .cabinetMedicale(cabinet)
                .specialite("Cardiologie")
                .build();
        medecinRepo.create(medecin);
        System.out.println("Médecin créé → ID = " + medecin.getId());

        // ----------------- Secrétaire -----------------
        Secretaire secretaire = Secretaire.builder()
                .nom("Benali")
                .prenom("Karim")
                .email("karim.benali@hopital.ma")
                .adresse("45 Rue Admin, Casablanca")
                .cin("CD654321")
                .telephone("0600000002")
                .dateNaissance(LocalDate.of(1992, 7, 22))
                .sexe(Sexe.Homme)
                .motDePasse("passwordSecretaire")
                .dateCreation(LocalDateTime.now())
                .creePar("system")
                .salaire(7000.0)
                .prime(500.0)
                .dateRecrutement(LocalDate.of(2018, 2, 15))
                .soldeConge(20)
                .cabinetMedicale(cabinet)
                .numCNS("CNS12345")
                .commission(300.0)
                .build();
        secretaireRepo.create(secretaire);
        System.out.println("Secrétaire créé → ID = " + secretaire.getId());
    }
}
