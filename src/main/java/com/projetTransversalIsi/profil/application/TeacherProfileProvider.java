package com.projetTransversalIsi.profil.application;


import com.projetTransversalIsi.profil.domain.*;
import org.springframework.stereotype.Component;

@Component
public class TeacherProfileProvider implements ProfileProvider {
        @Override
        public boolean supports(String roleName){return "TEACHER".equals(roleName);}

        @Override
        public Profile create(ProfilCreationDTO profileJson){
                TeacherProfile profile = new TeacherProfile();
                profile.setMatricule(profileJson.getMatricule());
                profile.setNom(profileJson.getNom());
                profile.setPrenom(profileJson.getPrenom());
                profile.setNumeroTelephone(profileJson.getNumeroTelephone());
                return profile;
        }
}
