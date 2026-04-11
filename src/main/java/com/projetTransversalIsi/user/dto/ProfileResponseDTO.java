package com.projetTransversalIsi.user.dto;

import com.projetTransversalIsi.user.profil.domain.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponseDTO {
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String numeroTelephone;
    private String photoUrl;
    
    // Student specific
    private Long classeId;
    
    // Teacher specific
    private String titre;
    private String specialite;
    private String type;

    public static ProfileResponseDTO fromDomain(Profile profile) {
        if (profile == null) return null;
        
        ProfileResponseDTOBuilder builder = ProfileResponseDTO.builder()
                .id(profile.getId())
                .matricule(profile.getMatricule())
                .nom(profile.getNom())
                .prenom(profile.getPrenom())
                .numeroTelephone(profile.getNumeroTelephone())
                .photoUrl(profile.getPhotoUrl());
        
        if (profile instanceof StudentProfile student) {
            builder.classeId(student.getClasseId());
        } else if (profile instanceof TeacherProfile teacher) {
            builder.titre(teacher.getTitre());
            builder.specialite(teacher.getSpecialite());
            builder.type(teacher.getType());
        }
        
        return builder.build();
    }
}
