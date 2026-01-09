package ma.WhiteLab.service.modules.consultation.impl;

import ma.WhiteLab.common.utils.RepoFactory;
import ma.WhiteLab.entities.dossierMedical.Consultation;
import ma.WhiteLab.entities.dossierMedical.InterventionMedecin;
import ma.WhiteLab.entities.dossierMedical.ActeMedical;
import ma.WhiteLab.service.modules.consultation.dto.ConsultationDTO;
import ma.WhiteLab.service.modules.consultation.dto.InterventionDTO;
import ma.WhiteLab.service.modules.consultation.dto.ActeDTO;
import ma.WhiteLab.service.modules.consultation.api.ConsultationService;
import ma.WhiteLab.service.common.Transaction;
import ma.WhiteLab.common.exceptions.ValidationException;

import ma.WhiteLab.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.InterventionMedecinRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.ActeMedicalRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationServiceImpl implements ConsultationService {

    private final RepoFactory<ConsultationRepository> consultRepoFactory;
    private final RepoFactory<InterventionMedecinRepository> intervRepoFactory; // Assume factories exists or generic
    private final RepoFactory<ActeMedicalRepository> acteRepoFactory;

    private final ConsultationValidator validator = new ConsultationValidator();

    public ConsultationServiceImpl(
            RepoFactory<ConsultationRepository> consultRepoFactory,
            RepoFactory<InterventionMedecinRepository> intervRepoFactory,
            RepoFactory<ActeMedicalRepository> acteRepoFactory) {
        this.consultRepoFactory = consultRepoFactory;
        this.intervRepoFactory = intervRepoFactory;
        this.acteRepoFactory = acteRepoFactory;
    }

    @Override
    public ConsultationDTO create(ConsultationDTO dto) throws ValidationException {
        // Validation
        java.util.Map<String, String> errors = validator.validate(dto);
        if (!errors.isEmpty()) throw new ValidationException("Consultation invalide");

        return Transaction.initTransaction(cnx -> {
            ConsultationRepository repo = consultRepoFactory.create(cnx);
            
            Consultation consultation = Consultation.builder()
                .dateConsultation(LocalDateTime.parse(dto.getDateConsultation())) // ISO DateTime
                .motif(dto.getMotif())
                .diagnostic(dto.getDiagnostic())
                .montant(dto.getMontant())
                .build();
            
            repo.create(consultation);
            
            // Gestion Interventions incluses
            if (dto.getInterventions() != null && !dto.getInterventions().isEmpty()) {
                InterventionMedecinRepository interRepo = intervRepoFactory.create(cnx);
                for (InterventionDTO iDto : dto.getInterventions()) {
                    InterventionMedecin interv = InterventionMedecin.builder()
                        .description(iDto.getDescription())
                        .type(iDto.getType())
                        .dent(iDto.getDent())
                        .consultation(consultation)
                        .build();
                    interRepo.create(interv);
                }
            }

            // Gestion Actes inclus
             if (dto.getActes() != null && !dto.getActes().isEmpty()) {
                ActeMedicalRepository acteRepo = acteRepoFactory.create(cnx);
                for (ActeDTO aDto : dto.getActes()) {
                    ActeMedical acte = ActeMedical.builder()
                        .libelle(aDto.getLibelle())
                        .prixBase(aDto.getPrixBase())
                        .categorie(aDto.getCategorie())
                        //.consultation(consultation) // Assuming Acte linked to Consultation
                        .build();
                    acteRepo.create(acte);
                }
            }
            
            dto.setId(consultation.getId());
            return dto;
        });
    }

    @Override
    public ConsultationDTO findById(Long id) {
        // Impl mini pour l'exemple
        return null; 
    }

    @Override
    public void addIntervention(Long consultationId, InterventionDTO interventionDto) {
         Transaction.initTransaction(cnx -> {
             // Logic retrieval check + insert
             return null;
         });
    }

    @Override
    public void addActe(Long consultationId, ActeDTO acteDto) {
         Transaction.initTransaction(cnx -> {
             // Logic retrieval check + insert
             return null;
         });
    }
}
