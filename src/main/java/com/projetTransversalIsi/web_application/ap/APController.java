package com.projetTransversalIsi.web_application.ap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ap")
public class APController {

    @GetMapping("/subjects")
    public String subjectsView(Model model) {
        model.addAttribute("activePage", "subjects");
        model.addAttribute("apName", "AP Name"); // à remplacer par l'utilisateur connecté
        return "APInterface/APSubjects";
    }

    @GetMapping("/schedule")
    public String scheduleView(Model model) {
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