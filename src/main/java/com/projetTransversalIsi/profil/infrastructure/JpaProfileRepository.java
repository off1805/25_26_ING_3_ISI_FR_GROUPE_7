package com.projetTransversalIsi.profil.infrastructure;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.domain.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


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
