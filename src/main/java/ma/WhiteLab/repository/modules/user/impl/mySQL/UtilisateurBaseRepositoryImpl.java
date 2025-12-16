package ma.WhiteLab.repository.modules.user.impl.mySQL;

import ma.WhiteLab.entities.user.Utilisateur;


public class UtilisateurBaseRepositoryImpl
        extends UtilisateurRepositoryImpl<UtilisateurConcret> {

    public UtilisateurBaseRepositoryImpl() {
        super(UtilisateurConcret.class);
    }
}
