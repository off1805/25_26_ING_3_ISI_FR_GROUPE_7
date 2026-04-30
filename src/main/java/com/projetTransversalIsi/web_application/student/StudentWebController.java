package com.projetTransversalIsi.web_application.student;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentWebController {

    @GetMapping("/attendance")
    public String attendanceView() {
        return "StudentInterface/StudentAttendance";
    }
}
