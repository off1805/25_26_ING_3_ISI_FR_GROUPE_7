package com.projetTransversalIsi.web_application.admin;

import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;
import com.projetTransversalIsi.user.dto.UserFiltreDto;
import com.projetTransversalIsi.user.infrastructure.UserMapper;
import com.projetTransversalIsi.user.services.GetAllUserStaffUC;
import com.projetTransversalIsi.user.services.SearchUserUC;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminController {
    private final GetAllUserStaffUC getAllUserStaffUC;
    private final SearchUserUC searchUserUC;
    private final UserMapper userMapper;
    @GetMapping("/school")
    public String schoolView(Model model){
        Map <String,Object> users= new HashMap<>();
        model.addAttribute("staff",getAllUserStaffUC.execute());
        return "AdminInterface/AdminSubjects";
    }
    @GetMapping("/users")
    public String usersView(Model model){
        Page<User> users=searchUserUC.execute(new UserFiltreDto(null, List.of("AP","TEACHER","SURVEILLANT"),null,null), PageRequest.of(0, 10, Sort.by("id").descending()));
        model.addAttribute("staff", users.stream().map(UserDetailsResponseDTO::fromDomain).toList());
        return "AdminInterface/AdminUser";
    }

    @GetMapping("/classes")
    public String classesView(Model model){
        model.addAttribute("staff", getAllUserStaffUC.execute());
        return "AdminInterface/AdminClasses";
    }
}



