package com.projetTransversalIsi.profil.application;

import com.projetTransversalIsi.profil.domain.AdminProfile;
import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.domain.ProfileProvider;
import com.projetTransversalIsi.profil.domain.StudentProfile;
import com.projetTransversalIsi.security.domain.EnumRole;
import org.springframework.stereotype.Component;

@Component
public class AdminProfileProvider implements ProfileProvider {
    @Override
    public boolean supports(String roleName){return EnumRole.ADMIN.name().equals(roleName);}

    @Override
    public Profile create(){return new AdminProfile();
    }
}
