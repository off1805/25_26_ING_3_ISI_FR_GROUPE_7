package com.projetTransversalIsi.user.profil.application;

import com.google.gson.Gson;
import com.projetTransversalIsi.user.profil.domain.*;
import com.projetTransversalIsi.security.domain.EnumRole;
import org.springframework.stereotype.Component;

@Component
public class APProfileProvider implements ProfileProvider {
    @Override
    public boolean supports(String roleName){return EnumRole.AP.name().equals(roleName);}

    @Override
    public Profile create(ProfilCreationDTO profileJson){
        APProfile profile = new APProfile();
        profile.setMatricule(profileJson.getMatricule());
        profile.setNom(profileJson.getNom());
        profile.setPrenom(profileJson.getPrenom());
        profile.setNumeroTelephone(profileJson.getNumeroTelephone());
        return profile;
    }
}
