package com.projetTransversalIsi.pedagogie.infrastructure;

import com.projetTransversalIsi.pedagogie.application.dto.OffreUeResponseDTO;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUeAssignment;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaOffreUeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface OffreUeMapper {

    // Entity -> Domain
    default OffreUe toDomain(JpaOffreUeEntity entity) {
        if (entity == null) return null;
        OffreUe offreUe = new OffreUe();
        offreUe.setId(entity.getId());
        offreUe.setUeId(entity.getUe() != null ? entity.getUe().getId() : null);
        offreUe.setAnneeScolaireId(entity.getAnneeScolaire() != null ? entity.getAnneeScolaire().getId() : null);
        offreUe.setLibelle(entity.getLibelle());
        offreUe.setCode(entity.getCode());
        offreUe.setCredit(entity.getCredit());
        offreUe.setVolumeHoraireTotal(entity.getVolumeHoraireTotal());
        offreUe.setDescription(entity.getDescription());
        offreUe.setCouleur(entity.getCouleur());
        offreUe.setSemestre(entity.getSemestre());
        offreUe.setSpecialiteId(entity.getSpecialiteId());
        // Union : enseignants du pool de l'UE mère (ue_enseignant) + enseignants
        // ayant une affectation fine (offre_ue_enseignant) sur cette offre.
        Set<Long> enseignantIds = new HashSet<>();
        if (entity.getUe() != null && entity.getUe().getEnseignants() != null) {
            entity.getUe().getEnseignants().forEach(t -> enseignantIds.add(t.getId()));
        }
        Set<OffreUeAssignment> enseignantAssignments = new HashSet<>();
        if (entity.getEnseignantAssignments() != null) {
            entity.getEnseignantAssignments().forEach(link -> {
                enseignantIds.add(link.getEnseignantId());
                enseignantAssignments.add(new OffreUeAssignment(link.getEnseignantId(), link.getClasseId()));
            });
        }
        offreUe.setEnseignantIds(enseignantIds);
        offreUe.setEnseignantAssignments(enseignantAssignments);
        offreUe.setCreatedAt(entity.getCreatedAt());
        return offreUe;
    }

    // Domain -> Entity (ue et anneeScolaire seront assignés manuellement dans le repository)
    @Mapping(target = "ue", ignore = true)
    @Mapping(target = "anneeScolaire", ignore = true)
    @Mapping(target = "enseignantAssignments", ignore = true)
    JpaOffreUeEntity toEntity(OffreUe offreUe);

    // Domain -> Response DTO
    OffreUeResponseDTO toResponseDTO(OffreUe offreUe);
}
