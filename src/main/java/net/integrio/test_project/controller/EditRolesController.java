package net.integrio.test_project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.integrio.test_project.entity.Role;
import net.integrio.test_project.models.EditRoleFormModel;
import net.integrio.test_project.service.RolesService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Log
@RequiredArgsConstructor
public class EditRolesController {
    private final RolesService rolesService;

    @GetMapping("users/editrole")
    public ModelAndView start(@RequestParam(value = "id", required = false) Long id) {
        return getModelAndView(id == null ? new Role() : rolesService.findById(id));
    }

    @PostMapping("users/changeRoleData")
    public ModelAndView editUser(@Validated @ModelAttribute EditRoleFormModel editRoleFormModel) {
        Role role = editRoleFormModel.setFormFieldsToRole();
        rolesService.saveInfo(role);
        return getModelAndView(role);
    }

    private ModelAndView getModelAndView(Role role) {
        ModelAndView modelAndView = new ModelAndView("users/editrole");
        modelAndView.addObject("role", role);
        return modelAndView;
    }
}
