package com.projetTransversalIsi.pedagogie.infrastructure;

import com.projetTransversalIsi.pedagogie.domain.model.Semestre;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaSemestreEntity;
import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SemestreMapper {

    Semestre toDomain(JpaSemestreEntity entity);

    JpaSemestreEntity toEntity(Semestre semestre);

    SemestreResponseDTO toResponseDTO(Semestre semestre);

    List<SemestreResponseDTO> toResponseDTOList(List<Semestre> semestres);
}