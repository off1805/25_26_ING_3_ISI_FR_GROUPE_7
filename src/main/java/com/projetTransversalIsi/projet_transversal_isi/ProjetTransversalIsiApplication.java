package com.projetTransversalIsi.projet_transversal_isi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.projetTransversalIsi", // Pour scanner tes Use Cases et Services
        "com.projetTransversalIsi", // Pour scanner tes Controllers
        "com.projetTransversalIsi" // Si tu as d'autres modules
})
@EntityScan(basePackages = "com.projetTransversalIsi")
@EnableJpaRepositories(basePackages = "com.projetTransversalIsi")
public class ProjetTransversalIsiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetTransversalIsiApplication.class, args);
    }

}

/*

Rouge : Le fichier est créé localement mais n'a pas encore été ajouté à l'index Git (Untracked). Git ne le surveille pas encore.

Vert : Le fichier est ajouté (Staged) et prêt à être "committé".

Blanc (ou Gris selon le thème) : Le fichier est déjà suivi par Git et n'a subi aucune modification par rapport à la version du serveur. C'est le cas des fichiers de ton collègue que tu viens de récupérer via le pull.

Bleu : Le fichier est déjà suivi par Git mais tu l'as modifié localement.

*/