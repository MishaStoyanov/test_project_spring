package net.integrio.test_project.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import net.integrio.test_project.entity.Role;
import net.integrio.test_project.service.RolesService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    public ModelAndView editUser(@Validated @ModelAttribute RoleFormModel roleFormModel) {
        Role role = roleFormModel.setFormFieldsToRole();
        rolesService.saveInfo(role);
        return getModelAndView(role);
    }


    @Getter
    @Setter
    public static class RoleFormModel {

        Long id;
        @NotNull(message = "Fill role")
        @NotEmpty
        private String role;

        public Role setFormFieldsToRole() {
            Role role = new Role();
            if (id != null) role.setId(id);
            role.setRole(this.role);
            return role;
        }
    }

    private ModelAndView getModelAndView(Role role) {
        ModelAndView modelAndView = new ModelAndView("users/editrole");
        modelAndView.addObject("role", role);
        return modelAndView;
    }
}
