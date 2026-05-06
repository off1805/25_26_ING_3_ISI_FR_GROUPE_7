package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.pedagogie.application.dto.UeFiltreDto;
import com.projetTransversalIsi.pedagogie.application.use_cases.SearchUeUC;
import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.structure_academique.application.dto.FiliereResponseDTO;
import com.projetTransversalIsi.structure_academique.application.service.FiliereService;
import com.projetTransversalIsi.structure_academique.application.dto.NiveauResponseDTO;
import com.projetTransversalIsi.structure_academique.application.service.NiveauService;
import com.projetTransversalIsi.emploi_temps.application.dto.EmploiTempsResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.SearchEmploiTempsRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.service.EmploiTempsService;
import com.projetTransversalIsi.structure_academique.application.dto.ClasseResponseDTO;
import com.projetTransversalIsi.structure_academique.application.service.ClasseService;
import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.profil.infrastructure.JpaAPProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataAPProfileRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import com.projetTransversalIsi.user.services.FindUserByIdUC;
import com.projetTransversalIsi.structure_academique.application.dto.SpecialiteResponseDTO;
import com.projetTransversalIsi.structure_academique.application.service.SpecialiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Controller
@RequestMapping("/ap")
@RequiredArgsConstructor
public class APController {

    private final FiliereService filiereService;
    private final NiveauService niveauService;
    private final SpecialiteService specialiteService;
    private final ClasseService classeService;
    private final SpringDataStudentProfileRepository studentProfileRepository;
    private final SpringDataAPProfileRepository apProfileRepository;
    private final EmploiTempsService emploiTempsService;
    private final SearchUeUC searchUe;
    private final FindUserByIdUC findUser;

    private Long resolveApFiliereId(UserPrincipal principal) {
        if(principal == null) System.out.println("Principal is null in resolveApFiliereId");
        if (principal == null) return null;
        System.out.println("Resolving filiereId for userId: " + principal.userId());
        
        return apProfileRepository.findByUserId(principal.userId())
                .map(JpaAPProfileEntity::getFiliereId)
                .orElse(null);
    }

    // ── View-model records ────────────────────────────────────────────────────

    public record EtudiantVM(
            Long userId,
            String nom,
            String prenom,
            String email,
            String matricule,
            String telephone) {
    }

    public record ClasseAvecEtudiants(
            Long id,
            String code,
            List<EtudiantVM> students) {
        public int getStudentCount() {
            return students == null ? 0 : students.size();
        }
    }

    // ── Breadcrumb helpers ────────────────────────────────────────────────────

    private static String bcSanitizeLabel(String label) {
        if (label == null)
            return "";
        return label.replace('|', ' ').replace(':', ' ').trim();
    }

    private static String bcItem(String label, String href) {
        String safeLabel = bcSanitizeLabel(label);
        if (safeLabel.isBlank())
            return "";
        if (href == null || href.isBlank())
            return safeLabel;
        return safeLabel + ":" + href;
    }

    private static String bcJoin(String... items) {
        StringJoiner joiner = new StringJoiner("|");
        for (String item : items) {
            if (item != null && !item.isBlank())
                joiner.add(item);
        }
        return joiner.toString();
    }

    // ── Routes ────────────────────────────────────────────────────────────────

    @GetMapping("/subjects")
    public String subjectsView(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        Long id = resolveApFiliereId(principal);
        System.out.println("Resolved filiereId for AP: " + id); // Debug log
        FiliereResponseDTO filiere = id != null ? filiereService.getFiliereById(id) : null;
        model.addAttribute("filiere", filiere);

        List<NiveauResponseDTO> niveaux = id != null ? niveauService.getNiveauxByFiliereId(id) : new ArrayList<>();
        model.addAttribute("niveaux", niveaux);

        List<SpecialiteResponseDTO> allSpecialites = new ArrayList<>();
        if (!niveaux.isEmpty()) {
            allSpecialites.addAll(specialiteService.getSpecialitesByNiveauId(niveaux.get(0).id()));
        }
        model.addAttribute("specialites", allSpecialites);

        List<ClasseResponseDTO> allClasses = new ArrayList<>();
        for (SpecialiteResponseDTO s : allSpecialites) {
            if (s.id() != null) {
                allClasses.addAll(classeService.getClassesBySpecialiteId(s.id()));
            }
        }
        model.addAttribute("classes", allClasses);

        Page<Ue> ue = allSpecialites.isEmpty()
                ? Page.empty()
                : searchUe.execute(new UeFiltreDto(null, null, allSpecialites.get(0).id(), false),
                        PageRequest.of(0, 10, Sort.by("id").descending()));
        model.addAttribute(
                "apPageBreadcrumb",
                bcJoin(bcItem(filiere != null ? filiere.nom() : "Filiere", null)));
        model.addAttribute("activePage", "subjects");
        model.addAttribute("apName", "AP Name");
        model.addAttribute("ues", ue.stream().toList());
        return "APInterface/APSubjects";
    }

    @GetMapping("/schedule")
    public String scheduleView(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        Long filiereId = resolveApFiliereId(principal);
        model.addAttribute("filiereId", filiereId);

        List<NiveauResponseDTO> niveaux = niveauService.getNiveauxByFiliereId(filiereId);
        model.addAttribute("niveaux", niveaux);

        List<SpecialiteResponseDTO> allSpecialites = new ArrayList<>();
        if (niveaux != null) {
            for (NiveauResponseDTO n : niveaux) {
                if (n.id() != null) {
                    allSpecialites.addAll(specialiteService.getSpecialitesByNiveauId(n.id()));
                }
            }
        }

        model.addAttribute("specialites", allSpecialites);
        Map<Long, SpecialiteResponseDTO> specialiteMap = new LinkedHashMap<>();
        for (SpecialiteResponseDTO s : allSpecialites) {
            if (s.id() != null)
                specialiteMap.put(s.id(), s);
        }
        model.addAttribute("specialiteMap", specialiteMap);

        List<ClasseResponseDTO> allClasses = new ArrayList<>();
        for (SpecialiteResponseDTO s : allSpecialites) {
            if (s.id() != null) {
                allClasses.addAll(classeService.getClassesBySpecialiteId(s.id()));
            }
        }

        Map<Long, ClasseResponseDTO> dedupClasses = new LinkedHashMap<>();
        for (ClasseResponseDTO c : allClasses) {
            if (c.id() != null)
                dedupClasses.putIfAbsent(c.id(), c);
        }
        model.addAttribute("classes", new ArrayList<>(dedupClasses.values()));
        model.addAttribute("classMap", dedupClasses);

        LocalDate today = LocalDate.now();

        // Fetch ONGOING schedules for these classes using the new date filters
        // Ongoing: startDateBeforeOrEqual(today) AND endDateAfterOrEqual(today)
        SearchEmploiTempsRequestDTO searchRequestParams = new SearchEmploiTempsRequestDTO(
                null, null, null, false, null,
                null, null, today, today);

        Page<EmploiTempsResponseDTO> ongoingPage = emploiTempsService.searchEmploiTemps(
                searchRequestParams,
                PageRequest.of(0, 100, Sort.by("semaine").descending()));

        List<Long> classIds = dedupClasses.values().stream().map(ClasseResponseDTO::id).toList();
        List<EmploiTempsResponseDTO> ongoingSchedules = ongoingPage.getContent().stream()
                .filter(e -> classIds.contains(e.classeId()))
                .toList();

        model.addAttribute("ongoingSchedules", ongoingSchedules);
        model.addAttribute("today", today);

        model.addAttribute("activePage", "schedule");
        model.addAttribute("apName", "AP Name");
        return "APInterface/APSchedule";
    }

    @GetMapping("/schedule/edit")
    public String editScheduleView(
            @org.springframework.web.bind.annotation.RequestParam(value = "id", required = false) Long id,
            @org.springframework.web.bind.annotation.RequestParam(value = "classId", required = false) Long classId,
            @org.springframework.web.bind.annotation.RequestParam(value = "dateDebut", required = false) String dateDebut,
            @org.springframework.web.bind.annotation.RequestParam(value = "dateFin", required = false) String dateFin,
            @org.springframework.web.bind.annotation.RequestParam(value = "semaine", required = false) Integer semaine,
            Model model) {

        java.time.LocalDate start = null;
        java.time.LocalDate end = null;
        Integer week = semaine;
        Long cid = classId;

        if (id != null) {
            EmploiTempsResponseDTO emploi = emploiTempsService.getEmploiTempsById(id);
            if (emploi != null) {
                if (start == null)
                    start = emploi.dateDebut();
                if (end == null)
                    end = emploi.dateFin();
                if (week == null)
                    week = emploi.semaine();
                if (cid == null)
                    cid = emploi.classeId();
            }
        }

        if (start == null && dateDebut != null && !dateDebut.isBlank()) {
            start = java.time.LocalDate.parse(dateDebut);
        }
        if (end == null && dateFin != null && !dateFin.isBlank()) {
            end = java.time.LocalDate.parse(dateFin);
        }

        model.addAttribute("emploiId", id);
        model.addAttribute("classId", cid);
        model.addAttribute("dateDebut", start);
        model.addAttribute("dateFin", end);
        model.addAttribute("semaine", week);

        if (cid != null) {
            ClasseResponseDTO classe = classeService.getClasseById(cid);
            if (classe != null) {
                model.addAttribute("className", classe.code());
            }
        }

        model.addAttribute("activePage", "schedule");
        model.addAttribute("apName", "AP Name");
        return "APInterface/EditSchedule";
    }

    @GetMapping("/classes")
    public String classesView(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        Long filiereId = resolveApFiliereId(principal);

        // Walk filière → niveaux → spécialités → classes (dedup by id)
        List<ClasseResponseDTO> rawClasses = new ArrayList<>();
        List<NiveauResponseDTO> niveaux = filiereId != null
                ? niveauService.getNiveauxByFiliereId(filiereId)
                : new ArrayList<>();
        niveaux.sort((n1, n2) -> Integer.compare(n1.ordre(), n2.ordre()));

        List<SpecialiteResponseDTO> specialites = niveaux.isEmpty()
                ? new ArrayList<>()
                : specialiteService.getSpecialitesByNiveauId(niveaux.get(0).id());
        for (SpecialiteResponseDTO s : specialites) {
            if (s == null || s.id() == null)
                continue;
            List<ClasseResponseDTO> cs = classeService.getClassesBySpecialiteId(s.id());
            if (cs != null)
                rawClasses.addAll(cs);

        }

        model.addAttribute("niveaux", niveaux);

        Map<Long, ClasseResponseDTO> dedup = new LinkedHashMap<>();
        for (ClasseResponseDTO c : rawClasses) {
            if (c == null || c.id() == null)
                continue;
            dedup.putIfAbsent(c.id(), c);
        }

        // For each class, fetch enrolled students of the first class
        List<ClasseAvecEtudiants> classesAvecEtudiants = new ArrayList<>();
        List<ClasseResponseDTO> dedupList = new ArrayList<>(dedup.values());
        if (!dedupList.isEmpty()) {
            ClasseResponseDTO c = dedupList.get(0);
            List<JpaStudentProfileEntity> profiles = studentProfileRepository.findByClasseId(c.id());
            List<EtudiantVM> students = new ArrayList<>();
            for (JpaStudentProfileEntity p : profiles) {
                Long userId = (p.getUser() != null) ? p.getUser().getId() : null;
                String email = (p.getUser() != null) ? p.getUser().getEmail() : null;
                students.add(new EtudiantVM(userId, p.getNom(), p.getPrenom(), email,
                        p.getMatricule(), p.getNumeroTelephone()));
            }
            classesAvecEtudiants.add(new ClasseAvecEtudiants(c.id(), c.code(), students));
        }

        model.addAttribute("classes", rawClasses);
        model.addAttribute("classe", classesAvecEtudiants.isEmpty() ? null : classesAvecEtudiants.get(0));
        model.addAttribute("activePage", "classes");
        model.addAttribute("apName", "AP Name");
        return "APInterface/APClasses";
    }

    @GetMapping("/config")
    public String configView(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        Long filiereId = resolveApFiliereId(principal);
        List<NiveauResponseDTO> niveaux = niveauService.getNiveauxByFiliereId(filiereId);
        niveaux.sort((n1, n2) -> Integer.compare(n1.ordre(), n2.ordre()));

        model.addAttribute("niveaux", niveaux);
        model.addAttribute("activePage", "config");
        model.addAttribute("apName", "AP Name"); // Stub string used in template rendering

        return "APInterface/APConfig";
    }

    @PostMapping("/config/niveau/{id}/semestre")
    public org.springframework.http.ResponseEntity<?> updateSemestre(
            @PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.RequestParam("semestreActif") Integer semestreActif) {
        try {
            niveauService.updateSemestreActif(id, semestreActif);
            return org.springframework.http.ResponseEntity.ok().build();
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
