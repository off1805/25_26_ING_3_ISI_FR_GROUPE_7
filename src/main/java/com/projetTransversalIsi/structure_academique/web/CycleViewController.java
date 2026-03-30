package com.projetTransversalIsi.structure_academique.web;

import com.projetTransversalIsi.structure_academique.application.service.CycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cycles")
@RequiredArgsConstructor
public class CycleViewController {

    private final CycleService cycleService;

    @GetMapping("/list")
    public String cycleList(Model model) {
        // We use getAllCycles from service which returns DTOs
        // This might need adjustment if the template expects domain objects
        // But DTOs are generally better for templates.
        model.addAttribute("cycles", cycleService.getAllCycles());
        return "AdminInterface/AdminCycles";
    }

    @GetMapping("/new")
    public String createCycleForm(Model model) {
        return "cycles/form";
    }
}
