package com.projetTransversalIsi.pedagogie.infrastructure;


import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireResponseDTO;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaAnneeScolaireEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnneeScolaireMapper {
    AnneeScolaire toDomainModel(JpaAnneeScolaireEntity entity);
    JpaAnneeScolaireEntity toJpaEntity(AnneeScolaire model);

    CreateAnneeScolaireResponseDTO toResponseDto(AnneeScolaire annee);
    AnneeScolaire toJpaEntity(CreateAnneeScolaireRequestDTO command);
}
