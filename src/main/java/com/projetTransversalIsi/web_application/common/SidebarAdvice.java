package com.projetTransversalIsi.web_application.common;

import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Injects sidebar user information into every Thymeleaf model automatically.
 * This ensures role-based nav filtering and profile display happen server-side.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class SidebarAdvice {

    private final HttpServletRequest request;
    private final SpringDataUserRepository userRepository;

    @ModelAttribute
    public void addSidebarUser(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        // Derive activePage from request URI if not already set
        if (!model.containsAttribute("activePage")) {
            String uri = request.getRequestURI();
            String activePage = deriveActivePage(uri);
            model.addAttribute("activePage", activePage);
        }

        if (principal == null) {
            model.addAttribute("sidebarRole", null);
            model.addAttribute("sidebarEmail", null);
            model.addAttribute("sidebarFullName", "Invité");
            model.addAttribute("sidebarPhotoUrl", null);
            return;
        }

        model.addAttribute("sidebarRole", principal.role());
        model.addAttribute("sidebarEmail", principal.email());
        model.addAttribute("sidebarFullName", principal.email()); // Default

        try {
            userRepository.findById(principal.userId()).ifPresent(user -> {
                JpaProfileEntity profile = user.getProfile();
                if (profile != null) {
                    String fullName = Stream.of(profile.getNom(), profile.getPrenom())
                            .filter(s -> s != null && !s.isBlank())
                            .collect(Collectors.joining(" "));
                    if (!fullName.isBlank()) {
                        model.addAttribute("sidebarFullName", fullName);
                    }
                    model.addAttribute("sidebarPhotoUrl", profile.getPhotoUrl());
                }
            });
        } catch (Exception e) {
            // Log error or ignore
        }
    }

    private String deriveActivePage(String uri) {
        if (uri == null || uri.isEmpty() || uri.equals("/")) return "dashboard";
        
        // Remove trailing slashes and split
        String path = uri.startsWith("/") ? uri.substring(1) : uri;
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        
        String[] parts = path.split("/");
        if (parts.length == 0) return "dashboard";
        
        // Strategy: take the last part for simple routes, or the second if it starts with role
        // e.g. /admin/users -> users
        // e.g. /teacher/schedule -> schedule
        // e.g. /ap/subjects -> subjects
        if (parts.length >= 2) {
            return parts[1];
        }
        
        return parts[0];
    }
}
