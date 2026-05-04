package com.projetTransversalIsi.web_application.profile;

import com.projetTransversalIsi.authentification.application.service.token.JwtService;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaProfileEntity;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class ProfileViewController {

    private final JwtService jwtService;
    private final SpringDataUserRepository userRepository;

    @GetMapping("/profile")
    public String profilePageGet() {
        return "redirect:/login";
    }

    @PostMapping("/profile")
    @Transactional(readOnly = true)
    public String profilePage(@RequestParam("token") String token, Model model) {
        try {
            Claims claims = jwtService.getClaimsFromJwt(token);
            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            JpaUserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));

            JpaProfileEntity profile = user.getProfile();

            String nom    = profile != null ? profile.getNom()    : null;
            String prenom = profile != null ? profile.getPrenom() : null;
            String fullName = Stream.of(nom, prenom)
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.joining(" "));

            model.addAttribute("email",     email   != null ? email   : "");
            model.addAttribute("role",      role    != null ? role    : "");
            model.addAttribute("profileId", profile != null ? profile.getId() : null);
            model.addAttribute("nom",       nom     != null ? nom     : "—");
            model.addAttribute("prenom",    prenom  != null ? prenom  : "—");
            model.addAttribute("matricule", profile != null && profile.getMatricule()       != null ? profile.getMatricule()       : "—");
            model.addAttribute("telephone", profile != null && profile.getNumeroTelephone() != null ? profile.getNumeroTelephone() : "—");
            model.addAttribute("photoUrl",  profile != null ? profile.getPhotoUrl() : null);
            model.addAttribute("fullName",  fullName.isBlank() ? "Nom Prénom" : fullName);
            model.addAttribute("initiale",  nom != null && !nom.isBlank() ? String.valueOf(nom.charAt(0)).toUpperCase() : "U");

        } catch (Exception e) {
            return "redirect:/login";
        }
        return "common/ProfilePage";
    }
}
