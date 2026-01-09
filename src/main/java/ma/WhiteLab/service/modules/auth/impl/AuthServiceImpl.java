package ma.WhiteLab.service.modules.auth.impl;

import ma.WhiteLab.common.consoleLog.ConsoleLogger;
import ma.WhiteLab.common.utils.RepoFactory;
import ma.WhiteLab.entities.user.Utilisateur;
import ma.WhiteLab.entities.user.Role;
import ma.WhiteLab.entities.user.RolePrivilege;
import ma.WhiteLab.entities.enums.RoleR;
import ma.WhiteLab.repository.modules.user.api.UtilisateurRepository;
import ma.WhiteLab.repository.modules.user.api.RoleRepository;
import ma.WhiteLab.service.modules.auth.api.AuthService;
import ma.WhiteLab.service.modules.auth.api.CredentialsValidator;
import ma.WhiteLab.service.modules.auth.api.PasswordEncoder;
import ma.WhiteLab.mvc.dto.auth.AuthRequest;
import ma.WhiteLab.mvc.dto.auth.AuthResult;
import ma.WhiteLab.mvc.dto.auth.UserPrincipal;
import ma.WhiteLab.service.common.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class AuthServiceImpl implements AuthService {

    private final RepoFactory<UtilisateurRepository> utilisateurRepoFactory;
    private final RepoFactory<RoleRepository> roleRepoFactory;
    private final CredentialsValidator credentialsValidator;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            RepoFactory<UtilisateurRepository> utilisateurRepoFactory,
            RepoFactory<RoleRepository> roleRepoFactory,
            CredentialsValidator credentialsValidator,
            PasswordEncoder passwordEncoder) {

        this.utilisateurRepoFactory = utilisateurRepoFactory;
        this.roleRepoFactory = roleRepoFactory;
        this.credentialsValidator = credentialsValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResult authenticate(AuthRequest request) {
        ConsoleLogger.info("Tentative d'authentification pour : " + request.getLogin());

        // Validation du formulaire
        Map<String, String> errors = credentialsValidator.validate(request);
        if (!errors.isEmpty()) {
            ConsoleLogger.warn("Validation échouée : " + errors);
            return AuthResult.validationError(errors);
        }

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);
            RoleRepository roleRepo = roleRepoFactory.create(cnx);

            Optional<Utilisateur> userOpt = userRepo.findByEmail(request.getLogin());
            if (userOpt.isEmpty()) {
                ConsoleLogger.error("Utilisateur introuvable : " + request.getLogin());
                return AuthResult.failure("Utilisateur introuvable");
            }

            Utilisateur user = userOpt.get();

            if (!passwordEncoder.matches(request.getPassword(), user.getMotDePasse())) {
                ConsoleLogger.error("Mot de passe incorrect pour : " + request.getLogin());
                return AuthResult.failure("Mot de passe incorrect");
            }

            UserPrincipal principal = buildUserPrincipal(user, roleRepo);

            // Mise à jour dernière connexion
            userRepo.updateLastLogin(user.getId());

            ConsoleLogger.info("Authentification réussie : " + request.getLogin() + " (ID: " + user.getId() + ")");
            return AuthResult.success(principal, "Connexion réussie");
        });
    }

    private UserPrincipal buildUserPrincipal(Utilisateur user, RoleRepository roleRepo) {
        List<Role> roles = roleRepo.findRolesByUserId(user.getId());

        Set<RoleR> roleTypes = roles.stream()
                .map(Role::getLibelle)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> privileges = roles.stream()
                .filter(Objects::nonNull)
                .map(Role::getPrivileges)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(RolePrivilege::getPrivilege)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(p -> !p.isBlank())
                .collect(Collectors.toSet());

        RoleR rolePrincipal = roleTypes.stream().findFirst().orElse(null);
        String fullName = buildFullName(user.getPrenom(), user.getNom());

        return new UserPrincipal(
                user.getId(),
                fullName,
                user.getEmail(),
                user.getEmail(),
                rolePrincipal,
                roleTypes,
                privileges
        );
    }

    private String buildFullName(String prenom, String nom) {
        StringBuilder sb = new StringBuilder();
        if (prenom != null && !prenom.isBlank()) sb.append(prenom.trim());
        if (nom != null && !nom.isBlank()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(nom.trim().toUpperCase());
        }
        return sb.length() > 0 ? sb.toString() : "Utilisateur inconnu";
    }

    @Override
    public UserPrincipal loadUserPrincipalByLogin(String login) {
        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);
            RoleRepository roleRepo = roleRepoFactory.create(cnx);

            Utilisateur user = (Utilisateur) userRepo.findByEmail(login)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + login));

            return buildUserPrincipal(user, roleRepo);
        });
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        Transaction.initTransaction(cnx -> {
            UtilisateurRepository repo = utilisateurRepoFactory.create(cnx);

            Utilisateur user = (Utilisateur) repo.findById(userId);
            if (user == null) throw new RuntimeException("Utilisateur introuvable");

            if (!passwordEncoder.matches(oldPassword, user.getMotDePasse())) {
                throw new RuntimeException("Ancien mot de passe incorrect");
            }

            Map<String, String> errors = credentialsValidator.validateNewPassword(newPassword);
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Nouveau mot de passe invalide : " + errors);
            }

            user.setMotDePasse(passwordEncoder.encode(newPassword));
            repo.update(user);

            ConsoleLogger.info("Mot de passe mis à jour pour l'utilisateur ID: " + userId);
            return null;
        });
    }
}