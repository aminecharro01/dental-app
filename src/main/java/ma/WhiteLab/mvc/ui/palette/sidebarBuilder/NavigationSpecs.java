package ma.WhiteLab.mvc.ui.palette.sidebarBuilder;

import ma.WhiteLab.entities.enums.RoleR;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.pagesNames.ApplicationPages;

import java.util.ArrayList;
import java.util.List;

import static ma.WhiteLab.security.Privileges.*;

public final class NavigationSpecs {

    private NavigationSpecs(){}

    public static List<NavSpec> forPrincipal(UserPrincipal principal) {

        boolean isAdmin   = principal != null && principal.roles() != null && principal.roles().contains(RoleR.ADMIN);
        boolean isMedecin = principal != null && principal.roles() != null && principal.roles().contains(RoleR.MEDECIN);
        boolean isSecretaire = principal != null && principal.roles() != null && principal.roles().contains(RoleR.SECRETAIRE);

        List<NavSpec> items = new ArrayList<>();

        // -------- Général (commun)
        items.add(item("Général", "Dashboard", "/static/icons/home.png", ApplicationPages.DASHBOARD, null));
        items.add(item("Général", "Profil", "/static/icons/profile.png", ApplicationPages.PROFILE, null));
        items.add(item("Général", "Notifications", "/static/icons/bell.png", ApplicationPages.NOTIFICATIONS, null));

        // -------- Admin
        if (isAdmin) {
            items.add(item("Administration", "Cabinets", "/static/icons/cabinet.png", ApplicationPages.CABINETS, CABINET_ACCESS));
            items.add(item("Administration", "Utilisateurs", "/static/icons/users.png", ApplicationPages.USERS, USERS_ACCESS));
            items.add(item("Système", "Paramètres", "/static/icons/param.png", ApplicationPages.PARAMETRAGE, CABINET_ACCESS));
        }

        // -------- Médecin
        if (isMedecin) {
            items.add(item("Cabinet", "Patients", "/static/icons/patient.png", ApplicationPages.PATIENTS, "GESTION_PATIENTS"));
            items.add(item("Cabinet", "Dossiers Médicaux", "/static/icons/folder.png", ApplicationPages.DOSSIERS_MEDICAUX, "GERER_DOSSIERS"));
            items.add(item("Système", "Configuration", "/static/icons/settings.png", ApplicationPages.PARAMETRAGE, null));
        }
        if (isSecretaire) {
            items.add(item("Accueil", "Patients", "/static/icons/patient.png", ApplicationPages.PATIENTS, "GESTION_PATIENTS"));
            items.add(item("Métier", "Caisse & Facturation", "/static/icons/caisse.png", ApplicationPages.CAISSE, "GESTION_CAISSE"));
            items.add(item("Système", "Paramètres", "/static/icons/settings.png", ApplicationPages.PARAMETRAGE, null));
        }

        return items;
    }

    private static NavSpec item(String section, String label, String iconPath,
                                ApplicationPages page, String privilegeOrNull) {
        return new NavSpec(section, label, iconPath, page.name(), privilegeOrNull);
    }
}
