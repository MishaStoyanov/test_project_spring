package net.integrio.test_project.models;

import lombok.Getter;
import lombok.Setter;
import net.integrio.test_project.entity.Role;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EditRoleFormModel {

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
