package com.projetTransversalIsi.justificatif.application.use_cases;

import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;
import com.projetTransversalIsi.justificatif.application.dto.SoumettreJustificatifDTO;
import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.domain.repository.JustificatifRepository;
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
public class SoumettreJustificatifUCImpl implements SoumettreJustificatifUC {

    private final JustificatifRepository justificatifRepo;

    @Value("${app.upload.dir:uploads/justificatifs}")
    private String uploadDir;

    @Override
    public JustificatifResponseDTO execute(SoumettreJustificatifDTO dto, MultipartFile fichier) {
        String fichierUrl = null;

        if (fichier != null && !fichier.isEmpty()) {
            String ext = getExtension(fichier.getOriginalFilename());
            String filename = dto.etudiantId() + "_" + UUID.randomUUID() + ext;
            try {
                Path dir = Paths.get(uploadDir);
                Files.createDirectories(dir);
                Files.copy(fichier.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                fichierUrl = "/uploads/justificatifs/" + filename;
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la sauvegarde du fichier", e);
            }
        }

        Justificatif justificatif = new Justificatif(
                dto.etudiantId(), dto.seanceId(), dto.motif(), fichierUrl, dto.dateAbsence()
        );
        return JustificatifResponseDTO.fromDomain(justificatifRepo.save(justificatif));
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".pdf";
        return filename.substring(filename.lastIndexOf("."));
    }
}
