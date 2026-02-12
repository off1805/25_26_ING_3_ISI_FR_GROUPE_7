package com.projetTransversalIsi.profil.application;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.domain.ProfileProvider;
import com.projetTransversalIsi.profil.domain.StudentProfile;
import com.projetTransversalIsi.profil.domain.SurveillantProfile;
import com.projetTransversalIsi.security.domain.EnumRole;
import org.springframework.stereotype.Component;

@Component
public class SurveillantProfileProvider implements ProfileProvider {

    @Override
    public boolean supports(String roleName){return EnumRole.SURVEILLANT.name().equals(roleName);}

    @Override
    public Profile create(){return new SurveillantProfile();
    }
}
