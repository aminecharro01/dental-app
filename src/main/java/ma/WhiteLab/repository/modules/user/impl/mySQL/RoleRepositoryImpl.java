package ma.WhiteLab.repository.modules.user.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.user.Role;
import ma.WhiteLab.entities.enums.RoleR;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.user.api.RoleRepository;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//auteur : Aymane Akarbich

public class RoleRepositoryImpl implements RoleRepository {

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM role ORDER BY libelle";
        List<Role> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapRole(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Role findById(Long id) {
        String sql = "SELECT * FROM role WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapRole(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Role r) {
        String sql = "INSERT INTO Role(libelle, privileges, dateCreation, creePar) VALUES(?,?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getLibelle().name());
            ps.setString(2, r.getPrivileges() != null ? String.join(",", r.getPrivileges()) : "");
            ps.setTimestamp(3, Timestamp.valueOf(r.getDateCreation() != null ? r.getDateCreation() : java.time.LocalDateTime.now()));
            ps.setString(4, r.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Role r) {
        String sql = "UPDATE role SET libelle=?, privileges=?, dateMiseAJour=?, modifierPar=? WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, r.getLibelle().name());
            ps.setString(2, r.getPrivileges() != null ? String.join(",", r.getPrivileges()) : "");
            ps.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(4, r.getModifierPar());
            ps.setLong(5, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Role r) { if (r != null) deleteById(r.getId()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM role WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Role> findByLibelle(RoleR libelle) {
        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, libelle.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapRole(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean existsByLibelle(RoleR libelle) {
        String sql = "SELECT 1 FROM role WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, libelle.name());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM role";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
