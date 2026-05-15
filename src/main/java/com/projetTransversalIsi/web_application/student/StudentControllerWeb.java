package com.projetTransversalIsi.web_application.student;

import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.domain.enums.UserStatus;
import com.projetTransversalIsi.user.dto.ProfileResponseDTO;
import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentControllerWeb {

    private final com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository userRepository;
    private final com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository studentProfileRepo;

    @GetMapping("/dashboard")
    public String dashboardView(Model model) {
        model.addAttribute("student", getFakeStudent());
        return "StudentInterface/StudentDashboard";
    }

    @GetMapping("/schedule")
    public String scheduleView(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                studentProfileRepo.findById(user.getProfile().getId()).ifPresent(sp -> {
                    if (sp.getClasse() != null) {
                        model.addAttribute("classId",   sp.getClasse().getId());
                        model.addAttribute("className", sp.getClasse().getCode());
                    }
                });
            }
        }
        return "StudentInterface/StudentSchedule";
    }

    @GetMapping("/attendance")
    public String attendanceView(Model model) {
        model.addAttribute("student", getFakeStudent());
        return "StudentInterface/StudentAttendance";
    }

    @GetMapping("/absences")
    public String absencesView(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                var profile = user.getProfile();
                model.addAttribute("student", new UserDetailsResponseDTO(
                        user.getId(),
                        user.getStatus(),
                        user.getEmail(),
                        "STUDENT",
                        ProfileResponseDTO.builder()
                                .id(profile.getId())
                                .nom(profile.getNom())
                                .prenom(profile.getPrenom())
                                .build()
                ));
            }
        } else {
            model.addAttribute("student", getFakeStudent());
        }
        return "StudentInterface/StudentAbsenceJustification";
    }

    private UserDetailsResponseDTO getFakeStudent() {
        return new UserDetailsResponseDTO(
                1L,
                UserStatus.ACTIVE,
                "etudiant@kemoschool.com",
                "STUDENT",
                ProfileResponseDTO.builder()
                        .id(201L)
                        .nom("Dupont")
                        .prenom("Jean")
                        .matricule("23L3I001")
                        .numeroTelephone("677777777")
                        .build()
        );
    }

    public record AbsenceViewModel(
            Long id,
            String subject,
            LocalDate date,
            String status,
            String reason
    ) {}
}
