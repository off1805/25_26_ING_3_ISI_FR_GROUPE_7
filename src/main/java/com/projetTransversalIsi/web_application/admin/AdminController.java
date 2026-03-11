package com.projetTransversalIsi.web_application.admin;

import com.projetTransversalIsi.user.services.interfaces.GetAllUserStaffUC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminController {
    private final GetAllUserStaffUC getAllUserStaffUC;
    @GetMapping("/school")
    public String schoolView(Model model){
        Map <String,Object> users= new HashMap<>();
        model.addAttribute("staff",getAllUserStaffUC.execute());
        return "AdminInterface/AdminCycles";
    }
    @GetMapping("/users")
    public String usersView(Model model){
        model.addAttribute("staff", getAllUserStaffUC.execute());
        return "AdminInterface/AdminUser";
    }

    @GetMapping("/classes")
    public String classesView(Model model){
        model.addAttribute("staff", getAllUserStaffUC.execute());
        return "AdminInterface/AdminClasses";
    }
}



