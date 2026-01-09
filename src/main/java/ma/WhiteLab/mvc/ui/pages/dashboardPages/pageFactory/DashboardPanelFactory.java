package ma.WhiteLab.mvc.ui.pages.dashboardPages.pageFactory;

import ma.WhiteLab.entities.enums.RoleR;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;

import javax.swing.*;
import ma.WhiteLab.mvc.ui.pages.dashboardPages.AdminDashboardPanel;
import ma.WhiteLab.mvc.ui.pages.dashboardPages.DefaultDashboardPanel;
import ma.WhiteLab.mvc.ui.pages.dashboardPages.DoctorDashboardPanel;
import ma.WhiteLab.mvc.ui.pages.dashboardPages.SecretaryDashboardPanel;

public final class DashboardPanelFactory {

    private DashboardPanelFactory(){}

    public static JComponent create(UserPrincipal principal) {
        RoleR role = (principal != null) ? principal.rolePrincipal() : null;

        if (role == null) {
            return new DefaultDashboardPanel(principal);
        }

        return switch (role) {
            case ADMIN      -> new AdminDashboardPanel(principal);
            case MEDECIN    -> new DoctorDashboardPanel(principal);
            case SECRETAIRE -> new SecretaryDashboardPanel(principal);
            default         -> new DefaultDashboardPanel(principal);
        };
    }
}
