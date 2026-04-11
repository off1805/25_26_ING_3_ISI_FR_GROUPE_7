package com.projetTransversalIsi.user.profil.application.use_cases;

import com.projetTransversalIsi.user.profil.infrastructure.JpaProfileEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UploadProfilePhotoUCImpl implements UploadProfilePhotoUC {

    private final EntityManager entityManager;

    @Value("${app.upload.dir:uploads/photos}")
    private String uploadDir;

    @Override
    @Transactional
    public String execute(Long profileId, MultipartFile photo) {
        JpaProfileEntity profile = entityManager.find(JpaProfileEntity.class, profileId);
        if (profile == null) {
            throw new IllegalArgumentException("Profil introuvable : " + profileId);
        }

        String extension = getExtension(photo.getOriginalFilename());
        String filename = profileId + "_" + UUID.randomUUID() + extension;

        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            Files.copy(photo.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la photo", e);
        }

        String photoUrl = "/uploads/photos/" + filename;
        profile.setPhotoUrl(photoUrl);
        entityManager.merge(profile);

        return photoUrl;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}
