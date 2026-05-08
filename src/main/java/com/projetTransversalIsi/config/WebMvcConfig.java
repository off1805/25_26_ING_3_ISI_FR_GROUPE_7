package com.projetTransversalIsi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/photos}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Dossier photos (profils, justificatifs…)
        Path absoluteUploadPath = Paths.get(uploadDir).toAbsolutePath();
        registry.addResourceHandler("/uploads/photos/**")
                .addResourceLocations("file:" + absoluteUploadPath + "/");

        // Racine uploads/ — sert les fichiers communs (logo école, etc.)
        Path absoluteUploadsRoot = Paths.get("uploads").toAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absoluteUploadsRoot + "/");
    }
}
