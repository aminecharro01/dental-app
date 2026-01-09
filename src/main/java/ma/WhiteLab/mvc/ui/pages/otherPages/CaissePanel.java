package ma.WhiteLab.mvc.ui.pages.otherPages;

import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.common.BasePlaceholderPanel;

public class CaissePanel extends BasePlaceholderPanel {
    public CaissePanel(UserPrincipal principal) {
        super("Module Caisse", principal);
    }
}
