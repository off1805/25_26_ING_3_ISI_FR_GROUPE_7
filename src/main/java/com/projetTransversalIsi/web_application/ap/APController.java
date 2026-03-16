package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.Filiere.application.dto.FiliereResponseDTO;
import com.projetTransversalIsi.Filiere.application.services.DefaultFiliereService;
import com.projetTransversalIsi.Niveau.application.dto.NiveauResponseDTO;
import com.projetTransversalIsi.Niveau.application.services.DefaultNiveauService;
import com.projetTransversalIsi.classe.application.dto.ClasseResponseDTO;
import com.projetTransversalIsi.classe.application.services.DefaultClasseService;
import com.projetTransversalIsi.specialite.application.dto.SpecialiteResponseDTO;
import com.projetTransversalIsi.specialite.application.services.DefaultSpecialiteService;
import com.projetTransversalIsi.ue.application.dto.UeFiltreDto;
import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.application.use_cases.SearchUeUC;
import com.projetTransversalIsi.ue.infrastructure.UeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Controller
@RequestMapping("/ap")
@RequiredArgsConstructor
public class APController {

    private final DefaultFiliereService filiereService;
    private final DefaultNiveauService niveauService;
    private final DefaultSpecialiteService specialiteService;
    private final DefaultClasseService classeService;
    private final SearchUeUC searchUeUC;
    private final UeMapper ueMapper;

    private static String bcSanitizeLabel(String label) {
        if (label == null) return "";
        // We use '|' and ':' as separators client-side; strip them from labels.
        return label.replace('|', ' ').replace(':', ' ').trim();
    }

    private static String bcItem(String label, String href) {
        String safeLabel = bcSanitizeLabel(label);
        if (safeLabel.isBlank()) return "";
        if (href == null || href.isBlank()) return safeLabel;
        return safeLabel + ":" + href;
    }

    private static String bcJoin(String... items) {
        StringJoiner joiner = new StringJoiner("|");
        for (String item : items) {
            if (item != null && !item.isBlank()) joiner.add(item);
        }
        return joiner.toString();
    }

    @GetMapping("/filiere/{id}")
    public String subjectsView(@PathVariable("id") Long id, Model model) {
        FiliereResponseDTO filiere = filiereService.getFiliereById(id);
        model.addAttribute("filiere", filiere);
        model.addAttribute("niveaux", niveauService.getNiveauxByFiliereId(id));
        model.addAttribute(
                "apPageBreadcrumb",
                bcJoin(
                        bcItem(filiere != null ? filiere.nom() : "Filiere", null)
                )
        );
        model.addAttribute("activePage", "subjects");
        model.addAttribute("apName", "AP Name"); // à remplacer par l'utilisateur connecté
        return "APInterface/APSubjects";
    }

    @GetMapping("/niveau/{id}")
    public String niveauView(@PathVariable("id") Long id, Model model) {
        NiveauResponseDTO niveau = niveauService.getNiveauById(id);
        model.addAttribute("niveau", niveau);
        model.addAttribute("specialites", specialiteService.getSpecialitesByNiveauId(id));
        FiliereResponseDTO filiere = null;
        if (niveau != null && niveau.filiereId() != null) {
            filiere = filiereService.getFiliereById(niveau.filiereId());
            model.addAttribute("filiere", filiere);
        }
        model.addAttribute(
                "apPageBreadcrumb",
                bcJoin(
                        bcItem(filiere != null ? filiere.nom() : (niveau != null ? niveau.filiereNom() : "Filiere"),
                                niveau != null && niveau.filiereId() != null ? ("/ap/filiere/" + niveau.filiereId()) : null),
                        bcItem(niveau != null ? ("Niveau " + niveau.ordre()) : "Niveau", null)
                )
        );
        model.addAttribute("activePage", "subjects");
        model.addAttribute("apName", "AP Name");
        return "APInterface/APNiveau";
    }

    @GetMapping("/specialite/{id}")
    public String specialiteView(@PathVariable("id") Long id, Model model) {
        SpecialiteResponseDTO specialite = specialiteService.getSpecialiteById(id);
        model.addAttribute("specialite", specialite);
        model.addAttribute("classes", classeService.getClassesBySpecialiteId(id));

        // UE catalog (module UE). For now this lists UEs globally (not yet scoped by specialite).
        UeFiltreDto ueFilter = new UeFiltreDto();
        ueFilter.setDeleted(false);
        ueFilter.setSpecialiteId(id);
        Page<Ue> uesPage = searchUeUC.execute(ueFilter, PageRequest.of(0, 50, Sort.by("id").descending()));
        model.addAttribute("ues", uesPage.map(ueMapper::toResponseDTO).getContent());

        NiveauResponseDTO niveau = null;
        FiliereResponseDTO filiere = null;
        if (specialite != null && specialite.niveauId() != null) {
            niveau = niveauService.getNiveauById(specialite.niveauId());
            model.addAttribute("niveau", niveau);
            if (niveau != null && niveau.filiereId() != null) {
                filiere = filiereService.getFiliereById(niveau.filiereId());
                model.addAttribute("filiere", filiere);
            }
        }

        model.addAttribute(
                "apPageBreadcrumb",
                bcJoin(
                        bcItem(filiere != null ? filiere.nom() : "Filiere",
                                filiere != null && filiere.id() != null ? ("/ap/filiere/" + filiere.id()) : null),
                        bcItem(niveau != null ? ("Niveau " + niveau.ordre()) : "Niveau",
                                niveau != null && niveau.id() != null ? ("/ap/niveau/" + niveau.id()) : null),
                        bcItem(specialite != null ? specialite.libelle() : "Specialite", null)
                )
        );
        model.addAttribute("activePage", "subjects");
        model.addAttribute("apName", "AP Name");
        return "APInterface/APSpecialite";
    }

    @GetMapping("/schedule")
    public String scheduleView(Model model) {
        // TODO: replace with the filiere linked to the authenticated AP.
        Long filiereId = 1L;
        model.addAttribute("filiereId", filiereId);

        // Load "valid" classes for this filiere by walking:
        // filiere -> niveaux -> specialites -> classes
        List<ClasseResponseDTO> collected = new ArrayList<>();
        List<NiveauResponseDTO> niveaux = niveauService.getNiveauxByFiliereId(filiereId);
        if (niveaux != null) {
            for (NiveauResponseDTO n : niveaux) {
                if (n == null || n.id() == null) continue;
                List<SpecialiteResponseDTO> specialites = specialiteService.getSpecialitesByNiveauId(n.id());
                if (specialites == null) continue;
                for (SpecialiteResponseDTO s : specialites) {
                    if (s == null || s.id() == null) continue;
                    List<ClasseResponseDTO> classes = classeService.getClassesBySpecialiteId(s.id());
                    if (classes != null) collected.addAll(classes);
                }
            }
        }

        // De-dup by id (or by code if id missing) while preserving order.
        Map<String, ClasseResponseDTO> uniq = new LinkedHashMap<>();
        for (ClasseResponseDTO c : collected) {
            if (c == null) continue;
            String key = c.id() != null ? ("id:" + c.id()) : ("code:" + c.code());
            if (!uniq.containsKey(key)) uniq.put(key, c);
        }
        model.addAttribute("classes", new ArrayList<>(uniq.values()));

        model.addAttribute("activePage", "schedule");
        model.addAttribute("apName", "AP Name");
        return "APInterface/APSchedule";
    }

    @GetMapping("/schedule/edit")
    public String editScheduleView(Model model) {
        model.addAttribute("activePage", "schedule");
        model.addAttribute("apName", "AP Name");
        return "APInterface/EditSchedule";
    }
}
