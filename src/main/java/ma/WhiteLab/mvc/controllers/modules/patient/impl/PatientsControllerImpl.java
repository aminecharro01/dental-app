package ma.WhiteLab.mvc.controllers.modules.patient.impl;

import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.mvc.controllers.modules.patient.api.PatientsController;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.PatientsPanel;
import ma.WhiteLab.service.modules.patient.api.PatientService;

import javax.swing.*;
import java.util.List;

public class PatientsControllerImpl implements PatientsController {

    private final PatientService patientService;

    private PatientsPanel cachedPanel;
    private List<Patient> patients;

    // ✅ Injection propre du service (IoC)
    public PatientsControllerImpl(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    public JPanel getView(UserPrincipal principal) {
        if (cachedPanel == null) {
            patients = patientService.findAll();

            cachedPanel = new PatientsPanel(
                    this,
                    patientService,
                    patients,
                    principal
            );
            cachedPanel.setName("PatientsPanel");
        }

        return cachedPanel;
    }
}
