package ma.WhiteLab.repository.modules.agenda.api;

import ma.WhiteLab.entities.agenda.RendezVous;
import ma.WhiteLab.repository.common.CrudRepository;

import java.util.List;

public interface RendezVousRepository extends CrudRepository<RendezVous, Long> {

    // Recherche par dossier médical
    List<RendezVous> findByDossierMedId(Long dossierMedId);

    // Recherche par consultation
    List<RendezVous> findByConsultationId(Long consultationId);

    // Recherche par status
    List<RendezVous> findByStatus(String status);
}
