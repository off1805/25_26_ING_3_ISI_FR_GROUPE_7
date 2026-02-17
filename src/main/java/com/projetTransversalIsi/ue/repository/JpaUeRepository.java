package com.projetTransversalIsi.ue.repository;

import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.domain.UeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaUeRepository implements UeRepository {

    private final SpringDataUeRepository ueRepo;

    @Override
    public Ue save(){
        return new Ue()     ;
    }
}
