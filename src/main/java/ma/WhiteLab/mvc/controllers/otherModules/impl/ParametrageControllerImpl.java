package ma.WhiteLab.mvc.controllers.otherModules.impl;

import javax.swing.JPanel;
import lombok.RequiredArgsConstructor;
import ma.WhiteLab.mvc.controllers.otherModules.api.ParametrageController;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.ParametragePanel;

@RequiredArgsConstructor
public class ParametrageControllerImpl implements ParametrageController {

    private JPanel cached;

    @Override
    public JPanel getView(UserPrincipal principal) {
        if (cached == null) cached = new ParametragePanel(principal);
        return cached;
    }
}
