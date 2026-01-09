package ma.WhiteLab.mvc.ui.pages.otherPages;

import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.common.BasePlaceholderPanel;

public class NotificationsPanel extends BasePlaceholderPanel {
    public NotificationsPanel(UserPrincipal principal) {
        super("Notifications", principal);
    }
}
