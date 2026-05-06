package com.projetTransversalIsi.web_application.common;

import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Injects sidebar user information into every Thymeleaf model automatically.
 * This ensures role-based nav filtering and profile display happen server-side.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class SidebarAdvice {

    private final SpringDataUserRepository userRepository;

    @ModelAttribute
    public void addSidebarUser(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        if (principal == null) {
            model.addAttribute("sidebarRole", null);
            model.addAttribute("sidebarEmail", null);
            model.addAttribute("sidebarFullName", null);
            model.addAttribute("sidebarPhotoUrl", null);
            return;
        }

        model.addAttribute("sidebarRole", principal.role());
        model.addAttribute("sidebarEmail", principal.email());

        // Try to load the profile for name and photo
        try {
            JpaUserEntity user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null) {
                JpaProfileEntity profile = user.getProfile();
                String fullName = Stream.of(
                                profile != null ? profile.getNom() : null,
                                profile != null ? profile.getPrenom() : null)
                        .filter(s -> s != null && !s.isBlank())
                        .collect(Collectors.joining(" "));
                model.addAttribute("sidebarFullName", fullName.isBlank() ? principal.email() : fullName);
                model.addAttribute("sidebarPhotoUrl", profile != null ? profile.getPhotoUrl() : null);
            } else {
                model.addAttribute("sidebarFullName", principal.email());
                model.addAttribute("sidebarPhotoUrl", null);
            }
        } catch (Exception e) {
            model.addAttribute("sidebarFullName", principal.email());
            model.addAttribute("sidebarPhotoUrl", null);
        }
    }
}
