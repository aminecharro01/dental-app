package ma.WhiteLab.service.modules.consultation.test;

import junit.framework.TestCase;
import ma.WhiteLab.service.modules.consultation.dto.ConsultationDTO;
import ma.WhiteLab.service.modules.consultation.impl.ConsultationValidator;

public class ConsultationServiceImplTest extends TestCase {
    public void testValidate() {
        ConsultationValidator val = new ConsultationValidator();
        ConsultationDTO dto = new ConsultationDTO();
        assertFalse(val.validate(dto).isEmpty());
    }
}
