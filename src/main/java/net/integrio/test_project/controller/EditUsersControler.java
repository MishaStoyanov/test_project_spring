package net.integrio.test_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class EditUsersControler {

    @GetMapping("users/edituser")
    public String start(){
        return "users/edituser";
    }
}
