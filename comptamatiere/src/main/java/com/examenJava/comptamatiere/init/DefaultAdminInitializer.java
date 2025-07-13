package com.examenJava.comptamatiere.init;

import com.examenJava.comptamatiere.model.Utilisateur;
import com.examenJava.comptamatiere.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (utilisateurRepository.findByEmail("admin@mail.com").isEmpty()) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Administrateur");
            admin.setEmail("admin@mail.com");
            admin.setMotDePasse(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");

            utilisateurRepository.save(admin);
            System.out.println("✅ Utilisateur admin créé avec succès (admin@mail.com / admin123)");
        } else {
            System.out.println("ℹ️ Utilisateur admin déjà présent en base.");
        }
    }
}
