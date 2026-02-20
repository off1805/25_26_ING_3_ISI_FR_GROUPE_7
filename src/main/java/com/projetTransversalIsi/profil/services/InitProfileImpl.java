package com.projetTransversalIsi.profil.services;


import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.infrastructure.JpaProfileEntity;
import com.projetTransversalIsi.profil.infrastructure.JpaProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitProfileImpl<T extends Profile,E extends JpaProfileEntity> implements InitProfile<T>{

    private final JpaProfileRepository<T,E> jpaProfileRepo;



    @Override
    public T execute(T profile){
        return (T) jpaProfileRepo.save(profile);

    }



}
