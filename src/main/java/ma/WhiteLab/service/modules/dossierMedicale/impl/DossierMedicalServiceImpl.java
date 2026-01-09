package ma.WhiteLab.service.modules.dossierMedicale.impl;

import ma.WhiteLab.common.utils.RepoFactory;
import ma.WhiteLab.entities.agenda.RendezVous;
import ma.WhiteLab.entities.dossierMedical.*;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.repository.modules.dossierMedical.api.*;
import ma.WhiteLab.repository.modules.patient.api.PatientRepository;
import ma.WhiteLab.repository.modules.user.api.UtilisateurRepository;
import ma.WhiteLab.service.modules.dossierMedicale.api.DossierMedicalService;
import ma.WhiteLab.service.modules.dossierMedicale.api.DossierMedicalValidator;
import ma.WhiteLab.service.common.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final RepoFactory<DossierMedicalRepository> dossierRepoFactory;
    private final RepoFactory<ConsultationRepository> consultationRepoFactory;
    private final RepoFactory<OrdonnanceRepository> ordonnanceRepoFactory;
    private final RepoFactory<CertificatRepository> certificatRepoFactory;
    private final RepoFactory<SituationFinanciereRepository> situationRepoFactory;
    private final RepoFactory<PatientRepository> patientRepoFactory;
    private final RepoFactory<UtilisateurRepository<Medecin>> medecinRepoFactory;

    private final DossierMedicalValidator validator = new DossierMedicalValidatorImpl();

    public DossierMedicalServiceImpl(
            RepoFactory<DossierMedicalRepository> dossierRepoFactory,
            RepoFactory<ConsultationRepository> consultationRepoFactory,
            RepoFactory<OrdonnanceRepository> ordonnanceRepoFactory,
            RepoFactory<CertificatRepository> certificatRepoFactory,
            RepoFactory<SituationFinanciereRepository> situationRepoFactory,
            RepoFactory<PatientRepository> patientRepoFactory,
            RepoFactory<UtilisateurRepository<Medecin>> medecinRepoFactory) {

        this.dossierRepoFactory = dossierRepoFactory;
        this.consultationRepoFactory = consultationRepoFactory;
        this.ordonnanceRepoFactory = ordonnanceRepoFactory;
        this.certificatRepoFactory = certificatRepoFactory;
        this.situationRepoFactory = situationRepoFactory;
        this.patientRepoFactory = patientRepoFactory;
        this.medecinRepoFactory = medecinRepoFactory;
    }

    @Override
    public DossierMedical createDossierForPatient(Long patientId, Long medecinId, String creePar) {
        Map<String, String> errors = validator.validateCreation(patientId, medecinId, creePar);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erreurs de validation : " + errors);
        }

        return Transaction.initTransaction(cnx -> {
            PatientRepository patientRepo = patientRepoFactory.create(cnx);
            UtilisateurRepository<Medecin> medecinRepo = medecinRepoFactory.create(cnx);
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);

            Patient patient = patientRepo.findById(patientId);
            if (patient == null) {
                throw new IllegalArgumentException("Patient avec l'ID " + patientId + " n'existe pas.");
            }

            Medecin medecin = medecinRepo.findById(medecinId);
            if (medecin == null) {
                throw new IllegalArgumentException("Médecin avec l'ID " + medecinId + " n'existe pas.");
            }

            if (dossierRepo.findByPatientId(patientId).isPresent()) {
                throw new IllegalStateException("Un dossier médical existe déjà pour ce patient.");
            }

            DossierMedical dossier = DossierMedical.builder()
                    .pat(patient)
                    .medecine(medecin)
                    .historique("")
                    .creePar(creePar.trim())
                    .dateCreation(LocalDateTime.now())
                    .build();

            dossierRepo.create(dossier);
            return dossier;
        });
    }

    @Override
    public DossierMedical updateHistorique(Long dossierId, String nouveauHistorique, String modifierPar) {
        Map<String, String> errors = validator.validateUpdateHistorique(dossierId, nouveauHistorique, modifierPar);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erreurs de validation : " + errors);
        }

        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);

            DossierMedical dossier = dossierRepo.findById(dossierId);
            if (dossier == null) {
                throw new IllegalArgumentException("Dossier médical avec l'ID " + dossierId + " n'existe pas.");
            }

            dossier.setHistorique(nouveauHistorique);
            dossier.setModifierPar(modifierPar.trim());
            dossier.setDateMiseAJour(LocalDateTime.now());

            dossierRepo.update(dossier);
            return dossier;
        });
    }

    // ====================== MÉTHODES DE LECTURE ======================

    @Override
    public Optional<DossierMedical> getDossierById(Long id) {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            return Optional.ofNullable(dossierRepo.findById(id));
        });
    }

    @Override
    public Optional<DossierMedical> getDossierByPatientId(Long patientId) {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            return dossierRepo.findByPatientId(patientId);
        });
    }

    @Override
    public List<DossierMedical> getDossiersByMedecinId(Long medecinId) {
        if (medecinId == null) {
            return List.of();
        }

        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            PatientRepository patientRepo = patientRepoFactory.create(cnx);
            UtilisateurRepository<Medecin> medecinRepo = medecinRepoFactory.create(cnx);

            // 1. Récupère les dossiers bruts (avec seulement les IDs des relations)
            List<DossierMedical> dossiers = dossierRepo.findByMedecinId(medecinId);

            // 2. Pour chaque dossier, charge les entités complètes
            for (DossierMedical dossier : dossiers) {
                // Charge le patient complet
                if (dossier.getPat() != null && dossier.getPat().getId() != null) {
                    Patient fullPatient = patientRepo.findById(dossier.getPat().getId());
                    if (fullPatient != null) {
                        dossier.setPat(fullPatient);
                    }
                }

                // Charge le médecin complet (le connecté)
                Medecin fullMedecin = medecinRepo.findById(medecinId);
                if (fullMedecin != null) {
                    dossier.setMedecine(fullMedecin);
                }
            }

            // Optionnel : filtre les dossiers où patient ou médecin est manquant
            return dossiers.stream()
                    .filter(d -> d.getPat() != null && d.getPat().getNom() != null)
                    .filter(d -> d.getMedecine() != null && d.getMedecine().getNom() != null)
                    .toList();
        });
    }

    @Override
    public List<DossierMedical> getAllDossiers() {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            return dossierRepo.findAll();
        });
    }

    @Override
    public long countDossiers() {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            return dossierRepo.count();
        });
    }

    @Override
    public List<DossierMedical> searchByPatientName(String nomOrPrenom) {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            return dossierRepo.findByPatientName(nomOrPrenom);
        });
    }

    @Override
    public List<DossierMedical> searchByHistorique(String keyword) {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            return dossierRepo.searchByHistorique(keyword);
        });
    }

    @Override
    public List<DossierMedical> findRecentDossiers(int days) {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            return dossierRepo.findRecent(days);
        });
    }

    @Override
    public Patient getPatient(Long dossierId) {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            DossierMedical dossier = dossierRepo.findById(dossierId);
            return dossier != null ? dossier.getPat() : null;
        });
    }

    @Override
    public List<Consultation> getConsultations(Long dossierId) {
        return Transaction.initTransaction(cnx -> {
            ConsultationRepository consultationRepo = consultationRepoFactory.create(cnx);
            return consultationRepo.findByDossierMedicalId(dossierId);
        });
    }

    @Override
    public List<Ordonnance> getOrdonnances(Long dossierId) {
        return Transaction.initTransaction(cnx -> {
            OrdonnanceRepository ordonnanceRepo = ordonnanceRepoFactory.create(cnx);
            return ordonnanceRepo.findByDossierId(dossierId);
        });
    }

    @Override
    public List<Certificat> getCertificats(Long dossierId) {
        return Transaction.initTransaction(cnx -> {
            CertificatRepository certificatRepo = certificatRepoFactory.create(cnx);
            return certificatRepo.findByDossierMedicalId(dossierId);
        });
    }

    @Override
    public List<Antecedent> getAntecedents(Long dossierId) {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            DossierMedical dossier = dossierRepo.findById(dossierId);
            return dossier != null
                    ? dossier.getPat().getAntecedents()
                    : List.of();
        });
    }

    @Override
    public List<RendezVous> getRendezVous(Long dossierId) {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            DossierMedical dossier = dossierRepo.findById(dossierId);
            return dossier != null ? dossier.getRendezVous() : List.of();
        });
    }

    @Override
    public SituationFinanciere getSituationFinanciere(Long dossierId) {
        return Transaction.initTransaction(cnx -> {
            DossierMedicalRepository dossierRepo = dossierRepoFactory.create(cnx);
            DossierMedical dossier = dossierRepo.findById(dossierId);
            return dossier != null ? dossier.getSituationFinanciere() : null;
        });
    }

    @Override
    public byte[] exportDossierToPdf(Long dossierId) {
        throw new UnsupportedOperationException("Export PDF en cours de développement");
    }
}