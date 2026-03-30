package com.projetTransversalIsi.pedagogie.infrastructure;

import com.projetTransversalIsi.pedagogie.application.dto.CreateUeRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.UeResponseDTO;
import com.projetTransversalIsi.pedagogie.application.dto.UpdateUeRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaUeEntity;
import com.projetTransversalIsi.profil.infrastructure.JpaTeacherProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UeMapper {

    // Domain <-> Entity
    @Mapping(target = "enseignants", ignore = true)
    JpaUeEntity ueToJpaUeEntity(Ue ue);

    default Ue jpaUeEntityToUe(JpaUeEntity entity) {
        if (entity == null) return null;
        Ue ue = new Ue();
        ue.setId(entity.getId());
        ue.setLibelle(entity.getLibelle());
        ue.setCode(entity.getCode());
        ue.setCredit(entity.getCredit());
        ue.setVolumeHoraireTotal(entity.getVolumeHoraireTotal());
        ue.setDescription(entity.getDescription());
        ue.setSpecialiteId(entity.getSpecialiteId());
        ue.setCouleur(entity.getCouleur());
        ue.setCreatedAt(entity.getCreatedAt());
        ue.setDeletedAt(entity.getDeletedAt());
        ue.setIsDeleted(entity.getIsDeleted());
        if (entity.getEnseignants() != null) {
            ue.setEnseignantIds(entity.getEnseignants().stream()
                    .map(JpaTeacherProfileEntity::getId)
                    .collect(Collectors.toSet()));
        }
        return ue;
    }

    // Request DTO -> Domain
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Ue toDomain(CreateUeRequestDTO request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateFromDTO(UpdateUeRequestDTO request, @org.mapstruct.MappingTarget Ue ue);

    // Domain -> Response DTO
    UeResponseDTO toResponseDTO(Ue ue);

    List<UeResponseDTO> toResponseDTOList(List<Ue> ues);

    // Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "enseignants", ignore = true)
    JpaUeEntity toEntity(CreateUeRequestDTO request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "enseignants", ignore = true)
    void updateEntityFromDTO(UpdateUeRequestDTO request, @org.mapstruct.MappingTarget JpaUeEntity entity);
}
