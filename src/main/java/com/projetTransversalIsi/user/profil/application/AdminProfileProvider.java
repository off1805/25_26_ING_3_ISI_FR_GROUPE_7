package com.projetTransversalIsi.user.profil.application;

import com.google.gson.Gson;
import com.projetTransversalIsi.user.profil.domain.AdminProfile;
import com.projetTransversalIsi.user.profil.domain.Profile;
import com.projetTransversalIsi.user.profil.domain.ProfileProvider;
import com.projetTransversalIsi.user.profil.domain.StudentProfile;
import com.projetTransversalIsi.security.domain.EnumRole;
import org.springframework.stereotype.Component;

@Component
public class AdminProfileProvider implements ProfileProvider {
    @Override
    public boolean supports(String roleName){return EnumRole.ADMIN.name().equals(roleName);}

    @Override
    public Profile create(ProfilCreationDTO profileJson){

        AdminProfile profile = new AdminProfile();
        profile.setMatricule(profileJson.getMatricule());
        profile.setNom(profileJson.getNom());
        profile.setPrenom(profileJson.getPrenom());
        profile.setNumeroTelephone(profileJson.getNumeroTelephone());
        return profile;
    }
}
