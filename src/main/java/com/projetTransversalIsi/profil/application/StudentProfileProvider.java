package com.projetTransversalIsi.profil.application;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.domain.ProfileProvider;
import com.projetTransversalIsi.profil.domain.StudentProfile;
import org.springframework.stereotype.Component;


@Component
public class StudentProfileProvider implements ProfileProvider {

    @Override
    public boolean supports(String roleName){return "STUDENT".equals(roleName);}

    @Override
    public Profile create(){return new StudentProfile();
    }
}
