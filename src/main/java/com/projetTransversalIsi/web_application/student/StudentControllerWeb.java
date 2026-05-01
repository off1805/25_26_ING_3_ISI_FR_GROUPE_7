package com.projetTransversalIsi.web_application.student;

import com.projetTransversalIsi.user.domain.enums.UserStatus;
import com.projetTransversalIsi.user.dto.ProfileResponseDTO;
import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentControllerWeb {

    @GetMapping("/dashboard")
    public String dashboardView(Model model) {
        UserDetailsResponseDTO student = getFakeStudent();
        model.addAttribute("student", student);
        return "StudentInterface/StudentDashboard";
    }

    @GetMapping("/schedule")
    public String scheduleView(Model model) {
        UserDetailsResponseDTO student = getFakeStudent();
        model.addAttribute("student", student);
        return "StudentInterface/StudentSchedule";
    }

    @GetMapping("/absences")
    public String absencesView(Model model) {
        UserDetailsResponseDTO student = getFakeStudent();
        List<AbsenceViewModel> absences = getFakeAbsences();
        
        model.addAttribute("student", student);
        model.addAttribute("absences", absences);
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

    private List<AbsenceViewModel> getFakeAbsences() {
        return List.of(
                new AbsenceViewModel(
                        1L,
                        "Mathématiques - Advanced Calculus II",
                        LocalDate.of(2024, 9, 23),
                        "PENDING",
                        "Absence justifiée"
                ),
                new AbsenceViewModel(
                        2L,
                        "Advanced Calculus II",
                        LocalDate.of(2024, 9, 20),
                        "PENDING",
                        "En attente d'approbation"
                ),
                new AbsenceViewModel(
                        3L,
                        "Approved Biologie Lab",
                        LocalDate.of(2024, 9, 15),
                        "APPROVED",
                        "Justificatif approuvé"
                ),
                new AbsenceViewModel(
                        4L,
                        "World History",
                        LocalDate.of(2024, 9, 10),
                        "REJECTED",
                        "Justificatif invalide"
                )
        );
    }

    public record AbsenceViewModel(
            Long id,
            String subject,
            LocalDate date,
            String status,
            String reason
    ) {
    }
}

