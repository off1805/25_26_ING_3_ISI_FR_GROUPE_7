package com.projetTransversalIsi.profil.application;

import com.projetTransversalIsi.profil.domain.*;
import com.projetTransversalIsi.security.domain.EnumRole;
import org.springframework.stereotype.Component;

@Component
public class APProfileProvider implements ProfileProvider {
    @Override
    public boolean supports(String roleName){return EnumRole.AP.name().equals(roleName);}

    @Override
    public Profile create(){return new APProfile();
    }
}
