package net.integrio.test_project.controller;

import lombok.extern.java.Log;
import net.integrio.test_project.dto.UsersDto;
import net.integrio.test_project.service.RolesService;
import net.integrio.test_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Log
@Controller

public class EditUsersControler {

    @Autowired
    UserService userService;
    @Autowired
    RolesService rolesService;
    long currentId = 0;

    @GetMapping("users/edituser")
    public String start(Model model) {
        model.addAttribute("currentUser", new UsersDto());
        model.addAttribute("rolelist", rolesService.findAll());
        model.addAttribute("userRoles", rolesService.findByUsersId(0L));
        return "users/edituser";
    }

    @PostMapping("users/changeData")
    public String editUser(Model model,
                           @RequestParam(value = "id", defaultValue = "0") Long id,
                           @RequestParam(value = "login", defaultValue = "") String login,
                           @RequestParam(value = "password", defaultValue = "") String password,
                           @RequestParam(value = "firstname", defaultValue = "") String firstname,
                           @RequestParam(value = "lastname", defaultValue = "") String lastname,
                           @RequestParam(value = "role", required = false) List<String> selectedRoles) {

        UsersDto currentUser = null;
        if (id != 0 && id != currentId) {
            currentUser = userService.findById(id);
            log.info("id = " + currentUser.getId() + "login = " + currentUser.getLogin() + ", password" + currentUser.getPassword() +
                    ", firstname = " + currentUser.getFirstname() + ", lastname = " + currentUser.getLastname());
            currentId = id;
        } else if (!login.equals("") || !password.equals("") || !firstname.equals("") || !lastname.equals("")) {
            userService.saveUserInfo(id, login, password, firstname, lastname);
            currentUser = userService.findById(id);
            if (selectedRoles != null) {
                rolesService.saveRolesByUserId(id, selectedRoles);
            }
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("id", id);//??почему не хочет читать филд с юзера
        model.addAttribute("rolelist", rolesService.findAll());
        model.addAttribute("userRoles", rolesService.findByUsersId(id));
        return "users/edituser";
    }

    @PostMapping("users/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (file.isEmpty()) {
            return start(model);
        }

        String folder = "src/main/resources/static/images/";

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folder + "avatar_" + session.getAttribute("username") + ".jpg");
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return start(model);
    }
}
