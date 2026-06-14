package com.projetTransversalIsi.justificatif.application.use_cases;

import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;
import com.projetTransversalIsi.justificatif.application.dto.SoumettreJustificatifDTO;
import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.domain.model.JustificatifFichier;
import com.projetTransversalIsi.justificatif.domain.repository.JustificatifRepository;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SoumettreJustificatifUCImpl implements SoumettreJustificatifUC {

    private final JustificatifRepository justificatifRepo;
    private final AnneeScolaireRepository anneeScolaireRepository;

    @Value("${app.upload.dir.justificatifs:uploads/justificatifs}")
    private String uploadDir;

    @Override
    public JustificatifResponseDTO execute(SoumettreJustificatifDTO dto, List<MultipartFile> fichiers) {
        // Garde anti-doublon : tout justificatif existant (quel que soit son statut) bloque
        // la soumission. Une absence rejetée ne peut pas être re-justifiée.
        if (dto.seanceIds() != null) {
            for (Long seanceId : dto.seanceIds()) {
                boolean alreadyExists = !justificatifRepo
                        .findByEtudiantIdAndSeanceId(dto.etudiantId(), seanceId)
                        .isEmpty();
                if (alreadyExists) {
                    throw new IllegalStateException(
                            "Un justificatif existe déjà pour la séance " + seanceId);
                }
            }
        }

        // Sauvegarde des fichiers et construction des value objects
        List<JustificatifFichier> justifFichiers = new ArrayList<>();
        if (fichiers != null) {
            for (MultipartFile fichier : fichiers) {
                if (fichier != null && !fichier.isEmpty()) {
                    String ext      = getExtension(fichier.getOriginalFilename());
                    String filename = dto.etudiantId() + "_" + UUID.randomUUID() + ext;
                    try {
                        Path dir = Paths.get(uploadDir);
                        Files.createDirectories(dir);
                        Files.copy(fichier.getInputStream(), dir.resolve(filename),
                                StandardCopyOption.REPLACE_EXISTING);
                        justifFichiers.add(new JustificatifFichier(
                                "/uploads/justificatifs/" + filename,
                                fichier.getOriginalFilename()));
                    } catch (IOException e) {
                        throw new RuntimeException("Erreur lors de la sauvegarde du fichier", e);
                    }
                }
            }
        }

        Justificatif justificatif = new Justificatif(
                dto.etudiantId(), dto.seanceIds(), dto.motif(),
                dto.message(), justifFichiers, dto.dateAbsence());
        anneeScolaireRepository.findActive().map(AnneeScolaire::getId)
                .ifPresent(justificatif::setAnneeScolaireId);

        return JustificatifResponseDTO.fromDomain(justificatifRepo.save(justificatif));
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".pdf";
        return filename.substring(filename.lastIndexOf("."));
    }
}
