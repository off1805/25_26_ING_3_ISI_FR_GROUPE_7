package com.projetTransversalIsi.profil.services;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.domain.StudentProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class InitProfileForNewUserAccessPortImpl implements InitProfileForNewUserAccessPort{



    @Override
    public Profile execute(Long id){
        return new StudentProfile();
    }
}
