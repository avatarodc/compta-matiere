package com.examenJava.comptamatiere.service;

import com.examenJava.comptamatiere.model.Utilisateur;
import com.examenJava.comptamatiere.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UtilisateurService implements UserDetailsService {

    private static final Logger LOGGER = Logger.getLogger(UtilisateurService.class.getName());

    @Autowired
    private UtilisateurRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("Tentative de connexion avec l'email : " + email);

        Optional<Utilisateur> optionalUser = repo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            LOGGER.warning("Échec : utilisateur non trouvé pour l'email : " + email);
            throw new UsernameNotFoundException("Utilisateur non trouvé");
        }

        Utilisateur user = optionalUser.get();
        LOGGER.info("Utilisateur trouvé : " + user.getEmail() + " avec rôle : " + user.getRole());

        return new User(
                user.getEmail(),
                user.getMotDePasse(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
