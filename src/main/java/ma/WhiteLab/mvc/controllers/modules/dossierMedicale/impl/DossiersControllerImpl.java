package ma.WhiteLab.mvc.controllers.modules.dossierMedicale.impl;

import ma.WhiteLab.entities.dossierMedical.DossierMedical;
import ma.WhiteLab.entities.enums.RoleR;
import ma.WhiteLab.mvc.controllers.modules.dossierMedicale.api.DossiersController;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.mvc.ui.pages.otherPages.DossierDetailPanel;
import ma.WhiteLab.mvc.ui.pages.otherPages.DossiersPanel;
import ma.WhiteLab.service.modules.dossierMedicale.api.DossierMedicalService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DossiersControllerImpl implements DossiersController {

    private final DossierMedicalService dossierMedicalService;

    public DossiersControllerImpl(DossierMedicalService dossierMedicalService) {
        this.dossierMedicalService = dossierMedicalService;
    }

    @Override
    public JPanel getView(UserPrincipal principal) {

        if (principal == null) {
            return createErrorPanel("Session invalide : utilisateur non identifié.");
        }

        try {
            List<DossierMedical> dossiers;

            // === LOGIQUE MÉTIER DÉLÉGUÉE AU SERVICE ===
            if (principal.roles().contains(RoleR.MEDECIN)) {
                dossiers = dossierMedicalService.getDossiersByMedecinId(principal.id());
            } else {
                dossiers = dossierMedicalService.getAllDossiers();
            }

            if (dossiers == null || dossiers.isEmpty()) {
                return createEmptyPanel("Aucun dossier médical trouvé.");
            }

            return new DossiersPanel(this, dossierMedicalService, dossiers, principal);

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Erreur lors du chargement des dossiers : " + e.getMessage());
        }
    }

    @Override
    public JPanel getDossierDetailView(Long dossierId, UserPrincipal principal) {

        if (principal == null) {
            return createErrorPanel("Session invalide : utilisateur non identifié.");
        }

        try {
            DossierMedical dossier = dossierMedicalService.getDossierById(dossierId)
                    .orElseThrow(() -> new IllegalArgumentException("Dossier médical introuvable."));

            // === CONTRÔLE D’ACCÈS ===
            boolean isAdmin = principal.roles().contains(RoleR.ADMIN);
            boolean isMedecinOwner =
                    dossier.getMedecine() != null &&
                            dossier.getMedecine().getId().equals(principal.id());

            if (!isAdmin && !isMedecinOwner) {
                return createErrorPanel("Accès refusé à ce dossier médical.");
            }

            return new DossierDetailPanel(dossier, dossierMedicalService, principal);

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Erreur lors du chargement du dossier : " + e.getMessage());
        }
    }

    // ===================== PANELS UTILITAIRES =====================

    private JPanel createErrorPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel(
                "<html><div style='text-align:center; color:red; font-size:18px;'>" +
                        message +
                        "</div></html>"
        );
        label.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEmptyPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel(
                "<html><div style='text-align:center; color:gray; font-size:18px;'>" +
                        message +
                        "</div></html>"
        );
        label.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
