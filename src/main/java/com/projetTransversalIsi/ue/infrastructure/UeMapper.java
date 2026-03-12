package com.projetTransversalIsi.ue.infrastructure;

import com.projetTransversalIsi.ue.application.dto.CreateUeRequestDTO;
import com.projetTransversalIsi.ue.application.dto.UeResponseDTO;
import com.projetTransversalIsi.ue.application.dto.UpdateUeRequestDTO;
import com.projetTransversalIsi.ue.domain.Ue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UeMapper {

    // Domain <-> Entity
    JpaUeEntity ueToJpaUeEntity(Ue ue);

    Ue jpaUeEntityToUe(JpaUeEntity jpaUeEntity);

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

    // Entity -> Response DTO
    UeResponseDTO toResponseDTO(JpaUeEntity jpaUeEntity);

    List<UeResponseDTO> jpaEntityListToResponseDTOList(List<JpaUeEntity> entities);

    // Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    JpaUeEntity toEntity(CreateUeRequestDTO request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntityFromDTO(UpdateUeRequestDTO request, @org.mapstruct.MappingTarget JpaUeEntity entity);

    // Response DTO -> Domain
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Ue responseDTOToUe(UeResponseDTO responseDTO);

    // Response DTO -> Entity
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    JpaUeEntity responseDTOToJpaUeEntity(UeResponseDTO responseDTO);
}
