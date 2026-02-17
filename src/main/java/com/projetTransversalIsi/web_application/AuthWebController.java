package com.projetTransversalIsi.web_application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/auth")
@Controller
@RequiredArgsConstructor
public class AuthWebController {

    @GetMapping("/login")
    public String loginView(Model model){
        return "common/login";
    }
}
