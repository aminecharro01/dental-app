package ma.WhiteLab.service.modules.certificat.api;

import ma.WhiteLab.service.modules.certificat.dto.CertificatDTO;
import ma.WhiteLab.common.exceptions.ValidationException;
import java.io.ByteArrayInputStream;

public interface CertificatService {
    CertificatDTO create(CertificatDTO dto) throws ValidationException;
    CertificatDTO findById(Long id);
    ByteArrayInputStream generatePdf(Long certificatId); // Pour impression
}
