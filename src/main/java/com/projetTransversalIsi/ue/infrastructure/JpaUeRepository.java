package com.projetTransversalIsi.ue.infrastructure;

import com.projetTransversalIsi.profil.infrastructure.JpaTeacherProfileEntity;
import com.projetTransversalIsi.profil.infrastructure.SpringDataTeacherProfileRepository;
import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.domain.UeRepository;
import com.projetTransversalIsi.ue.application.dto.UeFiltreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaUeRepository implements UeRepository {

    private final SpringDataUeRepository springDataUeRepository;
    private final SpringDataTeacherProfileRepository teacherProfileRepository;
    private final UeMapper ueMapper;

    @Override
    public boolean ueAlreadyExists(String code) {
        return springDataUeRepository.existsByCode(code);
    }

    @Override
    public Ue save(Ue ue) {
        JpaUeEntity entity = ueMapper.ueToJpaUeEntity(ue);

        // résoudre les enseignants depuis leurs ids
        if (ue.getEnseignantIds() != null && !ue.getEnseignantIds().isEmpty()) {
            Set<JpaTeacherProfileEntity> enseignants = new HashSet<>(
                    teacherProfileRepository.findAllById(ue.getEnseignantIds())
            );
            entity.setEnseignants(enseignants);
        } else {
            entity.setEnseignants(new HashSet<>());
        }

        // conserver l'id pour les updates
        if (ue.getId() != null) {
            entity.setId(ue.getId());
        }

        JpaUeEntity saved = springDataUeRepository.save(entity);
        log.info("UE saved with id: {}", saved.getId());
        return ueMapper.jpaUeEntityToUe(saved);
    }

    @Override
    public Optional<Ue> findById(Long id) {
        return springDataUeRepository.findById(id).map(ueMapper::jpaUeEntityToUe);
    }

    @Override
    public void delete(Ue ue) {
        JpaUeEntity entity = ueMapper.ueToJpaUeEntity(ue);
        if (ue.getId() != null) {
            entity.setId(ue.getId());
        }
        springDataUeRepository.save(entity);
        log.info("UE soft-deleted with id: {}", ue.getId());
    }

    @Override
    public Page<Ue> findAll(UeFiltreDto command, Pageable pageable) {
        Specification<JpaUeEntity> spec = Specification
                .where(UeSpec.hasLibelle(command.getLibelle()))
                .and(UeSpec.hasCode(command.getCode()))
                .and(UeSpec.hasSpecialiteId(command.getSpecialiteId()))
                .and(UeSpec.isDeleted(command.getDeleted()));

        return springDataUeRepository.findAll(spec, pageable).map(ueMapper::jpaUeEntityToUe);
    }
}
