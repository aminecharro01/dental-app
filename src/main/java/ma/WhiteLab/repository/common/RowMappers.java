package ma.WhiteLab.repository.common;

import ma.WhiteLab.entities.base.BaseEntity;
import ma.WhiteLab.entities.cabinet.CabinetMedicale;
import ma.WhiteLab.entities.cabinet.Statistiques;
import ma.WhiteLab.entities.dossierMedical.*;
import ma.WhiteLab.entities.cabinet.Charges;
import ma.WhiteLab.entities.cabinet.Revenus;
import ma.WhiteLab.entities.dossierMedical.SituationFinanciere;
import ma.WhiteLab.entities.patient.*;
import ma.WhiteLab.entities.enums.*;
import ma.WhiteLab.entities.user.Admin;
import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.entities.user.Secretaire;
import ma.WhiteLab.entities.user.Staff;
import ma.WhiteLab.entities.agenda.AgendaMensuel;
import ma.WhiteLab.entities.agenda.RendezVous;
import ma.WhiteLab.entities.user.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class RowMappers {

    private RowMappers(){}

    // ============================================================
    //  MÉTHODE GLOBALE POUR MAPPER BaseEntity
    // ============================================================
    public static <T extends BaseEntity> T mapBaseEntity(ResultSet rs, T entity) throws SQLException {
        entity.setId(rs.getLong("id"));

        // CORRIGÉ : noms de colonnes en snake_case (comme dans MySQL)
        Timestamp tsCreation = rs.getTimestamp("dateCreation");
        entity.setDateCreation(tsCreation != null ? tsCreation.toLocalDateTime() : null);

        Timestamp tsMaj = rs.getTimestamp("dateMiseAJour");
        entity.setDateMiseAJour(tsMaj != null ? tsMaj.toLocalDateTime() : null);

        entity.setCreePar(getStringOrNull(rs, "creePar"));
        entity.setModifierPar(getStringOrNull(rs, "modifierPar"));

        return entity;
    }

    // Méthode utilitaire pour éviter les NPE et vérifier NULL pour String
    private static String getStringOrNull(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return rs.wasNull() || value == null ? null : value;
    }

    // Méthode utilitaire pour mapper les ENUMS de manière robuste
    private static <E extends Enum<E>> E mapEnum(ResultSet rs, String columnName, Class<E> enumClass) throws SQLException {
        String value = getStringOrNull(rs, columnName);
        if (value != null && !value.isBlank()) {
            try {
                return Enum.valueOf(enumClass, value);
            } catch (IllegalArgumentException e) {
                // Gérer l'erreur si la chaîne ne correspond à aucune constante Enum
                System.err.println("Erreur de mapping d'Enum pour " + columnName + " avec valeur : " + value);
            }
        }
        return null;
    }


    // ============================================================
    //  PATIENT
    // ============================================================
    public static Patient mapPatient(ResultSet rs) throws SQLException {

        Patient patientRow = mapBaseEntity(rs, new Patient());

        patientRow.setNom(rs.getString("nom"));
        patientRow.setPrenom(rs.getString("prenom"));
        patientRow.setAdresse(rs.getString("adresse"));
        patientRow.setTelephone(rs.getString("telephone"));
        patientRow.setEmail(rs.getString("email"));

        var dn = rs.getDate("dateNaissance");
        if (dn != null) patientRow.setDateNaissance(dn.toLocalDate());

        // Mappage robuste des Enums Sexe et Assurance
        patientRow.setSexe(mapEnum(rs, "sexe", Sexe.class));
        // NOTE: La colonne 'assurance' dans Patient est VARCHAR(50).
        // Si Assurance est un ENUM, son mappage doit être fait comme ceci:
        // patientRow.setAssurance(mapEnum(rs, "assurance", Assurance.class));
        // Sinon, si Assurance est un simple String:
        patientRow.setAssurance(mapEnum(rs, "assurance", Assurance.class));

        // list vide : chargée dans le service
        patientRow.setAntecedents(new ArrayList<>());

        return patientRow;
    }


    // ============================================================
    //  ANTECEDENT
    // ============================================================
    public static Antecedent mapAntecedent(ResultSet rs) throws SQLException {

        Antecedent a = mapBaseEntity(rs, new Antecedent());

        a.setNom(rs.getString("nom"));
        a.setDescription(rs.getString("description"));

        a.setCategorie(mapEnum(rs, "categorie", CategorieAntecedent.class));
        a.setNiveauDeRisk(mapEnum(rs, "niveauRisque", NiveauDeRisk.class));

        return a;
    }


    // ============================================================
    //  CERTIFICAT
    // ============================================================
    public static Certificat mapCertificat(ResultSet rs) throws SQLException {

        Certificat c = mapBaseEntity(rs, new Certificat());

        Date dDebut = rs.getDate("dateDebut");
        if (dDebut != null) c.setDateDebut(dDebut.toLocalDate());

        Date dFin = rs.getDate("dateFin");
        if (dFin != null) c.setDateFin(dFin.toLocalDate());

        c.setDureeRepos(rs.getInt("dureeRepos"));
        c.setContenu(rs.getString("contenu"));

        // Mappage des relations (IDs uniquement)
        long consultationId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation consult = new Consultation();
            consult.setId(consultationId);
            c.setConsultation(consult);
        }

        long dossierMedId = rs.getLong("dossier_med_id");
        if (!rs.wasNull()) {
            DossierMedical dm = new DossierMedical();
            dm.setId(dossierMedId);
            c.setDossierMedical(dm);
        }

        return c;
    }


    // ============================================================
    //  DOSSIER MEDICAL
    // ============================================================
    public static DossierMedical mapDossierMedical(ResultSet rs) throws SQLException {

        DossierMedical d = mapBaseEntity(rs, new DossierMedical());

        d.setHistorique(rs.getString("historique"));

        // Mappage des relations (IDs uniquement)
        long patId = rs.getLong("pat_id");
        if (!rs.wasNull()) {
            Patient p = new Patient();
            p.setId(patId);
            d.setPat(p);
        }

        long medecineId = rs.getLong("medcine_id");
        if (!rs.wasNull()) {
            // Initialisation directe en Medecin pour correspondre au type de la relation
            Medecin medecin = new Medecin();
            medecin.setId(medecineId);
            d.setMedcine(medecin);
        }

        return d;
    }


    // ============================================================
    //  NOTIFICATION
    // ============================================================
    public static Notification mapNotification(ResultSet rs) throws SQLException {

        Notification n = mapBaseEntity(rs, new Notification());

        // Mappage robuste des Enums
        n.setTitre(mapEnum(rs, "titre", TitreNotification.class));

        n.setMessage(rs.getString("message"));

        Date d = rs.getDate("date");
        if (d != null) n.setDate(d.toLocalDate());

        Time t = rs.getTime("time");
        if (t != null) n.setTime(t.toLocalTime());

        n.setType(mapEnum(rs, "type", TypeNotification.class));
        n.setPriorite(mapEnum(rs, "priorite", PrioriteNotification.class));

        return n;
    }


    // ============================================================
    //  ROLE
    // ============================================================
    public static Role mapRole(ResultSet rs) throws SQLException {

        Role role = mapBaseEntity(rs, new Role());

        role.setLibelle(mapEnum(rs, "libelle", RoleR.class));

        String privs = rs.getString("privileges");
        if (privs != null && !privs.isBlank()) {
            // Utiliser trim() pour nettoyer les espaces
            role.setPrivileges(List.of(privs.split(",")));
        } else {
            role.setPrivileges(new ArrayList<>());
        }

        return role;
    }


    // ============================================================
    //  UTILISATEUR (générique)
    // ============================================================
    public static <T extends Utilisateur> T mapUtilisateur(ResultSet rs, T utilisateur) throws SQLException {

        utilisateur = mapBaseEntity(rs, utilisateur);

        utilisateur.setNom(rs.getString("nom"));
        utilisateur.setPrenom(rs.getString("prenom"));
        utilisateur.setEmail(rs.getString("email"));
        utilisateur.setAdresse(rs.getString("adresse"));
        utilisateur.setCin(rs.getString("cin"));
        utilisateur.setTelephone(rs.getString("telephone"));

        var dn = rs.getDate("dateNaissance");
        if (dn != null) utilisateur.setDateNaissance(dn.toLocalDate());

        // CORRIGÉ : Mappage du sexe
        utilisateur.setSexe(mapEnum(rs, "sexe", Sexe.class));

        var lastLogin = rs.getTimestamp("lastLoginDate");
        if (lastLogin != null)
            utilisateur.setLastLoginDate(lastLogin.toLocalDateTime());

        utilisateur.setMotDePasse(rs.getString("motDePasse"));

        // relations via services (rôles et notifications)
        utilisateur.setPrivileges(new ArrayList<>());
        utilisateur.setNotifications(new ArrayList<>());


        return utilisateur;

    }

    // ============================================================
    //  ADMIN
    // ============================================================
    public static Admin mapAdmin(ResultSet rs) throws SQLException {
        // Appelle mapUtilisateur pour mapper les champs de base, y compris l'ID.
        // Puisque Admin hérite de Utilisateur, c'est la façon correcte de mapper.
        return mapUtilisateur(rs, new Admin());
    }

    // ============================================================
    //  STAFF
    // ============================================================
    public static Staff mapStaff(ResultSet rs) throws SQLException {
        // CORRIGÉ : Staff hérite de Utilisateur. On utilise mapUtilisateur pour le mapping de base.
        Staff s = mapUtilisateur(rs, new Staff());

        s.setSalaire(rs.getDouble("salaire"));
        s.setPrime(rs.getDouble("prime"));

        var dateRec = rs.getDate("dateRecrutement");
        if (dateRec != null) s.setDateRecrutement(dateRec.toLocalDate());

        s.setSoldeConge(rs.getInt("soldeConge"));

        // Relation CabinetMedicale (juste l'id, le reste peut être chargé via service)
        long cabinetId = rs.getLong("cabinetMedicale_id");
        if (!rs.wasNull()) {
            CabinetMedicale cabinet = new CabinetMedicale();
            cabinet.setId(cabinetId);
            s.setCabinetMedicale(cabinet);
        }

        return s;
    }

    // ============================================================
    //  MEDECIN
    // ============================================================
    public static Medecin mapMedecin(ResultSet rs) throws SQLException {
        // 1 & 2. Map all inherited fields (Utilisateur + Staff)
        Medecin medecin = new Medecin();
        mapStaff(rs, medecin);

        // 3. Map Medecin-specific fields
        medecin.setSpecialite(rs.getString("specialite"));

        // 4. Initialize collections for later service loading
        medecin.setAgendaDocteur(new AgendaMensuel());
        medecin.setDossiersMed(new ArrayList<>());

        return medecin;
    }

    // Surcharge de mapStaff pour l'utiliser dans mapMedecin/mapSecretaire
    private static <T extends Staff> T mapStaff(ResultSet rs, T staff) throws SQLException {
        staff = mapUtilisateur(rs, staff);

        staff.setSalaire(rs.getDouble("salaire"));
        staff.setPrime(rs.getDouble("prime"));

        var dateRec = rs.getDate("dateRecrutement");
        if (dateRec != null) staff.setDateRecrutement(dateRec.toLocalDate());

        staff.setSoldeConge(rs.getInt("soldeConge"));

        // Relation CabinetMedicale
        long cabinetId = rs.getLong("cabinetMedicale_id");
        if (!rs.wasNull()) {
            CabinetMedicale cabinet = new CabinetMedicale();
            cabinet.setId(cabinetId);
            staff.setCabinetMedicale(cabinet);
        }

        return staff;
    }

    // ============================================================
    //  SECRETAIRE
    // ============================================================
    public static Secretaire mapSecretaire(ResultSet rs) throws SQLException {
        // CORRIGÉ : Secretaire hérite de Staff. On utilise mapStaff pour le mapping de base.
        Secretaire s = new Secretaire();
        mapStaff(rs, s);

        s.setNumCNS(rs.getString("numCNS"));
        s.setCommission(rs.getDouble("commission"));

        return s;
    }

    // ============================================================
    //  CABINET MEDICALE
    // ============================================================
    public static CabinetMedicale mapCabinetMedical(ResultSet rs) throws SQLException {

        CabinetMedicale c = mapBaseEntity(rs, new CabinetMedicale());

        c.setNom(rs.getString("nom"));
        c.setEmail(rs.getString("email"));
        c.setLogo(rs.getString("logo"));
        c.setCategorie(rs.getString("categorie"));
        c.setTel1(rs.getString("tel1"));
        c.setTel2(rs.getString("tel2"));
        c.setSiteWeb(rs.getString("siteWeb"));
        c.setInstagram(rs.getString("instagram"));
        c.setFacebook(rs.getString("facebook"));
        c.setDescription(rs.getString("description"));

        // Initialisation des collections
        c.setStatistiques(new ArrayList<>());
        c.setCharges(new ArrayList<>());
        c.setRevenus(new ArrayList<>());

        return c;
    }

    // ============================================================
    // Statistiques
    // ============================================================
    public static Statistiques mapStatistiques(ResultSet rs) throws SQLException {
        Statistiques s = mapBaseEntity(rs, new Statistiques());

        s.setNom(rs.getString("nom"));

        s.setCategorie(mapEnum(rs, "categorie", CategorieStatistique.class));

        // CORRIGÉ : Vérification du double
        double chiffre = rs.getDouble("chiffre");
        if (!rs.wasNull()) s.setChiffre(chiffre);

        java.sql.Date d = rs.getDate("dateCalcul");
        if (d != null) s.setDateCalcul(d.toLocalDate());

        // Chargement minimal de CabinetMedicale : juste l'ID
        long cabinetId = rs.getLong("cabinet_id");
        if (!rs.wasNull()) {
            CabinetMedicale c = new CabinetMedicale();
            c.setId(cabinetId);
            s.setCabinetMedicale(c);
        }

        return s;
    }

    // ============================================================
    // ACTE MEDICAL
    // ============================================================
    public static ActeMedical mapActeMedical(ResultSet rs) throws SQLException {
        ActeMedical acte = mapBaseEntity(rs, new ActeMedical());

        acte.setLibelle(rs.getString("libelle"));
        acte.setCategorie(rs.getString("categorie"));

        // CORRIGÉ : Vérification du float
        float prix = rs.getFloat("prixDeBase");
        if (!rs.wasNull()) acte.setPrixDeBase(prix);

        // La liste d'interventions sera chargée via le service
        acte.setInterventionMedecin(new ArrayList<>());

        return acte;
    }

    // ============================================================
    // CONSULTATION
    // ============================================================
    public static Consultation mapConsultation(ResultSet rs) throws SQLException {
        Consultation c = mapBaseEntity(rs, new Consultation()); // Utiliser mapBaseEntity

        // date de la consultation
        Timestamp dateConsult = rs.getTimestamp("date");
        c.setDate(dateConsult != null ? dateConsult.toLocalDateTime() : null);

        // statut
        c.setStatus(mapEnum(rs, "status", StatusConsultation.class));

        c.setNotes(rs.getString("notes"));
        // CORRIGÉ : Assurez-vous que la colonne 'observations_medecin' est présente
        c.setObservationsMedecin(getStringOrNull(rs, "observations_medecin"));

        // dossier_medical_id (plus simple sans vérifier la colonne)
        long dossierId = rs.getLong("dossier_medical_id");
        if (!rs.wasNull()) {
            DossierMedical dm = new DossierMedical();
            dm.setId(dossierId);
            c.setDossierMedical(dm);
        }

        // Initialisation des collections
        c.setOrdonnances(new ArrayList<>());
        c.setCertificats(new Certificat()); // CORRIGÉ : doit être initialisée
        c.setIms(new ArrayList<>());
        c.setFactures(new ArrayList<>());
        c.setRendezVouses(new ArrayList<>());

        return c;
    }

    // ============================================================
    // INTERVENTION MEDECIN
    // ============================================================
    public static InterventionMedecin mapInterventionMedecin(ResultSet rs) throws SQLException {
        InterventionMedecin im = mapBaseEntity(rs, new InterventionMedecin());

        // CORRIGÉ : Vérification du double
        double prix = rs.getDouble("prixDePatient");
        if (!rs.wasNull()) im.setPrixDePatient(prix);

        // CORRIGÉ : Vérification de l'int
        int num = rs.getInt("numDent");
        if (!rs.wasNull()) im.setNumDent(num);

        // Mappage des relations (IDs uniquement)
        long consultationId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation c = new Consultation();
            c.setId(consultationId);
            im.setConsultation(c);
        }

        long acteMedicalId = rs.getLong("acteMedical_id");
        if (!rs.wasNull()) {
            ActeMedical am = new ActeMedical();
            am.setId(acteMedicalId);
            im.setActeMedical(am);
        }

        return im;
    }
    // ============================================================
    // CHARGES
    // ============================================================
    public static Charges mapCharges(ResultSet rs) throws SQLException {
        Charges c = mapBaseEntity(rs, new Charges());

        c.setTitre(rs.getString("titre"));
        c.setDescription(rs.getString("description"));

        // CORRIGÉ : Vérification du double
        double montant = rs.getDouble("montant");
        if (!rs.wasNull()) c.setMontant(montant);

        Timestamp dt = rs.getTimestamp("date");
        if (dt != null) c.setDate(dt.toLocalDateTime());

        // Relation CabinetMedicale à charger via service
        long cabinetId = rs.getLong("cabinet_id");
        if (!rs.wasNull()) {
            CabinetMedicale cabinet = new CabinetMedicale();
            cabinet.setId(cabinetId);
            c.setCabinetMedicale(cabinet);
        }

        return c;
    }

    // ============================================================
    // REVENUS
    // ============================================================
    public static Revenus mapRevenus(ResultSet rs) throws SQLException {
        Revenus r = mapBaseEntity(rs, new Revenus());

        r.setTitre(rs.getString("titre"));
        r.setDescription(rs.getString("description"));

        // CORRIGÉ : Vérification du double
        double montant = rs.getDouble("montant");
        if (!rs.wasNull()) r.setMontant(montant);

        Timestamp dt = rs.getTimestamp("date");
        if (dt != null) r.setDate(dt.toLocalDateTime());

        // Relation CabinetMedicale à charger via service
        long cabinetId = rs.getLong("cabinet_id");
        if (!rs.wasNull()) {
            CabinetMedicale cabinet = new CabinetMedicale();
            cabinet.setId(cabinetId);
            r.setCabinetMedicale(cabinet);
        }

        return r;
    }

    // ============================================================
    // FACTURE
    // ============================================================
    public static Facture mapFacture(ResultSet rs) throws SQLException {
        Facture f = mapBaseEntity(rs, new Facture());

        // CORRIGÉ : Vérification des floats
        float totalFact = rs.getFloat("totalFact");
        if (!rs.wasNull()) f.setTotalFact(totalFact);

        float totalPaye = rs.getFloat("totalPaye");
        if (!rs.wasNull()) f.setTotalPaye(totalPaye);

        float reste = rs.getFloat("reste");
        if (!rs.wasNull()) f.setReste(reste);

        Date d = rs.getDate("date");
        if (d != null) f.setDate(d.toLocalDate());

        f.setStatut(mapEnum(rs, "statut", StatutFacture.class));

        // Relations Consultation et SituationFinanciere à charger via service (IDs uniquement)
        long consultationId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation c = new Consultation();
            c.setId(consultationId);
            f.setConsultation(c);
        }

        long sfId = rs.getLong("sf_id");
        if (!rs.wasNull()) {
            SituationFinanciere sf = new SituationFinanciere();
            sf.setId(sfId);
            f.setSf(sf);
        }

        return f;
    }

    // ============================================================
    // Situation Financiere
    // ============================================================
    public static SituationFinanciere mapSituationFinanciere(ResultSet rs) throws SQLException {
        SituationFinanciere sf = mapBaseEntity(rs, new SituationFinanciere());

        // CORRIGÉ : Vérification des floats
        float totalDesActes = rs.getFloat("totalDesActes");
        if (!rs.wasNull()) sf.setTotalDesActes(totalDesActes);

        float totalPaye = rs.getFloat("totalPaye");
        if (!rs.wasNull()) sf.setTotalPaye(totalPaye);

        float credit = rs.getFloat("credit");
        if (!rs.wasNull()) sf.setCredit(credit);

        sf.setEnPromo(mapEnum(rs, "enPromo", PromoStatus.class));
        sf.setStatus(mapEnum(rs, "status", Status.class));

        // Relations listeFactures et dossierMedical à charger via service (IDs uniquement)
        sf.setListeFactures(new ArrayList<>());

        long dossierMedicalId = rs.getLong("dossierMedical_id");
        if (!rs.wasNull()) {
            DossierMedical dm = new DossierMedical();
            dm.setId(dossierMedicalId);
            sf.setDossierMedical(dm);
        }

        return sf;
    }

    // ============================================================
    // AgendaMensuel
    // ============================================================
    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        AgendaMensuel a = mapBaseEntity(rs, new AgendaMensuel()); // Utiliser mapBaseEntity

        a.setMois(mapEnum(rs, "mois", Mois.class));

        // Ne pas récupérer les jours ici, on les récupère via la table de liaison
        a.setJoursNonDisponible(new ArrayList<>());

        long medId = rs.getLong("medecin_id");
        if (!rs.wasNull()) {
            Medecin m = new Medecin();
            m.setId(medId);
            a.setMedecin(m);
        }

        return a;
    }

    // ============================================================
    // RendezVous
    // ============================================================
    public static RendezVous mapRendezVous(ResultSet rs) throws SQLException {
        RendezVous r = mapBaseEntity(rs, new RendezVous()); // Utiliser mapBaseEntity

        r.setDate(rs.getTimestamp("dateRDV") != null ? rs.getTimestamp("dateRDV").toLocalDateTime() : null);
        r.setTime(rs.getTime("heure_rdv") != null ? rs.getTime("heure_rdv").toLocalTime() : null);
        r.setMotif(rs.getString("motif"));

        r.setStatus(mapEnum(rs, "status", Status.class));

        r.setNoteMedecin(rs.getString("note_medecin"));

        // dossier médical
        long dmid = rs.getLong("dossier_med_id");
        if (!rs.wasNull()) {
            DossierMedical dm = new DossierMedical();
            dm.setId(dmid);
            r.setDossierMed(dm);
        }

        // consultation
        long cid = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation c = new Consultation();
            c.setId(cid);
            r.setConsultation(c);
        }

        return r;
    }

    // ============================================================
    // Medicament
    // ============================================================
    public static Medicament mapMedicament(ResultSet rs) throws SQLException {
        Medicament m = mapBaseEntity(rs, new Medicament()); // Utiliser mapBaseEntity

        m.setNom(rs.getString("nom"));
        m.setLabo(rs.getString("labo"));
        m.setType(rs.getString("type"));

        m.setForme(mapEnum(rs, "forme", Forme.class));

        // CORRIGÉ : Vérification du boolean (parfois getBoolean peut être unreliable)
        Boolean remboursable = (Boolean) rs.getObject("remboursable");
        m.setRemboursable(remboursable != null && remboursable);

        // CORRIGÉ : Vérification du double
        double prix = rs.getDouble("prix_unitaire");
        if (!rs.wasNull()) m.setPrixUnitaire(prix);

        m.setDescription(rs.getString("description"));

        // prescriptions NON CHARGEES ici (Lazy)
        m.setPrescriptions(new ArrayList<>());

        return m;
    }

    // ============================================================
    // Ordonnance
    // ============================================================
    public static Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        Ordonnance o = mapBaseEntity(rs, new Ordonnance()); // Utiliser mapBaseEntity

        o.setDateOrdonnance(rs.getDate("date_ordonnance") != null ? rs.getDate("date_ordonnance").toLocalDate() : null);

        // Relations : consultation et dossierMedical
        long consultationId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation c = new Consultation();
            c.setId(consultationId);
            o.setConsultation(c);
        }

        long dossierId = rs.getLong("dossier_med_id");
        if (!rs.wasNull()) {
            DossierMedical d = new DossierMedical();
            d.setId(dossierId);
            o.setDossierMedical(d);
        }

        // prescriptions NON CHARGEES ici (lazy)
        o.setPrescriptions(new ArrayList<>());

        return o;
    }

    // ============================================================
    // Prescription
    // ============================================================
    public static Prescription mapPrescription(ResultSet rs) throws SQLException {
        Prescription p = mapBaseEntity(rs, new Prescription()); // Utiliser mapBaseEntity

        // CORRIGÉ : Vérification des Int
        int qte = rs.getInt("qte");
        if (!rs.wasNull()) p.setQte(qte);

        p.setFrequence(rs.getString("frequence"));

        int duree = rs.getInt("duree");
        if (!rs.wasNull()) p.setDuree(duree);

        // Relations : ordonnance et medicament
        long ordonnanceId = rs.getLong("ordonnance_id");
        if (!rs.wasNull()) {
            Ordonnance o = new Ordonnance();
            o.setId(ordonnanceId);
            p.setOrdonnance(o);
        }

        long medicamentId = rs.getLong("medicament_id");
        if (!rs.wasNull()) {
            Medicament m = new Medicament();
            m.setId(medicamentId);
            p.setMedicament(m);
        }

        return p;
    }
}