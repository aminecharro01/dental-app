package ma.WhiteLab.mvc.ui.pages.otherPages;

import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.common.BasePlaceholderPanel;

public class ParametragePanel extends BasePlaceholderPanel {
    public ParametragePanel(UserPrincipal principal) {
        super("Paramétrage Cabinet", principal);
    }
}
