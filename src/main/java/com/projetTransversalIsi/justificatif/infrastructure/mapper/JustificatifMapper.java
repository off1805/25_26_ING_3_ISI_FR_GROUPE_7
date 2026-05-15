package com.projetTransversalIsi.justificatif.infrastructure.mapper;

import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.domain.model.JustificatifFichier;
import com.projetTransversalIsi.justificatif.infrastructure.entity.FichierEmbeddable;
import com.projetTransversalIsi.justificatif.infrastructure.entity.JpaJustificatifEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JustificatifMapper {

    public JpaJustificatifEntity toEntity(Justificatif domain) {
        JpaJustificatifEntity entity = new JpaJustificatifEntity();
        entity.setId(domain.getId());
        entity.setEtudiantId(domain.getEtudiantId());
        entity.setSeanceIds(new ArrayList<>(domain.getSeanceIds()));
        entity.setMotif(domain.getMotif());
        entity.setMessage(domain.getMessage());
        entity.setFichiers(domain.getFichiers().stream()
                .map(f -> new FichierEmbeddable(f.getFichierUrl(), f.getNomOriginal()))
                .collect(Collectors.toList()));
        entity.setDateAbsence(domain.getDateAbsence());
        entity.setStatut(domain.getStatut());
        entity.setCommentaireAP(domain.getCommentaireAP());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public Justificatif toDomain(JpaJustificatifEntity entity) {
        Justificatif domain = new Justificatif();
        domain.setId(entity.getId());
        domain.setEtudiantId(entity.getEtudiantId());
        domain.setSeanceIds(new ArrayList<>(entity.getSeanceIds()));
        domain.setMotif(entity.getMotif());
        domain.setMessage(entity.getMessage());

        List<JustificatifFichier> fichiers = entity.getFichiers() == null
                ? new ArrayList<>()
                : entity.getFichiers().stream()
                        .map(f -> new JustificatifFichier(f.getFichierUrl(), f.getNomOriginal()))
                        .collect(Collectors.toList());
        domain.setFichiers(fichiers);

        domain.setDateAbsence(entity.getDateAbsence());
        domain.setStatut(entity.getStatut());
        domain.setCommentaireAP(entity.getCommentaireAP());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }
}
