package ma.WhiteLab.mvc.controllers.otherModules.impl;

import javax.swing.JPanel;
import lombok.RequiredArgsConstructor;
import ma.WhiteLab.mvc.controllers.otherModules.api.UsersController;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.UsersPanel;

@RequiredArgsConstructor
public class UsersControllerImpl implements UsersController {

    private JPanel cached;

    @Override
    public JPanel getView(UserPrincipal principal) {
        if (cached == null) cached = new UsersPanel(principal);
        return cached;
    }
}
