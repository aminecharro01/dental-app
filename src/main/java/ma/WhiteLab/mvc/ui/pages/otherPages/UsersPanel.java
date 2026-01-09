package ma.WhiteLab.mvc.ui.pages.otherPages;

import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.common.BasePlaceholderPanel;

public class UsersPanel extends BasePlaceholderPanel {
    public UsersPanel(UserPrincipal principal) {
        super("Backoffice — Users", principal);
    }
}
