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
