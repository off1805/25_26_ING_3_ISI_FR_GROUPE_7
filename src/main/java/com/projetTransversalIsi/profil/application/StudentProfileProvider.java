package com.projetTransversalIsi.profil.application;

import com.google.gson.Gson;
import com.projetTransversalIsi.profil.domain.AdminProfile;
import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.domain.ProfileProvider;
import com.projetTransversalIsi.profil.domain.StudentProfile;
import org.springframework.stereotype.Component;


@Component
public class StudentProfileProvider implements ProfileProvider {

    @Override
    public boolean supports(String roleName){return "STUDENT".equals(roleName);}

    @Override
    public Profile create(ProfilCreationDTO profileJson){
        StudentProfile profile = new StudentProfile();
        profile.setMatricule(profileJson.getMatricule());
        profile.setNom(profileJson.getNom());
        profile.setPrenom(profileJson.getPrenom());
        profile.setNumeroTelephone(profileJson.getNumeroTelephone());
        return profile;
    }
}
