package com.projetTransversalIsi.emploi_temps.application;

import com.projetTransversalIsi.emploi_temps.application.dto.PresenceNotificationDTO;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresenceNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final SpringDataStudentProfileRepository profileRepo;

    public void notifyStudentPresent(Long etudiantId, Long presenceListId) {
        JpaStudentProfileEntity profile = profileRepo.findById(etudiantId).orElse(null);
        String nom     = profile != null ? profile.getNom()    : "—";
        String prenom  = profile != null ? profile.getPrenom() : "";
        String photoUrl = profile != null ? profile.getPhotoUrl() : null;

        messagingTemplate.convertAndSend(
                "/topic/presences/" + presenceListId,
                new PresenceNotificationDTO(etudiantId, nom, prenom, photoUrl, presenceListId)
        );
    }
}
