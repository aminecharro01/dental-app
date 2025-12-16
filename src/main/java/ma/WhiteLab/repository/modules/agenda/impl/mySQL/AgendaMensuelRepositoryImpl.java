package ma.WhiteLab.repository.modules.agenda.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.enums.Jour;
import ma.WhiteLab.entities.enums.Mois;
import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.entities.agenda.AgendaMensuel;
import ma.WhiteLab.repository.modules.agenda.api.AgendaMensuelRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaMensuelRepositoryImpl implements AgendaMensuelRepository {

    // =========================
    // CRUD
    // =========================

    @Override
    public List<AgendaMensuel> findAll() {
        List<AgendaMensuel> out = new ArrayList<>();
        String sql = "SELECT * FROM AgendaMensuel";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AgendaMensuel agenda = mapAgendaMensuel(rs);
                agenda.setJoursNonDisponible(getJoursOfAgenda(conn, agenda.getId()));
                out.add(agenda);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public AgendaMensuel findById(Long id) {
        String sql = "SELECT * FROM AgendaMensuel WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AgendaMensuel agenda = mapAgendaMensuel(rs);
                    agenda.setJoursNonDisponible(getJoursOfAgenda(conn, agenda.getId()));
                    return agenda;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void create(AgendaMensuel a) {
        String sql = "INSERT INTO AgendaMensuel (mois, medecin_id, dateCreation, dateMiseAJour, creePar, modifierPar) VALUES (?, ?, NOW(), NOW(), ?, ?)";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getMois() != null ? a.getMois().name() : null);
            ps.setLong(2, a.getMedecin() != null ? a.getMedecin().getId() : Types.NULL);
            ps.setString(3, a.getCreePar());
            ps.setString(4, a.getModifierPar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getLong(1));
            }

            // Ajouter les jours non disponibles
            addJoursToAgenda(conn, a.getId(), a.getJoursNonDisponible());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(AgendaMensuel a) {
        String sql = "UPDATE AgendaMensuel SET mois = ?, medecin_id = ?, dateMiseAJour = NOW(), modifierPar = ? WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getMois() != null ? a.getMois().name() : null);
            ps.setLong(2, a.getMedecin() != null ? a.getMedecin().getId() : Types.NULL);
            ps.setString(3, a.getModifierPar());
            ps.setLong(4, a.getId());
            ps.executeUpdate();

            // Supprimer tous les jours existants
            removeAllJoursFromAgenda(conn, a.getId());

            // Ajouter les jours actuels
            addJoursToAgenda(conn, a.getId(), a.getJoursNonDisponible());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AgendaMensuel a) {
        if (a != null) deleteById(a.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM AgendaMensuel WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // Queries par Médecin / Mois
    // =========================

    @Override
    public List<AgendaMensuel> findByMedecinId(Long medecinId) {
        List<AgendaMensuel> out = new ArrayList<>();
        String sql = "SELECT * FROM AgendaMensuel WHERE medecin_id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AgendaMensuel agenda = mapAgendaMensuel(rs);
                    agenda.setJoursNonDisponible(getJoursOfAgenda(conn, agenda.getId()));
                    out.add(agenda);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public List<AgendaMensuel> findByMedecinIdAndMois(Long medecinId, String mois) {
        List<AgendaMensuel> out = new ArrayList<>();
        String sql = "SELECT * FROM AgendaMensuel WHERE medecin_id = ? AND mois = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setString(2, mois);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AgendaMensuel agenda = mapAgendaMensuel(rs);
                    agenda.setJoursNonDisponible(getJoursOfAgenda(conn, agenda.getId()));
                    out.add(agenda);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    // =========================
    // Gestion des jours (Many-to-Many simplifiée)
    // =========================

    private void addJoursToAgenda(Connection conn, Long agendaId, List<Jour> jours) throws SQLException {
        if (jours == null || jours.isEmpty()) return;

        String sql = "INSERT INTO AgendaMensuel_Jour (agenda_id, jour_nom) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Jour jour : jours) {
                ps.setLong(1, agendaId);
                ps.setString(2, jour.name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void removeAllJoursFromAgenda(Connection conn, Long agendaId) throws SQLException {
        String sql = "DELETE FROM AgendaMensuel_Jour WHERE agenda_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.executeUpdate();
        }
    }

    private List<Jour> getJoursOfAgenda(Connection conn, Long agendaId) throws SQLException {
        List<Jour> jours = new ArrayList<>();
        String sql = "SELECT jour_nom FROM AgendaMensuel_Jour WHERE agenda_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jours.add(Jour.valueOf(rs.getString("jour_nom")));
                }
            }
        }
        return jours;
    }

    // =========================
    // Mapper
    // =========================

    private AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        AgendaMensuel a = new AgendaMensuel();
        a.setId(rs.getLong("id"));
        a.setMois(rs.getString("mois") != null ? Mois.valueOf(rs.getString("mois")) : null);

        long medId = rs.getLong("medecin_id");
        if (!rs.wasNull()) {
            Medecin m = new Medecin();
            m.setId(medId);
            a.setMedecin(m);
        }

        a.setCreePar(rs.getString("creePar"));
        a.setModifierPar(rs.getString("modifierPar"));
        a.setDateCreation(rs.getTimestamp("dateCreation").toLocalDateTime());
        a.setDateMiseAJour(rs.getTimestamp("dateMiseAJour").toLocalDateTime());

        return a;
    }
}
