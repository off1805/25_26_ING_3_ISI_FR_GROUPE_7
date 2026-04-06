package com.projetTransversalIsi.pedagogie.infrastructure;

import com.projetTransversalIsi.pedagogie.application.dto.OffreUeResponseDTO;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaOffreUeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
        offreUe.setSpecialiteId(entity.getSpecialiteId());
        offreUe.setEnseignantIds(entity.getEnseignantIds());
        offreUe.setCreatedAt(entity.getCreatedAt());
        return offreUe;
    }

    // Domain -> Entity (ue et anneeScolaire seront assignés manuellement dans le repository)
    @Mapping(target = "ue", ignore = true)
    @Mapping(target = "anneeScolaire", ignore = true)
    JpaOffreUeEntity toEntity(OffreUe offreUe);

    // Domain -> Response DTO
    OffreUeResponseDTO toResponseDTO(OffreUe offreUe);
}
