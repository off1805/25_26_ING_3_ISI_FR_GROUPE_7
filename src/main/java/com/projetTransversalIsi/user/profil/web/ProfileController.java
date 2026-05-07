package com.projetTransversalIsi.user.profil.web;

import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.application.use_cases.UploadProfilePhotoUC;
import com.projetTransversalIsi.user.profil.infrastructure.JpaProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/profils")
@RequiredArgsConstructor
public class ProfileController {

    private final UploadProfilePhotoUC uploadProfilePhotoUC;
    private final SpringDataUserRepository userRepository;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null || principal.userId() == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = Objects.requireNonNull(principal.userId());
        JpaUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));

        JpaProfileEntity profile = user.getProfile();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId",    principal.userId());
        data.put("email",     principal.email());
        data.put("role",      principal.role());
        data.put("profileId", profile != null ? profile.getId()               : null);
        data.put("nom",       profile != null ? profile.getNom()               : null);
        data.put("prenom",    profile != null ? profile.getPrenom()            : null);
        data.put("matricule", profile != null ? profile.getMatricule()         : null);
        data.put("telephone", profile != null ? profile.getNumeroTelephone()   : null);
        data.put("photoUrl",  profile != null ? profile.getPhotoUrl()          : null);

        return ResponseEntity.ok(data);
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<Map<String, String>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("photo") MultipartFile photo) {
        String photoUrl = uploadProfilePhotoUC.execute(id, photo);
        return ResponseEntity.ok(Map.of("photoUrl", photoUrl));
    }
}
