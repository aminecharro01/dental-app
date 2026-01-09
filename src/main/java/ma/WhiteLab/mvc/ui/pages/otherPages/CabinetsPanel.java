package ma.WhiteLab.mvc.ui.pages.otherPages;

import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.common.BasePlaceholderPanel;

public class CabinetsPanel extends BasePlaceholderPanel {
    public CabinetsPanel(UserPrincipal principal) {
        super("Backoffice — Cabinets", principal);
    }
}
