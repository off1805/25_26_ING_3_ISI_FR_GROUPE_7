package com.projetTransversalIsi.user.profil.infrastructure;

import com.projetTransversalIsi.user.profil.application.dto.FiltreProfil;
import com.projetTransversalIsi.user.profil.domain.Profile;
import com.projetTransversalIsi.user.profil.domain.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@RequiredArgsConstructor
@Repository
public class JpaProfileRepository<T extends Profile,E extends JpaProfileEntity> implements ProfileRepository<T> {

    private final SpringDataProfileRepository<E> sprgDataProfileRepo;
    private final ProfileMapper profileMapper;

    @Override
    public T save(T profile){

        E entityToSave = (E) profileMapper.profiletoJpaProfileEntity(profile);

        if(entityToSave == null) throw new ClassCastException("Casting in JpaProfileRepository lost");
        E savedEntity = sprgDataProfileRepo.save(entityToSave);

        return (T) profileMapper.jpaProfileEntitytoDomain(savedEntity);
    }


}
