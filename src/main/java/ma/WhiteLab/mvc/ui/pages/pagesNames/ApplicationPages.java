package ma.WhiteLab.mvc.ui.pages.pagesNames;

public enum ApplicationPages {
    DASHBOARD,

    // Général
    PROFILE,
    NOTIFICATIONS,

    // Métier
    PATIENTS,
    CAISSE,
    CABINET, // page cabinet courant (infos du cabinet)

    // Backoffice
    USERS,
    CABINETS,

    // Cabinet
    DOSSIERS_MEDICAUX,
    PARAMETRAGE;

    // 🔥 SAFE resolver (DO NOT REMOVE)
    public static ApplicationPages from(String raw) {
        if (raw == null) return null;

        String normalized = raw
                .trim()
                .toUpperCase()
                .replaceAll("[^A-Z_]", ""); // removes invisible unicode chars

        for (ApplicationPages p : values()) {
            if (p.name().equals(normalized)) {
                return p;
            }
        }

        throw new IllegalArgumentException(
                "Page inconnue dans ApplicationPages : '" + raw + "'"
        );
    }
}
