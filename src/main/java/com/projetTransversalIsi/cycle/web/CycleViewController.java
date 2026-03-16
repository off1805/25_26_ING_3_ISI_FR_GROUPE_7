package com.projetTransversalIsi.cycle.web;

import com.projetTransversalIsi.cycle.application.use_cases.GetAllCyclesUC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cycles")
@RequiredArgsConstructor
public class CycleViewController {

    private final GetAllCyclesUC getAllCyclesUC;

    @GetMapping("/list")
    public String cycleList(Model model) {
        model.addAttribute("cycles", getAllCyclesUC.execute());
        return "AdminInterface/AdminCycles";
    }

    @GetMapping("/new")
    public String createCycleForm(Model model) {
        return "cycles/form";
    }
}
