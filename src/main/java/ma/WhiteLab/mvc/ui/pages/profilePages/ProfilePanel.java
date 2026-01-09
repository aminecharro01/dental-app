package ma.WhiteLab.mvc.ui.pages.profilePages;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.mvc.dto.profileDtos.*;
import ma.WhiteLab.mvc.ui.palette.alert.Alert;
import ma.WhiteLab.mvc.ui.palette.combos.CustomEnumComboBox;
import ma.WhiteLab.mvc.ui.palette.fields.CustomPasswordField;
import ma.WhiteLab.mvc.ui.palette.fields.CustomTextField;
import ma.WhiteLab.service.modules.profileService.api.ProfileService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static ma.WhiteLab.mvc.ui.palette.utils.ImageTools.loadIcon;

public class ProfilePanel extends JPanel {

    private final ProfileService service;
    private ProfileData data;

    // Avatar UI
    private AvatarView avatarView;
    private JButton btEditAvatar;

    // Champs
    private CustomTextField tfPrenom, tfNom, tfEmail, tfTel, tfAdresse, tfCin;
    private JTextField tfAvatar;
    private CustomEnumComboBox<Sexe> cbSexe;

    private CustomTextField tfSpecialite, tfNumCNSS, tfCommission;

    // Erreurs
    private JLabel errPrenom, errNom, errEmail, errGlobal;

    // Header
    private JLabel lblName;
    private JLabel lblEmail;

    private final Consumer<ProfileData> onProfileSaved;

    public ProfilePanel(Object controllerIgnored, ProfileService service, ProfileData data, Consumer<ProfileData> onProfileSaved) {
        this.service = service;
        this.data = data;
        this.onProfileSaved = onProfileSaved;

        setLayout(new BorderLayout(16, 16));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(22, 22, 22, 22));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        fillFromData();
    }

    // ======================== HEADER ========================

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);

        lblName = new JLabel("—");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 28));

        lblEmail = new JLabel("—");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setForeground(new Color(90, 90, 90));

        JPanel left = new JPanel(new GridLayout(0, 1, 0, 6));
        left.setOpaque(false);
        left.add(lblName);
        left.add(lblEmail);

        JPanel avatarWrapper = new JPanel();
        avatarWrapper.setOpaque(false);
        avatarWrapper.setLayout(new OverlayLayout(avatarWrapper));
        avatarWrapper.setPreferredSize(new Dimension(92, 92));

        avatarView = new AvatarView(92);
        avatarView.setAlignmentX(0.5f);
        avatarView.setAlignmentY(0.5f);

        btEditAvatar = createAvatarEditButton();
        btEditAvatar.setAlignmentX(1f);
        btEditAvatar.setAlignmentY(1f);
        btEditAvatar.setVisible(false);

        avatarWrapper.add(btEditAvatar);
        avatarWrapper.add(avatarView);

        installAvatarHover(avatarWrapper, btEditAvatar);

        header.add(left, BorderLayout.WEST);
        header.add(avatarWrapper, BorderLayout.EAST);
        return header;
    }

    // ======================== BODY ========================

    private JComponent buildBody() {
        JPanel wrapper = new JPanel(new BorderLayout(16, 0));
        wrapper.setOpaque(false);
        wrapper.add(buildForm(), BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        tfPrenom = ctf("Prénom");
        tfNom = ctf("Nom");
        tfEmail = ctf("Email");
        tfTel = ctf("Téléphone");
        tfAdresse = ctf("Adresse");
        tfCin = ctf("CIN");

        tfAvatar = new JTextField();
        tfAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tfAvatar.setPreferredSize(new Dimension(320, 38));
        tfAvatar.setEditable(false);
        tfAvatar.setBackground(new Color(245, 245, 245));

        cbSexe = new CustomEnumComboBox<>(Sexe.class, "— Sexe —");
        cbSexe.setPreferredFieldSize(320, 38);
        cbSexe.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        errPrenom = err();
        errNom = err();
        errEmail = err();
        errGlobal = err();
        errGlobal.setHorizontalAlignment(SwingConstants.CENTER);

        int row = 0;
        row = addRow(form, gc, row, "Prénom", tfPrenom, errPrenom);
        row = addRow(form, gc, row, "Nom", tfNom, errNom);
        row = addRow(form, gc, row, "Email", tfEmail, errEmail);
        row = addRow(form, gc, row, "Téléphone", tfTel, null);
        row = addRow(form, gc, row, "Adresse", tfAdresse, null);
        row = addRow(form, gc, row, "CIN", tfCin, null);
        row = addRow(form, gc, row, "Sexe", cbSexe, null);

        if (data != null && data.rolePrincipal() != null) {
            switch (data.rolePrincipal()) {
                case MEDECIN -> {
                    tfSpecialite = ctf("Spécialité");
                    row = addRow(form, gc, row, "Spécialité", tfSpecialite, null);
                }
                case SECRETAIRE -> {
                    tfNumCNSS = ctf("Num CNSS");
                    tfCommission = ctf("Commission");
                    row = addRow(form, gc, row, "Num CNSS", tfNumCNSS, null);
                    row = addRow(form, gc, row, "Commission", tfCommission, null);
                }
                default -> {}
            }
        }

        gc.gridy = row;
        gc.gridx = 0;
        gc.gridwidth = 2;
        form.add(errGlobal, gc);

        return form;
    }

    // ======================== FOOTER ========================

    private JComponent buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        p.setOpaque(false);

        JButton btSave = new JButton("Enregistrer");
        btSave.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btSave.addActionListener(e -> onSave());

        JButton btPwd = new JButton("Changer mot de passe");
        btPwd.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btPwd.addActionListener(e -> openChangePasswordDialog());

        p.add(btSave);
        p.add(btPwd);
        return p;
    }

    // ======================== ACTIONS ========================

    private void onSave() {
        clearErrors();

        if (data == null || data.id() == null) {
            Alert.error(this, "Impossible de sauvegarder : profil non chargé.");
            return;
        }

        ProfileUpdateRequest req = ProfileUpdateRequest.builder()
                .id(data.id())
                .prenom(readValue(tfPrenom))
                .nom(readValue(tfNom))
                .email(readValue(tfEmail))
                .tel(readValue(tfTel))
                .adresse(readValue(tfAdresse))
                .cin(readValue(tfCin))
                .avatar(tfAvatar.getText())
                .sexe(cbSexe.getSelectedEnum())
                .dateNaissance(data.dateNaissance())

                .salaire(data.salaire())
                .prime(data.prime())
                .dateRecrutement(data.dateRecrutement())
                .soldeConge(data.soldeConge())

                .specialite(tfSpecialite != null ? readValue(tfSpecialite) : null)
                .numCNSS(tfNumCNSS != null ? readValue(tfNumCNSS) : null)
                .commission(tfCommission != null ? parseDouble(readValue(tfCommission)) : null)
                .build();

        ProfileUpdateResult res = service.update(req);

        if (!res.ok()) {
            showFieldErrors(res.fieldErrors(), res.message());
            return;
        }

        this.data = res.data();
        fillFromData();
        if (onProfileSaved != null) onProfileSaved.accept(this.data);
        Alert.success(this, "Profil enregistré avec succès.");
    }

    private void onChooseAvatar() {
        if (data == null || data.id() == null) {
            Alert.error(this, "Profil non chargé.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Choisir une image d’avatar");
        fc.setFileFilter(new FileNameExtensionFilter("Images (png, jpg, jpeg, webp)", "png", "jpg", "jpeg", "webp"));
        fc.setAcceptAllFileFilterUsed(false);

        int r = fc.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return;

        File selected = fc.getSelectedFile();
        if (selected == null || !selected.exists()) {
            Alert.error(this, "Fichier invalide.");
            return;
        }

        try {
            BufferedImage img = ImageIO.read(selected);
            if (img == null) {
                Alert.error(this, "Fichier non reconnu comme image.");
                return;
            }

            Path dir = resolveAvatarDir();
            Files.createDirectories(dir);

            String ext = fileExt(selected.getName());
            if (ext == null) ext = "png";

            String fileName = "u" + data.id() + "_" + UUID.randomUUID() + "." + ext;
            Path dest = dir.resolve(fileName);

            Files.copy(selected.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            String storedPath = "avatars/" + fileName;
            tfAvatar.setText(storedPath);

            avatarView.setImage(img);
            avatarView.revalidate();
            avatarView.repaint();

        } catch (Exception ex) {
            Alert.error(this, "Erreur upload avatar : " + ex.getMessage());
        }
    }

    private void openChangePasswordDialog() {
        if (data == null || data.id() == null) {
            Alert.error(this, "Profil non chargé.");
            return;
        }

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Changer mot de passe", Dialog.ModalityType.APPLICATION_MODAL);
        d.setUndecorated(true);
        d.setLayout(new BorderLayout());
        d.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        CustomPasswordField pfCur = new CustomPasswordField("Mot de passe actuel");
        CustomPasswordField pfNew = new CustomPasswordField("Nouveau mot de passe");
        CustomPasswordField pfCnf = new CustomPasswordField("Confirmer");

        configurePwdField(pfCur);
        configurePwdField(pfNew);
        configurePwdField(pfCnf);

        JLabel eCur = err();
        JLabel eNew = err();
        JLabel eCnf = err();
        JLabel eG = err();
        eG.setHorizontalAlignment(SwingConstants.CENTER);

        int row = 0;
        row = addRow(content, gc, row, "Actuel", pfCur, eCur);
        row = addRow(content, gc, row, "Nouveau", pfNew, eNew);
        row = addRow(content, gc, row, "Confirmer", pfCnf, eCnf);

        gc.gridy = row;
        gc.gridx = 0;
        gc.gridwidth = 2;
        content.add(eG, gc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        footer.setBackground(Color.WHITE);

        JButton btOk = new JButton("Valider");
        btOk.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton btCancel = new JButton("Annuler");
        btCancel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btCancel.addActionListener(ev -> d.dispose());

        btOk.addActionListener(ev -> {
            eCur.setText(" ");
            eNew.setText(" ");
            eCnf.setText(" ");
            eG.setText(" ");

            String cur = new String(pfCur.getPassword());
            String nw = new String(pfNew.getPassword());
            String cf = new String(pfCnf.getPassword());

            ChangePasswordRequest req = ChangePasswordRequest.builder()
                    .userId(data.id())
                    .currentPassword(cur)
                    .newPassword(nw)
                    .confirmPassword(cf)
                    .build();

            ChangePasswordResult res = service.changePassword(req);
            if (!res.ok()) {
                Map<String, String> m = res.fieldErrors();
                if (m != null) {
                    eCur.setText(m.getOrDefault("currentPassword", " "));
                    eNew.setText(m.getOrDefault("newPassword", " "));
                    eCnf.setText(m.getOrDefault("confirmPassword", " "));
                    eG.setText(m.getOrDefault("_global", res.message() != null ? res.message() : " "));
                } else {
                    eG.setText(res.message() != null ? res.message() : "Erreur.");
                }
                return;
            }

            Alert.success(this, "Mot de passe modifié.");
            d.dispose();
        });

        footer.add(btOk);
        footer.add(btCancel);

        InputMap im = d.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = d.getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "OK");
        am.put("OK", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { btOk.doClick(); }
        });
        im.put(KeyStroke.getKeyStroke("ESCAPE"), "CLOSE");
        am.put("CLOSE", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { d.dispose(); }
        });
        d.getRootPane().setDefaultButton(btOk);

        d.add(content, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(this);
        SwingUtilities.invokeLater(pfCur::requestFocusInWindow);
        d.setVisible(true);
    }

    // ======================== DATA → UI ========================

    private void fillFromData() {
        if (data == null) return;

        setValue(tfPrenom, data.prenom());
        setValue(tfNom, data.nom());
        setValue(tfEmail, data.email());
        setValue(tfTel, data.tel());
        setValue(tfAdresse, data.adresse());
        setValue(tfCin, data.cin());

        tfAvatar.setText(safe(data.avatar()));

        cbSexe.setSelectedEnum(data.sexe());

        if (tfSpecialite != null) setValue(tfSpecialite, data.specialite());
        if (tfNumCNSS != null) setValue(tfNumCNSS, data.numCNSS());
        if (tfCommission != null) setValue(tfCommission,
                data.commission() != null ? String.valueOf(data.commission()) : "");

        String fullName = (safe(data.prenom()) + " " + safe(data.nom())).trim();
        lblName.setText(fullName.isBlank() ? "Mon Profil" : fullName);
        lblEmail.setText(safe(data.email()));

        BufferedImage img = loadImageFromStoredPath(data.avatar());
        avatarView.setImage(img);
    }

    private void setValue(CustomTextField tf, String value) {
        if (tf == null) return;

        String v = value == null ? "" : value.trim();

        if (v.isBlank()) {
            tf.setText(tf.getHint());
            tf.setFont(tf.getLostFont());
            tf.setForeground(tf.getLostColor());
        } else {
            tf.setText(v);
            tf.setFont(tf.getGainFont());
            tf.setForeground(tf.getGainColor());
        }

        tf.repaint();
    }

    // ======================== ERRORS ========================

    private void showFieldErrors(Map<String, String> errors, String globalMsg) {
        if (errors == null) return;

        errPrenom.setText(errors.getOrDefault("prenom", " "));
        errNom.setText(errors.getOrDefault("nom", " "));
        errEmail.setText(errors.getOrDefault("email", " "));
        errGlobal.setText(errors.getOrDefault("_global", globalMsg != null ? globalMsg : " "));
    }

    private void clearErrors() {
        errPrenom.setText(" ");
        errNom.setText(" ");
        errEmail.setText(" ");
        errGlobal.setText(" ");
    }

    // ======================== HELPERS (UI) ========================

    private CustomTextField ctf(String hint) {
        CustomTextField t = new CustomTextField(hint);
        t.setPreferredSize(new Dimension(320, 42));
        return t;
    }

    private JLabel err() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(222, 112, 112));
        return l;
    }

    private int addRow(JPanel form, GridBagConstraints gc, int row, String label, JComponent field, JLabel err) {
        gc.gridy = row;
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.weightx = 0;

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        form.add(l, gc);

        gc.gridx = 1;
        gc.weightx = 1;
        form.add(field, gc);

        if (err != null) {
            gc.gridy = row + 1;
            gc.gridx = 1;
            gc.weightx = 1;
            form.add(err, gc);
            return row + 2;
        }
        return row + 1;
    }

    private void configurePwdField(CustomPasswordField pf) {
        pf.setPreferredSize(new Dimension(320, 42));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    }

    // ======================== HELPERS (Avatar) ========================

    private JButton createAvatarEditButton() {
        ImageIcon icon = loadIcon("/static/icons/profile.png", 22, 22);
        JButton b = new JButton(icon != null ? icon : new ImageIcon());
        b.setToolTipText("Modifier l’avatar");
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> onChooseAvatar());
        b.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 2));
        return b;
    }

    private void installAvatarHover(JComponent target, JComponent button) {
        var ml = new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setVisible(true); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(p, target);
                if (!target.contains(p)) button.setVisible(false);
            }
        };
        target.addMouseListener(ml);
        button.addMouseListener(ml);
    }

    private Path resolveAvatarDir() {
        String dir = ApplicationContext.getInstance().getProperty("profile.avatars.dir", "avatars");
        return Paths.get(dir);
    }

    private BufferedImage loadImageFromStoredPath(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return null;

        try {
            Path p = Paths.get(storedPath);
            if (p.isAbsolute() && Files.exists(p)) {
                return ImageIO.read(p.toFile());
            }

            String fn = storedPath.replace("\\", "/");
            if (fn.contains("/")) fn = fn.substring(fn.lastIndexOf('/') + 1);

            Path file = resolveAvatarDir().resolve(fn);
            if (!Files.exists(file)) return null;

            return ImageIO.read(file.toFile());
        } catch (Exception e) {
            return null;
        }
    }

    private String fileExt(String name) {
        if (name == null) return null;
        int i = name.lastIndexOf('.');
        if (i < 0 || i == name.length() - 1) return null;
        return name.substring(i + 1).toLowerCase();
    }

    // ======================== HELPERS (String/Number) ========================

    private String safe(String s) { return s == null ? "" : s; }

    private Double parseDouble(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ======================== HELPER pour onSave ========================

    private String readValue(CustomTextField tf) {
        if (tf == null) return null;

        String text = tf.getText();
        if (text == null) return "";

        String trimmed = text.trim();

        if (trimmed.equals(tf.getHint())) {
            return "";
        }

        return trimmed.isEmpty() ? "" : trimmed;
    }

    // ======================== INNER CLASS : AvatarView ========================

    private static class AvatarView extends JComponent {
        private final int size;
        private BufferedImage image;

        AvatarView(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
        }

        void setImage(BufferedImage img) {
            this.image = img;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(245, 245, 245));
            g2.fillOval(0, 0, size, size);

            if (image != null) {
                BufferedImage scaled = scaleToFill(image, size, size);
                Shape clip = new Ellipse2D.Double(0, 0, size, size);
                g2.setClip(clip);
                g2.drawImage(scaled, 0, 0, null);
                g2.setClip(null);
            } else {
                g2.setColor(new Color(130, 130, 130));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 34));
                FontMetrics fm = g2.getFontMetrics();
                String s = "👤";
                int x = (size - fm.stringWidth(s)) / 2;
                int y = (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(s, x, y);
            }

            g2.setColor(new Color(220, 220, 220));
            g2.drawOval(0, 0, size - 1, size - 1);

            g2.dispose();
        }

        private static BufferedImage scaleToFill(BufferedImage src, int w, int h) {
            int sw = src.getWidth();
            int sh = src.getHeight();
            if (sw <= 0 || sh <= 0) return src;

            double rSrc = (double) sw / sh;
            double rDst = (double) w / h;

            int cw, ch, cx, cy;
            if (rSrc > rDst) {
                ch = sh;
                cw = (int) (sh * rDst);
                cx = (sw - cw) / 2;
                cy = 0;
            } else {
                cw = sw;
                ch = (int) (sw / rDst);
                cx = 0;
                cy = (sh - ch) / 2;
            }

            BufferedImage cropped = src.getSubimage(Math.max(0, cx), Math.max(0, cy),
                    Math.min(cw, sw), Math.min(ch, sh));

            Image scaled = cropped.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = out.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(scaled, 0, 0, null);
            g2.dispose();
            return out;
        }
    }
}