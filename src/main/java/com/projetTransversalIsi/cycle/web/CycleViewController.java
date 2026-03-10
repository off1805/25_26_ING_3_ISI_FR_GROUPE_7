package com.projetTransversalIsi.cycle.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cycles")
public class CycleViewController {

    @GetMapping("/list")
    public String cycleList(Model model) {
        Map<String, Object> cycles = new HashMap<>();
        model.addAllAttributes(cycles);
        return "cycles/list";
    }

    @GetMapping("/new")
    public String createCycleForm(Model model) {
        return "cycles/form";
    }
}
