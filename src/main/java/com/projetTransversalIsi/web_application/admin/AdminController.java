package com.projetTransversalIsi.web_application.admin;

import com.projetTransversalIsi.user.application.use_cases.GetAllUserStaffUC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminController {
    private final GetAllUserStaffUC getAllUserStaffUC;
    @GetMapping("/dashboard")
    public String dashboardView(Model model){
        Map <String,Object> users= new HashMap<>();
        model.addAttribute("staff",getAllUserStaffUC.execute());
        return "admin/dashboard";
    }
}



