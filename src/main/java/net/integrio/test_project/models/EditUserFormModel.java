package net.integrio.test_project.models;

import lombok.Getter;
import lombok.Setter;
import net.integrio.test_project.entity.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class EditUserFormModel {

    Long id;
    @NotNull(message = "Fill login")
    @NotEmpty
    private String login;
    @NotNull(message = "Set password")
    @NotEmpty
    private String password;
    @NotNull(message = "Fill firstname")
    @NotEmpty
    private String firstname;
    @NotNull(message = "Fill lastname")
    @NotEmpty
    private String lastname;
    @NotNull(message = "Set user roles")
    @NotEmpty
    private List<String> roles;

    public User setFormFieldsToUser() {
        User user = new User();
        if (id != null) user.setId(id);
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        return user;
    }
}