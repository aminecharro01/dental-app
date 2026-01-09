package ma.WhiteLab.mvc.controllers.otherModules.impl;

import javax.swing.JPanel;
import lombok.RequiredArgsConstructor;
import ma.WhiteLab.mvc.controllers.otherModules.api.CaisseController;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.CaissePanel;

@RequiredArgsConstructor
public class CaisseControllerImpl implements CaisseController {

    private JPanel cached;

    @Override
    public JPanel getView(UserPrincipal principal) {
        if (cached == null) cached = new CaissePanel(principal);
        return cached;
    }
}
