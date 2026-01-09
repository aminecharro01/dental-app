package ma.WhiteLab.mvc.ui.pages.dashboardPages;


import ma.WhiteLab.mvc.dto.auth.UserPrincipal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SecretaryDashboardPanel extends JPanel {

    public SecretaryDashboardPanel(UserPrincipal principal) {
        setLayout(new BorderLayout(16, 16));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel title = new JLabel("Dashboard — Secrétaire");
        title.setFont(new Font("Optima", Font.BOLD, 28));

        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 14));
        cards.setOpaque(false);

        cards.add(card("Agenda", "Planifier / modifier RDV"));
        cards.add(card("Caisse", "Paiements & factures"));
        cards.add(card("Dossiers", "Création / mise à jour"));

        add(title, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
    }

    private JComponent card(String head, String sub) {
        JPanel p = new JPanel(new GridLayout(0, 1, 0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel h = new JLabel(head);
        h.setFont(new Font("Optima", Font.BOLD, 18));

        JLabel s = new JLabel(sub);
        s.setFont(new Font("Optima", Font.PLAIN, 14));
        s.setForeground(new Color(90, 90, 90));

        p.add(h);
        p.add(s);
        return p;
    }
}
