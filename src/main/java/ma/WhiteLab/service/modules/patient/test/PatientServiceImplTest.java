package ma.WhiteLab.service.modules.patient.test;

import junit.framework.TestCase;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.service.modules.patient.dto.PatientDTO;
import ma.WhiteLab.service.modules.patient.impl.PatientValidator;
import ma.WhiteLab.service.modules.patient.impl.PromoPatientServiceImpl;

public class PatientServiceImplTest extends TestCase {

    public void testValidatePatientDTO_Success() {
        PatientValidator validator = new PatientValidator();
        PatientDTO dto = new PatientDTO();
        dto.setNom("Doe");
        dto.setPrenom("John");
        dto.setTelephone("0600000000");
        dto.setEmail("john.doe@example.com");
        dto.setSexe("MALE");
        dto.setDateNaissance("1990-01-01");

        assertTrue(validator.validate(dto).isEmpty());
    }

    public void testValidatePatientDTO_Failure_MissingName() {
        PatientValidator validator = new PatientValidator();
        PatientDTO dto = new PatientDTO();
        dto.setPrenom("John");
        
        // Nom missing
        assertFalse(validator.validate(dto).isEmpty());
        assertTrue(validator.validate(dto).containsKey("nom"));
    }
    
    public void testPromoPatientService_Senior() {
        PromoPatientServiceImpl promoService = new PromoPatientServiceImpl();
        Patient p = new Patient();
        p.setDateNaissance(java.time.LocalDate.now().minusYears(65));
        
        assertTrue(promoService.isSeniorDiscountApplicable(p));
        assertNotNull(promoService.checkPromoEligibility(p));
    }
}
