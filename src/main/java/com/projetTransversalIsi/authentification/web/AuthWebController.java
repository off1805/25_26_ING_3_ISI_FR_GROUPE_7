package com.projetTransversalIsi.authentification.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthWebController {

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }
}