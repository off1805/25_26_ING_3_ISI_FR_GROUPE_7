package com.projetTransversalIsi.pedagogie.infrastructure;

import com.projetTransversalIsi.pedagogie.application.dto.CreateUeRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.UeResponseDTO;
import com.projetTransversalIsi.pedagogie.application.dto.UpdateUeRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaUeEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UeMapperTest {

    private final UeMapper ueMapper = Mappers.getMapper(UeMapper.class);

    @Test
    void shouldMapUeToJpaUeEntity() {
        Ue ue = new Ue(1L, "Maths", "MATH101", 6, 60, "Description", 10L, new java.util.HashSet<>(), "#ffffff", 1, null, null, false);
        JpaUeEntity entity = ueMapper.ueToJpaUeEntity(ue);

        assertEquals(ue.getId(), entity.getId());
        assertEquals(ue.getLibelle(), entity.getLibelle());
        assertEquals(ue.getCode(), entity.getCode());
        assertEquals(ue.getCredit(), entity.getCredit());
        assertEquals(ue.getVolumeHoraireTotal(), entity.getVolumeHoraireTotal());
        assertEquals(ue.getDescription(), entity.getDescription());
        assertEquals(ue.getSpecialiteId(), entity.getSpecialiteId());
        assertEquals(ue.getCouleur(), entity.getCouleur());
    }

    @Test
    void shouldMapJpaUeEntityToUe() {
        JpaUeEntity entity = new JpaUeEntity();
        entity.setId(1L);
        entity.setLibelle("Maths");
        entity.setCode("MATH101");
        entity.setCredit(6);
        entity.setVolumeHoraireTotal(60);
        entity.setDescription("Description");
        entity.setSpecialiteId(10L);
        entity.setCouleur("#ffffff");

        Ue ue = ueMapper.jpaUeEntityToUe(entity);

        assertEquals(entity.getId(), ue.getId());
        assertEquals(entity.getLibelle(), ue.getLibelle());
        assertEquals(entity.getCode(), ue.getCode());
        assertEquals(entity.getCredit(), ue.getCredit());
        assertEquals(entity.getVolumeHoraireTotal(), ue.getVolumeHoraireTotal());
        assertEquals(entity.getDescription(), ue.getDescription());
        assertEquals(entity.getSpecialiteId(), ue.getSpecialiteId());
        assertEquals(entity.getCouleur(), ue.getCouleur());
    }

    @Test
    void shouldMapCreateUeRequestDTOToUe() {
        CreateUeRequestDTO request = new CreateUeRequestDTO("Maths", "MATH101", 6, 60, "Description", "#ffffff", 10L, 1, Set.of(1L, 2L));
        Ue ue = ueMapper.toDomain(request);

        assertNull(ue.getId());
        assertEquals(request.libelle(), ue.getLibelle());
        assertEquals(request.code(), ue.getCode());
        assertEquals(request.credit(), ue.getCredit());
        assertEquals(request.volumeHoraireTotal(), ue.getVolumeHoraireTotal());
        assertEquals(request.description(), ue.getDescription());
        assertEquals(request.couleur(), ue.getCouleur());
        assertEquals(request.specialiteId(), ue.getSpecialiteId());
    }

    @Test
    void shouldUpdateUeFromUpdateUeRequestDTO() {
        UpdateUeRequestDTO request = new UpdateUeRequestDTO("Maths Updated", "MATH101", 8, 80, "Description Updated", "#ff0000", 11L, 2, new java.util.HashSet<>());
        Ue ue = new Ue(1L, "Maths", "MATH101", 6, 60, "Description", 10L, new java.util.HashSet<>(), "#ffffff", 1, null, null, false);
        
        ueMapper.updateFromDTO(request, ue);

        assertEquals(1L, ue.getId());
        assertEquals(request.libelle(), ue.getLibelle());
        assertEquals(request.code(), ue.getCode());
        assertEquals(request.credit(), ue.getCredit());
        assertEquals(request.volumeHoraireTotal(), ue.getVolumeHoraireTotal());
        assertEquals(request.description(), ue.getDescription());
        assertEquals(request.couleur(), ue.getCouleur());
        assertEquals(request.specialiteId(), ue.getSpecialiteId());
    }

    @Test
    void shouldMapUeToUeResponseDTO() {
        Ue ue = new Ue(1L, "Maths", "MATH101", 6, 60, "Description", 10L, new java.util.HashSet<>(), "#ffffff", 1, null, null, false);
        UeResponseDTO response = ueMapper.toResponseDTO(ue);

        assertEquals(ue.getId(), response.id());
        assertEquals(ue.getLibelle(), response.libelle());
        assertEquals(ue.getCode(), response.code());
        assertEquals(ue.getCredit(), response.credit());
        assertEquals(ue.getVolumeHoraireTotal(), response.volumeHoraireTotal());
        assertEquals(ue.getDescription(), response.description());
        assertEquals(ue.getCouleur(), response.couleur());
    }

    @Test
    void shouldMapJpaUeEntityToUeResponseDTO() {
        JpaUeEntity entity = new JpaUeEntity();
        entity.setId(1L);
        entity.setLibelle("Maths");
        entity.setCode("MATH101");
        entity.setCredit(6);
        entity.setVolumeHoraireTotal(60);
        entity.setDescription("Description");
        entity.setSpecialiteId(10L);
        entity.setCouleur("#ffffff");

        // Use a better way if toResponseDTO(JpaUeEntity) is not available
        // Usually, we map Entity -> Domain -> DTO or Entity -> DTO
        // Since toResponseDTO(JpaUeEntity) is not in the interface, we can use Entity -> Domain -> DTO
        Ue domain = ueMapper.jpaUeEntityToUe(entity);
        UeResponseDTO response = ueMapper.toResponseDTO(domain);

        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getLibelle(), response.libelle());
        assertEquals(entity.getCode(), response.code());
        assertEquals(entity.getCredit(), response.credit());
        assertEquals(entity.getVolumeHoraireTotal(), response.volumeHoraireTotal());
        assertEquals(entity.getDescription(), response.description());
        assertEquals(entity.getCouleur(), response.couleur());
    }

    @Test
    void shouldMapJpaUeEntityListToUeResponseDTOList() {
        JpaUeEntity entity = new JpaUeEntity();
        entity.setId(1L);
        entity.setLibelle("Maths");
        List<JpaUeEntity> entities = List.of(entity);

        // Map to Domain list first then to DTO list
        List<Ue> domainList = entities.stream().map(ueMapper::jpaUeEntityToUe).toList();
        List<UeResponseDTO> responses = ueMapper.toResponseDTOList(domainList);

        assertEquals(1, responses.size());
        assertEquals(entity.getId(), responses.get(0).id());
    }

    @Test
    void shouldMapCreateUeRequestDTOToJpaUeEntity() {
        CreateUeRequestDTO request = new CreateUeRequestDTO("Maths", "MATH101", 6, 60, "Description", "#ffffff", 10L, 1, Set.of(1L, 2L));
        JpaUeEntity entity = ueMapper.toEntity(request);

        assertNull(entity.getId());
        assertEquals(request.libelle(), entity.getLibelle());
        assertEquals(request.code(), entity.getCode());
        assertEquals(request.couleur(), entity.getCouleur());
        assertEquals(request.specialiteId(), entity.getSpecialiteId());
    }

    @Test
    void shouldUpdateJpaUeEntityFromUpdateUeRequestDTO() {
        UpdateUeRequestDTO request = new UpdateUeRequestDTO("Maths Updated", "MATH101", 8, 80, "Description Updated", "#ff0000", 11L, 2, new java.util.HashSet<>());
        JpaUeEntity entity = new JpaUeEntity();
        entity.setId(1L);

        ueMapper.updateEntityFromDTO(request, entity);

        assertEquals(1L, entity.getId());
        assertEquals(request.libelle(), entity.getLibelle());
        assertEquals(request.couleur(), entity.getCouleur());
        assertEquals(request.specialiteId(), entity.getSpecialiteId());
    }
}
