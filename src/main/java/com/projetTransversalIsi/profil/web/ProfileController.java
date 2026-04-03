package com.projetTransversalIsi.profil.web;

import com.projetTransversalIsi.profil.application.use_cases.UploadProfilePhotoUC;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/profils")
@RequiredArgsConstructor
public class ProfileController {

    private final UploadProfilePhotoUC uploadProfilePhotoUC;

    @PostMapping("/{id}/photo")
    public ResponseEntity<Map<String, String>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("photo") MultipartFile photo) {
        String photoUrl = uploadProfilePhotoUC.execute(id, photo);
        return ResponseEntity.ok(Map.of("photoUrl", photoUrl));
    }
}
