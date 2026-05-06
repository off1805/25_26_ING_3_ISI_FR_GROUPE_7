package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;

import com.projetTransversalIsi.pedagogie.application.dto.OffreUeFiltreDto;
import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import com.projetTransversalIsi.pedagogie.infrastructure.OffreUeMapper;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaAnneeScolaireEntity;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaOffreUeEntity;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaUeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaOffreUeRepository implements OffreUeRepository {

    private final SpringDataOffreUeRepository springDataOffreUeRepository;
    private final SpringDataUeRepository springDataUeRepository;
    private final SpringDataAnneeScolaireRepository springDataAnneeScolaireRepository;
    private final OffreUeMapper offreUeMapper;

    @Override
    public boolean offreUeAlreadyExists(Long ueId, Long anneeScolaireId) {
        return springDataOffreUeRepository.existsByUe_IdAndAnneeScolaire_Id(ueId, anneeScolaireId);
    }

    @Override
    public OffreUe save(OffreUe offreUe) {
        JpaOffreUeEntity entity;

        if (offreUe.getId() != null) {
            // UPDATE : charger l'entité existante et mettre à jour uniquement les champs métier
            entity = springDataOffreUeRepository.findById(offreUe.getId())
                    .orElseThrow(() -> new RuntimeException("OffreUe introuvable : " + offreUe.getId()));
            entity.setLibelle(offreUe.getLibelle());
            entity.setCode(offreUe.getCode());
            entity.setCredit(offreUe.getCredit());
            entity.setVolumeHoraireTotal(offreUe.getVolumeHoraireTotal());
            entity.setDescription(offreUe.getDescription());
            entity.setCouleur(offreUe.getCouleur());
            entity.setSemestre(offreUe.getSemestre());
            entity.setSpecialiteId(offreUe.getSpecialiteId());
        } else {
            // CREATE : mapper l'entité complète et résoudre les associations JPA
            JpaUeEntity ueEntity = springDataUeRepository.findById(offreUe.getUeId())
                    .orElseThrow(() -> new RuntimeException("UE introuvable : " + offreUe.getUeId()));
            JpaAnneeScolaireEntity anneeEntity = springDataAnneeScolaireRepository
                    .findById(offreUe.getAnneeScolaireId())
                    .orElseThrow(() -> new RuntimeException("Année scolaire introuvable : " + offreUe.getAnneeScolaireId()));
            entity = offreUeMapper.toEntity(offreUe);
            entity.setUe(ueEntity);
            entity.setAnneeScolaire(anneeEntity);
        }

        return offreUeMapper.toDomain(springDataOffreUeRepository.save(entity));
    }

    @Override
    public Optional<OffreUe> findById(Long id) {
        return springDataOffreUeRepository.findById(id)
                .map(offreUeMapper::toDomain);
    }

    @Override
    public void delete(OffreUe offreUe) {
        springDataOffreUeRepository.deleteById(offreUe.getId());
    }

    @Override
    public Page<OffreUe> findAll(Pageable pageable) {
        return springDataOffreUeRepository.findAll(pageable)
                .map(offreUeMapper::toDomain);
    }

    @Override
    public Page<OffreUe> findByAnneeScolaireId(Long anneeScolaireId, Pageable pageable) {
        return springDataOffreUeRepository
                .findByAnneeScolaire_Id(anneeScolaireId, pageable)
                .map(offreUeMapper::toDomain);
    }

    @Override
    public Page<OffreUe> findByUeId(Long ueId, Pageable pageable) {
        return springDataOffreUeRepository
                .findByUe_Id(ueId, pageable)
                .map(offreUeMapper::toDomain);
    }

    @Override
    public Page<OffreUe> findBySpecialiteIdAndAnneeScolaireId(Long specialiteId, Long anneeScolaireId, OffreUeFiltreDto filtre, Pageable pageable) {
        Specification<JpaOffreUeEntity> spec = Specification
                .where(JpaOffreUeSpec.hasSpecialiteId(specialiteId))
                .and(JpaOffreUeSpec.hasAnneeScolaireId(anneeScolaireId))
                .and(JpaOffreUeSpec.hasSemestre(filtre != null ? filtre.getSemestre() : null))
                .and(JpaOffreUeSpec.hasLibelleLike(filtre != null ? filtre.getLibelle() : null));
        return springDataOffreUeRepository.findAll(spec, pageable)
                .map(offreUeMapper::toDomain);
    }
}
