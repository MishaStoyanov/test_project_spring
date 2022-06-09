package net.integrio.test_project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.integrio.test_project.entity.Role;
import net.integrio.test_project.entity.User;
import net.integrio.test_project.service.RolesService;
import net.integrio.test_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Log
@Controller
@RequiredArgsConstructor
public class EditUsersControler {

    private final UserService userService;
    private final RolesService rolesService;

    @GetMapping("users/edituser")
    public String start(Model model,
                        @RequestParam(value = "id", required = false) Long id) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("rolelist", rolesService.findAll());
        model.addAttribute("userRoles", rolesService.findByUsersId(id));
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
        //1 - get entity form db by id
        //2 - if null = new
        //3 - fill from model
        //4 - save
        User user = userFormModel.setFormFieldsToUser();
        userService.saveUserInfo(user, fromListRolesToSet(selectedRoles));
        model.addAttribute("user", user);
        model.addAttribute("rolelist", rolesService.findAll());
        model.addAttribute("userRoles", rolesService.findByUsersId(user.getId()));
        return "users/edituser";
    }

    @PostMapping("users/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (file.isEmpty()) {
            return "redirect:/users/edituser";
        }

        String folder = "src/main/resources/static/images/";

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folder + "avatar_" + session.getAttribute("username") + ".jpg");
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/users/edituser";
    }
}
