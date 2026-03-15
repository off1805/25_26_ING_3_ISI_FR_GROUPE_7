package com.projetTransversalIsi.ue.infrastructure;

import com.projetTransversalIsi.ue.application.dto.CreateUeRequestDTO;
import com.projetTransversalIsi.ue.application.dto.UeResponseDTO;
import com.projetTransversalIsi.ue.application.dto.UpdateUeRequestDTO;
import com.projetTransversalIsi.ue.domain.Ue;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UeMapperTest {

    private final UeMapper ueMapper = Mappers.getMapper(UeMapper.class);

    @Test
    void shouldMapUeToJpaUeEntity() {
        Ue ue = new Ue(1L, "Maths", "MATH101", 6, 60, "Description", "#ffffff", null, null, false);
        JpaUeEntity entity = ueMapper.ueToJpaUeEntity(ue);

        assertEquals(ue.getId(), entity.getId());
        assertEquals(ue.getLibelle(), entity.getLibelle());
        assertEquals(ue.getCode(), entity.getCode());
        assertEquals(ue.getCredit(), entity.getCredit());
        assertEquals(ue.getVolumeHoraireTotal(), entity.getVolumeHoraireTotal());
        assertEquals(ue.getDescription(), entity.getDescription());
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
        entity.setCouleur("#ffffff");

        Ue ue = ueMapper.jpaUeEntityToUe(entity);

        assertEquals(entity.getId(), ue.getId());
        assertEquals(entity.getLibelle(), ue.getLibelle());
        assertEquals(entity.getCode(), ue.getCode());
        assertEquals(entity.getCredit(), ue.getCredit());
        assertEquals(entity.getVolumeHoraireTotal(), ue.getVolumeHoraireTotal());
        assertEquals(entity.getDescription(), ue.getDescription());
        assertEquals(entity.getCouleur(), ue.getCouleur());
    }

    @Test
    void shouldMapCreateUeRequestDTOToUe() {
        CreateUeRequestDTO request = new CreateUeRequestDTO("Maths", "MATH101", 6, 60, "Description", "#ffffff");
        Ue ue = ueMapper.toDomain(request);

        assertNull(ue.getId());
        assertEquals(request.libelle(), ue.getLibelle());
        assertEquals(request.code(), ue.getCode());
        assertEquals(request.credit(), ue.getCredit());
        assertEquals(request.volumeHoraireTotal(), ue.getVolumeHoraireTotal());
        assertEquals(request.description(), ue.getDescription());
        assertEquals(request.couleur(), ue.getCouleur());
    }

    @Test
    void shouldUpdateUeFromUpdateUeRequestDTO() {
        UpdateUeRequestDTO request = new UpdateUeRequestDTO("Maths Updated", "MATH101", 8, 80, "Description Updated", "#ff0000");
        Ue ue = new Ue(1L, "Maths", "MATH101", 6, 60, "Description", "#ffffff", null, null, false);
        
        ueMapper.updateFromDTO(request, ue);

        assertEquals(1L, ue.getId());
        assertEquals(request.libelle(), ue.getLibelle());
        assertEquals(request.code(), ue.getCode());
        assertEquals(request.credit(), ue.getCredit());
        assertEquals(request.volumeHoraireTotal(), ue.getVolumeHoraireTotal());
        assertEquals(request.description(), ue.getDescription());
        assertEquals(request.couleur(), ue.getCouleur());
    }

    @Test
    void shouldMapUeToUeResponseDTO() {
        Ue ue = new Ue(1L, "Maths", "MATH101", 6, 60, "Description", "#ffffff", null, null, false);
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
        entity.setCouleur("#ffffff");

        UeResponseDTO response = ueMapper.toResponseDTO(entity);

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

        List<UeResponseDTO> responses = ueMapper.jpaEntityListToResponseDTOList(entities);

        assertEquals(1, responses.size());
        assertEquals(entity.getId(), responses.get(0).id());
    }

    @Test
    void shouldMapCreateUeRequestDTOToJpaUeEntity() {
        CreateUeRequestDTO request = new CreateUeRequestDTO("Maths", "MATH101", 6, 60, "Description", "#ffffff");
        JpaUeEntity entity = ueMapper.toEntity(request);

        assertNull(entity.getId());
        assertEquals(request.libelle(), entity.getLibelle());
        assertEquals(request.code(), entity.getCode());
        assertEquals(request.couleur(), entity.getCouleur());
    }

    @Test
    void shouldUpdateJpaUeEntityFromUpdateUeRequestDTO() {
        UpdateUeRequestDTO request = new UpdateUeRequestDTO("Maths Updated", "MATH101", 8, 80, "Description Updated", "#ff0000");
        JpaUeEntity entity = new JpaUeEntity();
        entity.setId(1L);

        ueMapper.updateEntityFromDTO(request, entity);

        assertEquals(1L, entity.getId());
        assertEquals(request.libelle(), entity.getLibelle());
        assertEquals(request.couleur(), entity.getCouleur());
    }

    @Test
    void shouldMapUeResponseDTOToUe() {
        UeResponseDTO response = new UeResponseDTO(1L, "Maths", "MATH101", 6, 60, "Description", "#ffffff");
        Ue ue = ueMapper.responseDTOToUe(response);

        assertEquals(response.id(), ue.getId());
        assertEquals(response.libelle(), ue.getLibelle());
        assertEquals(response.couleur(), ue.getCouleur());
    }

    @Test
    void shouldMapUeResponseDTOToJpaUeEntity() {
        UeResponseDTO response = new UeResponseDTO(1L, "Maths", "MATH101", 6, 60, "Description", "#ffffff");
        JpaUeEntity entity = ueMapper.responseDTOToJpaUeEntity(response);

        assertEquals(response.id(), entity.getId());
        assertEquals(response.libelle(), entity.getLibelle());
        assertEquals(response.couleur(), entity.getCouleur());
    }
}
