package com.projetTransversalIsi.profil.application.use_cases;

import org.springframework.web.multipart.MultipartFile;

public interface UploadProfilePhotoUC {
    String execute(Long profileId, MultipartFile photo);
}
