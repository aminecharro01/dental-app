package ma.WhiteLab.mvc.controllers.modules.dossierMedicale.api;

import javax.swing.*;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;

public interface DossiersController {
    JPanel getView(UserPrincipal principal);
    JPanel getDossierDetailView(Long dossierId, UserPrincipal principal);
}
