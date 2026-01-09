package ma.WhiteLab.service.modules.certificat.test;

import junit.framework.TestCase;
import ma.WhiteLab.service.modules.certificat.dto.CertificatDTO;
import ma.WhiteLab.service.modules.certificat.impl.CertificatValidator;

public class CertificatServiceImplTest extends TestCase {
    public void testValidate() {
        CertificatValidator val = new CertificatValidator();
        CertificatDTO dto = new CertificatDTO();
        assertFalse(val.validate(dto).isEmpty());
    }
}
