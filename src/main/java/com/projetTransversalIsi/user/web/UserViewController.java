package com.projetTransversalIsi.user.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserViewController {

    @GetMapping("/list")
    public String userList(Model model){
        Map <String,Object> users= new HashMap<>();
        model.addAllAttributes(
               users
        );
        return "students/list";
    }

}
