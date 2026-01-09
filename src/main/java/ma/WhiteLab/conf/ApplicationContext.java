package ma.WhiteLab.conf;

import ma.WhiteLab.common.consoleLog.ConsoleLogger;
import ma.WhiteLab.common.utils.FactoryUtils;
import ma.WhiteLab.common.utils.RepoFactory;

import ma.WhiteLab.mvc.controllers.modules.auth.api.AuthController;
import ma.WhiteLab.mvc.controllers.dashboardModule.api.DashboardController;
import ma.WhiteLab.mvc.controllers.modules.dossierMedicale.api.DossiersController;
import ma.WhiteLab.mvc.controllers.modules.dossierMedicale.impl.DossiersControllerImpl;
import ma.WhiteLab.mvc.controllers.modules.patient.api.PatientsController;           // Interface
import ma.WhiteLab.mvc.controllers.modules.patient.impl.PatientsControllerImpl;     // Impl
import ma.WhiteLab.mvc.controllers.profileModule.api.ProfileController;

import ma.WhiteLab.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.WhiteLab.repository.modules.agenda.api.RendezVousRepository;
import ma.WhiteLab.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.WhiteLab.repository.modules.cabinet.api.ChargesRepository;
import ma.WhiteLab.repository.modules.cabinet.api.RevenusRepository;
import ma.WhiteLab.repository.modules.cabinet.api.StatistiquesRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.*;
import ma.WhiteLab.repository.modules.notifications.api.NotificationRepository;
import ma.WhiteLab.repository.modules.patient.api.AntecedentRepository;
import ma.WhiteLab.repository.modules.patient.api.PatientRepository;
import ma.WhiteLab.repository.modules.user.api.UtilisateurRepository;
import ma.WhiteLab.repository.modules.user.api.RoleRepository;

import ma.WhiteLab.service.modules.auth.api.AuthService;
import ma.WhiteLab.service.modules.auth.api.AuthorizationService;
import ma.WhiteLab.service.modules.auth.api.CredentialsValidator;
import ma.WhiteLab.service.modules.auth.api.PasswordEncoder;

import ma.WhiteLab.service.modules.patient.api.PatientService;
import ma.WhiteLab.service.modules.patient.impl.PatientServiceImpl;

import ma.WhiteLab.service.modules.profileService.api.ProfileService;
import ma.WhiteLab.service.modules.profileService.api.ProfileValidator;
import ma.WhiteLab.service.modules.profileService.api.ChangePasswordValidator;

import ma.WhiteLab.service.modules.dossierMedicale.api.DossierMedicalService;
import ma.WhiteLab.service.modules.dossierMedicale.impl.DossierMedicalServiceImpl;

import ma.WhiteLab.entities.user.Medecin;

import java.io.InputStream;
import java.sql.Connection;
import java.util.*;

public final class ApplicationContext {

    private static volatile ApplicationContext INSTANCE;

    private static final Map<Class<?>, Object> context = new LinkedHashMap<>();

    private Properties props;
    private final ClassLoader cl = Thread.currentThread().getContextClassLoader();

    // REPO FACTORIES
    private RepoFactory<UtilisateurRepository> utilisateurRepositoryFactory;
    private RepoFactory<RoleRepository> roleRepositoryFactory;
    private RepoFactory<PatientRepository> patientRepoFactory;
    private RepoFactory<CabinetMedicaleRepository> cabinetRepoFactory;
    private RepoFactory<DossierMedicalRepository> dossierMedicalRepoFactory;
    private RepoFactory<MedicamentRepository> medicamentRepoFactory;
    private RepoFactory<ActeMedicalRepository> acteRepoFactory;
    private RepoFactory<AntecedentRepository> antecedentRepoFactory;
    private RepoFactory<ConsultationRepository> consultationRepoFactory;
    private RepoFactory<CertificatRepository> certificatRepoFactory;
    private RepoFactory<FactureRepository> factureRepoFactory;
    private RepoFactory<InterventionMedecinRepository> interventionRepoFactory;
    private RepoFactory<OrdonnanceRepository> ordonnanceRepoFactory;
    private RepoFactory<PrescriptionRepository> prescriptionRepoFactory;
    private RepoFactory<AgendaMensuelRepository> agendaMensuelRepoFactory;
    private RepoFactory<RendezVousRepository> rendezVousRepoFactory;
    private RepoFactory<NotificationRepository> notificationRepoFactory;
    private RepoFactory<ChargesRepository> chargesRepoFactory;
    private RepoFactory<RevenusRepository> revenusRepoFactory;
    private RepoFactory<StatistiquesRepository> statistiquesRepoFactory;
    private RepoFactory<SituationFinanciereRepository> situationFinanciereRepoFactory;

    private ApplicationContext() {
        try {
            loadPropertiesAndBeans();
            ConsoleLogger.info("ApplicationContext initialisé avec succès : " + context.size() + " beans chargés.");
        } catch (Exception e) {
            ConsoleLogger.error("ÉCHEC CRITIQUE : Impossible d'initialiser ApplicationContext");
            e.printStackTrace();
            throw new RuntimeException("Erreur fatale lors de l'initialisation de l'application", e);
        }
    }

    public static ApplicationContext getInstance() {
        if (INSTANCE == null) {
            synchronized (ApplicationContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApplicationContext();
                }
            }
        }
        return INSTANCE;
    }

    private void loadPropertiesAndBeans() throws Exception {
        InputStream input = cl.getResourceAsStream("config/beans.properties");
        if (input == null) {
            throw new IllegalStateException("Fichier config/beans.properties introuvable dans le classpath");
        }

        props = new Properties();
        props.load(input);
        input.close();
        context.put(Properties.class, props);

        ConsoleLogger.info("Chargement des beans depuis beans.properties...");

        // 1. REPO FACTORIES
        utilisateurRepositoryFactory   = buildRepoFactory("utilisateurRepository", UtilisateurRepository.class);
        roleRepositoryFactory          = buildRepoFactory("roleRepo", RoleRepository.class);
        patientRepoFactory             = buildRepoFactory("patientRepo", PatientRepository.class);
        cabinetRepoFactory             = buildRepoFactory("cabinetRepo", CabinetMedicaleRepository.class);
        dossierMedicalRepoFactory      = buildRepoFactory("dossierMedicalRepo", DossierMedicalRepository.class);
        medicamentRepoFactory          = buildRepoFactory("medicamentRepo", MedicamentRepository.class);
        acteRepoFactory                = buildRepoFactory("acteRepo", ActeMedicalRepository.class);
        antecedentRepoFactory          = buildRepoFactory("antecedentRepo", AntecedentRepository.class);
        consultationRepoFactory        = buildRepoFactory("consultationRepo", ConsultationRepository.class);
        certificatRepoFactory          = buildRepoFactory("certificatRepo", CertificatRepository.class);
        factureRepoFactory             = buildRepoFactory("factureRepo", FactureRepository.class);
        interventionRepoFactory        = buildRepoFactory("interventionRepo", InterventionMedecinRepository.class);
        ordonnanceRepoFactory          = buildRepoFactory("ordonnanceRepo", OrdonnanceRepository.class);
        prescriptionRepoFactory        = buildRepoFactory("prescriptionRepo", PrescriptionRepository.class);
        agendaMensuelRepoFactory       = buildRepoFactory("agendaMensuelRepo", AgendaMensuelRepository.class);
        rendezVousRepoFactory          = buildRepoFactory("rendezVousRepo", RendezVousRepository.class);
        notificationRepoFactory        = buildRepoFactory("notificationRepo", NotificationRepository.class);
        chargesRepoFactory             = buildRepoFactory("chargesRepo", ChargesRepository.class);
        revenusRepoFactory             = buildRepoFactory("revenusRepo", RevenusRepository.class);
        statistiquesRepoFactory        = buildRepoFactory("statRepo", StatistiquesRepository.class);
        situationFinanciereRepoFactory = buildRepoFactory("suiviFinancierRepo", SituationFinanciereRepository.class);

        // Factory spécialisée pour Medecin
        @SuppressWarnings("unchecked")
        RepoFactory<UtilisateurRepository<Medecin>> medecinRepoFactory = (Connection c) -> {
            String implClassName = props.getProperty("utilisateurRepository");
            try {
                Class<?> implClass = Class.forName(implClassName.trim());
                UtilisateurRepository<?> rawRepo = (UtilisateurRepository<?>) implClass
                        .getDeclaredConstructor(Connection.class)
                        .newInstance(c);
                return (UtilisateurRepository<Medecin>) rawRepo;
            } catch (Exception e) {
                throw new RuntimeException("Impossible d'instancier le repository pour Medecin", e);
            }
        };

        // 2. VALIDATORS & ENCODERS
        var credentialsValidator   = FactoryUtils.buildImplInstance(props, "credentialsValidator", CredentialsValidator.class);
        var passwordEncoder        = FactoryUtils.buildImplInstance(props, "passwordEncoder", PasswordEncoder.class);
        var profileValidator       = FactoryUtils.buildImplInstance(props, "profileValidator", ProfileValidator.class);
        var changePasswordValidator = FactoryUtils.buildImplInstance(props, "changePasswordValidator", ChangePasswordValidator.class);

        context.put(CredentialsValidator.class, credentialsValidator);
        context.put(PasswordEncoder.class, passwordEncoder);
        context.put(ProfileValidator.class, profileValidator);
        context.put(ChangePasswordValidator.class, changePasswordValidator);

        // 3. SERVICES
        var authService = FactoryUtils.buildImplInstance(props, "authService", AuthService.class,
                utilisateurRepositoryFactory, roleRepositoryFactory, credentialsValidator, passwordEncoder);

        var authorizationService = FactoryUtils.buildImplInstance(props, "authorizationService", AuthorizationService.class);

        var profileService = FactoryUtils.buildImplInstance(props, "profileService", ProfileService.class,
                utilisateurRepositoryFactory, profileValidator, changePasswordValidator, passwordEncoder);

        var patientService = new PatientServiceImpl(patientRepoFactory);

        var dossierMedicalService = new DossierMedicalServiceImpl(
                dossierMedicalRepoFactory,
                consultationRepoFactory,
                ordonnanceRepoFactory,
                certificatRepoFactory,
                situationFinanciereRepoFactory,
                patientRepoFactory,
                medecinRepoFactory
        );

        context.put(AuthService.class, authService);
        context.put(AuthorizationService.class, authorizationService);
        context.put(ProfileService.class, profileService);
        context.put(PatientService.class, patientService);
        context.put(DossierMedicalService.class, dossierMedicalService);

        // 4. CONTROLLERS
        var authController = FactoryUtils.buildImplInstance(props, "authController", AuthController.class, authService);
        var profileController = FactoryUtils.buildImplInstance(props, "profileController", ProfileController.class, profileService);
        var dashboardController = FactoryUtils.buildImplInstance(props, "dashboardController", DashboardController.class, authorizationService, authController);
        var dossiersController = new DossiersControllerImpl(dossierMedicalService);

        // AJOUT DU PATIENTS CONTROLLER - CORRECTION FINALE
        var patientsController = new PatientsControllerImpl(patientService);

        context.put(AuthController.class, authController);
        context.put(ProfileController.class, profileController);
        context.put(DashboardController.class, dashboardController);
        context.put(DossiersController.class, dossiersController);
        context.put(PatientsController.class, patientsController);

        ConsoleLogger.info("PatientsController bean enregistré avec succès");
    }

    public static <T> T getBean(Class<T> beanClass) {
        Object bean = context.get(beanClass);
        if (bean == null) {
            ConsoleLogger.warn("Bean non trouvé : " + beanClass.getSimpleName());
            throw new IllegalArgumentException("Bean non trouvé : " + beanClass.getSimpleName());
        }
        return beanClass.cast(bean);
    }

    private <T> RepoFactory<T> buildRepoFactory(String propertyKey, Class<T> apiType) {
        String implClassName = props.getProperty(propertyKey);
        if (implClassName == null || implClassName.isBlank()) {
            throw new IllegalArgumentException("Propriété manquante dans beans.properties : " + propertyKey);
        }

        return (Connection c) -> {
            try {
                Class<?> implClass = Class.forName(implClassName.trim());
                return apiType.cast(implClass.getDeclaredConstructor(Connection.class).newInstance(c));
            } catch (Exception e) {
                throw new RuntimeException("Impossible d'instancier " + implClassName + " avec Connection", e);
            }
        };
    }

    // GETTERS REPO FACTORIES (inchangés)
    public RepoFactory<CabinetMedicaleRepository> getCabinetRepoFactory() { return cabinetRepoFactory; }
    public RepoFactory<UtilisateurRepository> getUtilisateurRepositoryFactory() { return utilisateurRepositoryFactory; }
    public RepoFactory<RoleRepository> getRoleRepositoryFactory() { return roleRepositoryFactory; }
    public RepoFactory<PatientRepository> getPatientRepoFactory() { return patientRepoFactory; }
    public RepoFactory<DossierMedicalRepository> getDossierMedicalRepoFactory() { return dossierMedicalRepoFactory; }
    public RepoFactory<MedicamentRepository> getMedicamentRepoFactory() { return medicamentRepoFactory; }
    public RepoFactory<ActeMedicalRepository> getActeRepoFactory() { return acteRepoFactory; }
    public RepoFactory<AntecedentRepository> getAntecedentRepoFactory() { return antecedentRepoFactory; }
    public RepoFactory<ConsultationRepository> getConsultationRepoFactory() { return consultationRepoFactory; }
    public RepoFactory<CertificatRepository> getCertificatRepoFactory() { return certificatRepoFactory; }
    public RepoFactory<FactureRepository> getFactureRepoFactory() { return factureRepoFactory; }
    public RepoFactory<InterventionMedecinRepository> getInterventionRepoFactory() { return interventionRepoFactory; }
    public RepoFactory<OrdonnanceRepository> getOrdonnanceRepoFactory() { return ordonnanceRepoFactory; }
    public RepoFactory<PrescriptionRepository> getPrescriptionRepoFactory() { return prescriptionRepoFactory; }
    public RepoFactory<AgendaMensuelRepository> getAgendaMensuelRepoFactory() { return agendaMensuelRepoFactory; }
    public RepoFactory<RendezVousRepository> getRendezVousRepoFactory() { return rendezVousRepoFactory; }
    public RepoFactory<NotificationRepository> getNotificationRepoFactory() { return notificationRepoFactory; }
    public RepoFactory<ChargesRepository> getChargesRepoFactory() { return chargesRepoFactory; }
    public RepoFactory<RevenusRepository> getRevenusRepoFactory() { return revenusRepoFactory; }
    public RepoFactory<StatistiquesRepository> getStatistiquesRepoFactory() { return statistiquesRepoFactory; }
    public RepoFactory<SituationFinanciereRepository> getSituationFinanciereRepoFactory() { return situationFinanciereRepoFactory; }

    public String getProperty(String key, String defaultValue) {
        String value = props.getProperty(key);
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    public String getProperty(String key) { return getProperty(key, null); }

    public static String splitCamelCase(String s) {
        if (s == null || s.isBlank()) return s;
        return s.replaceAll("([a-z])([A-Z])", "$1 $2")
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2");
    }

    public void printAllBeans() {
        System.out.println("\n=== Beans chargés dans ApplicationContext ===");
        context.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Class::getSimpleName)))
                .forEach(e -> System.out.println(splitCamelCase(e.getKey().getSimpleName()) + " → " + e.getValue().getClass().getName()));
        System.out.println("Total : " + context.size() + " beans\n");
    }
}