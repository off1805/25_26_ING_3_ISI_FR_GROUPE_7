package com.projetTransversalIsi.profil.application;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.domain.ProfileProvider;
import com.projetTransversalIsi.profil.domain.StudentProfile;
import com.projetTransversalIsi.profil.domain.TeacherProfile;
import org.springframework.stereotype.Component;

@Component
public class TeacherProfileProvider implements ProfileProvider {
        @Override
        public boolean supports(String roleName){return "TEACHER".equals(roleName);}

        @Override
        public Profile create(){return new TeacherProfile();
        }
}
