package ma.WhiteLab.mvc.controllers.otherModules.impl;

import javax.swing.JPanel;
import lombok.RequiredArgsConstructor;
import ma.WhiteLab.mvc.controllers.otherModules.api.CabinetsController;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.CabinetsPanel;

@RequiredArgsConstructor
public class CabinetsControllerImpl implements CabinetsController {

    private JPanel cached;

    @Override
    public JPanel getView(UserPrincipal principal) {
        if (cached == null) cached = new CabinetsPanel(principal);
        return cached;
    }
}
