package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.pedagogie.application.dto.UeFiltreDto;
import com.projetTransversalIsi.pedagogie.application.dto.UeResponseDTO;
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
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import com.projetTransversalIsi.structure_academique.application.dto.SpecialiteResponseDTO;
import com.projetTransversalIsi.structure_academique.application.service.SpecialiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final EmploiTempsService emploiTempsService;
    private final SearchUeUC searchUe;

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

    @GetMapping("/filiere/{id}")
    public String subjectsView(@PathVariable("id") Long id, Model model) {
        FiliereResponseDTO filiere = filiereService.getFiliereById(id);
        model.addAttribute("filiere", filiere);
        
        List<NiveauResponseDTO> niveaux = niveauService.getNiveauxByFiliereId(id);
        model.addAttribute("niveaux", niveaux);

        List<SpecialiteResponseDTO> allSpecialites = new ArrayList<>(specialiteService.getSpecialitesByNiveauId(niveaux.get(0).id()));
        model.addAttribute("specialites", allSpecialites);

        List<ClasseResponseDTO> allClasses = new ArrayList<>();
        for (SpecialiteResponseDTO s : allSpecialites) {
            if (s.id() != null) {
                allClasses.addAll(classeService.getClassesBySpecialiteId(s.id()));
            }
        }
        model.addAttribute("classes", allClasses);
        Page<Ue> ue= searchUe.execute(new UeFiltreDto(null,null,allSpecialites.get(0).id(),false),PageRequest.of(0, 10, Sort.by("id").descending()));
        System.out.println(ue.stream().toList());
        model.addAttribute(
                "apPageBreadcrumb",
                bcJoin(bcItem(filiere != null ? filiere.nom() : "Filiere", null)));
        model.addAttribute("activePage", "subjects");
        model.addAttribute("apName", "AP Name");
        model.addAttribute("ues",ue.stream().toList());
        return "APInterface/APSubjects";
    }

    @GetMapping("/schedule")
    public String scheduleView(Model model) {
        // TODO: replace with the filiere linked to the authenticated AP.

        // recuperer les niveaux
        // recupere le premier niveau
        // recuperer les specialites
        // recuperer les classes
        // recuperer les emplois de temps en cours
        // je charge ces emplois de temps dans les pages

        Long filiereId = 1L;
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
            if (s.id() != null) specialiteMap.put(s.id(), s);
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
                null, null, today, today
        );
        
        Page<EmploiTempsResponseDTO> ongoingPage = emploiTempsService.searchEmploiTemps(
                searchRequestParams, 
                PageRequest.of(0, 100, Sort.by("semaine").descending())
        );

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
    public String classesView(Model model) {
        // TODO: replace with the filiere linked to the authenticated AP.
        Long filiereId = 1L;

        // Walk filière → niveaux → spécialités → classes (dedup by id)
        List<ClasseResponseDTO> rawClasses = new ArrayList<>();
        List<NiveauResponseDTO> niveaux = niveauService.getNiveauxByFiliereId(filiereId);
        niveaux.sort((n1,n2)->n1.ordre());

                List<SpecialiteResponseDTO> specialites = specialiteService.getSpecialitesByNiveauId(niveaux.get(0).id());
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

        // For each class, fetch enrolled students
        List<ClasseAvecEtudiants> classesAvecEtudiants = new ArrayList<>();
        ClasseResponseDTO c=dedup.values().stream().toList().get(0);

            List<JpaStudentProfileEntity> profiles = studentProfileRepository.findByClasseId(c.id());
            List<EtudiantVM> students = new ArrayList<>();
            for (JpaStudentProfileEntity p : profiles) {
                Long userId = (p.getUser() != null) ? p.getUser().getId() : null;
                String email = (p.getUser() != null) ? p.getUser().getEmail() : null;

                students.add(new EtudiantVM(
                        userId,
                        p.getNom(),
                        p.getPrenom(),
                        email,
                        p.getMatricule(),
                        p.getNumeroTelephone()));
            }

            classesAvecEtudiants.add(new ClasseAvecEtudiants(c.id(), c.code(), students));

        model.addAttribute("classes", rawClasses);
        model.addAttribute("classe", classesAvecEtudiants.get(0));
        model.addAttribute("activePage", "classes");
        model.addAttribute("apName", "AP Name");
        return "APInterface/APClasses";
    }
}
