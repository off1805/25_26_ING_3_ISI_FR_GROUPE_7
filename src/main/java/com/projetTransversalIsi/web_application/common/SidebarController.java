package com.projetTransversalIsi.web_application.common;

import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides the sidebar as a pre-formatted HTML fragment via /api/sidebar.
 * The client fetches this on page load and injects it into the DOM.
 */
@Controller
@RequiredArgsConstructor
public class SidebarController {

    private final SpringDataUserRepository userRepository;

    /**
     * Returns the rendered sidebar HTML fragment for the authenticated user.
     *
     * @param principal       the authenticated user (from JWT)
     * @param activePage      the current page identifier used to highlight the active nav item
     * @param subjectsFiliereId optional filière ID needed for AP subjects link
     */
    @GetMapping(value = "/api/sidebar", produces = MediaType.TEXT_HTML_VALUE)
    public String sidebarHtml(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "") String activePage,
            Model model) {

        if (principal == null) {
            // Return an empty sidebar (unauthenticated) — client will redirect if needed
            model.addAttribute("sidebarRole", null);
            model.addAttribute("sidebarEmail", null);
            model.addAttribute("sidebarFullName", "Utilisateur");
            model.addAttribute("sidebarPhotoUrl", null);
        } else {
            model.addAttribute("sidebarRole", principal.role());
            model.addAttribute("sidebarEmail", principal.email());

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

        model.addAttribute("activePage", activePage);

        return "common/SidebarFragment";
    }
}
