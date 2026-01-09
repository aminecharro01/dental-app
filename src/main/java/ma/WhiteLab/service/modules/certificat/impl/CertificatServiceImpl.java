package ma.WhiteLab.service.modules.certificat.impl;

import ma.WhiteLab.common.utils.RepoFactory;
import ma.WhiteLab.entities.dossierMedical.Certificat;
import ma.WhiteLab.service.modules.certificat.dto.CertificatDTO;
import ma.WhiteLab.service.modules.certificat.api.CertificatService;
import ma.WhiteLab.service.common.Transaction;
import ma.WhiteLab.common.exceptions.ValidationException;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Map;

// On suppose que le repository existe ou est générique. 
// Si inexistant, il faudra le créer. Utilisation de placeholder pour compilation.
import ma.WhiteLab.repository.modules.dossierMedical.api.CertificatRepository; 

public class CertificatServiceImpl implements CertificatService {

    private final RepoFactory<CertificatRepository> repoFactory;
    private final CertificatValidator validator = new CertificatValidator();
    private final CertificatPrintService printService = new CertificatPrintService();

    public CertificatServiceImpl(RepoFactory<CertificatRepository> repoFactory) {
        this.repoFactory = repoFactory;
    }

    @Override
    public CertificatDTO create(CertificatDTO dto) throws ValidationException {
        // 1. Validation
        Map<String, String> errors = validator.validate(dto);
        if (!errors.isEmpty()) {
            throw new ValidationException("Erreur de validation certificat");
        }

        return Transaction.initTransaction(cnx -> {
            CertificatRepository repo = repoFactory.create(cnx);
            
            // Mapping DTO -> Entity
            Certificat cert = Certificat.builder()
                .dateDebut(LocalDate.parse(dto.getDateDebut()))
                .dureeRepos(dto.getDureeRepos())
                .contenu(dto.getContenu())
                // .consultation(...) // Charger la consultation si nécessaire via liaison ID
                .build();
            
            // Calcul date fin
            cert.setDateFin(cert.getDateDebut().plusDays(cert.getDureeRepos()));

            // Persistance
            repo.create(cert);
            
            // Mapping retour
            dto.setId(cert.getId());
            dto.setDateFin(cert.getDateFin().toString());
            return dto;
        });
    }

    @Override
    public CertificatDTO findById(Long id) {
         return Transaction.initTransaction(cnx -> {
            CertificatRepository repo = repoFactory.create(cnx);
            Certificat cert = repo.findById(id);
            if (cert == null) return null;
            
            return CertificatDTO.builder()
                .id(cert.getId())
                .dateDebut(cert.getDateDebut().toString())
                .dateFin(cert.getDateFin().toString())
                .dureeRepos(cert.getDureeRepos())
                .contenu(cert.getContenu())
                .build();
        });
    }

    @Override
    public ByteArrayInputStream generatePdf(Long certificatId) {
        CertificatDTO cert = findById(certificatId);
        if (cert == null) throw new RuntimeException("Certificat non trouvé");
        
        // Enrichir avec noms si possible (via Services Consultation/Patient)
        // Pour l'instant, données brutes
        return printService.generatePdf(cert);
    }
}
