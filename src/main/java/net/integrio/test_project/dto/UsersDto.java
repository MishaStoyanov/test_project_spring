package net.integrio.test_project.dto;

import lombok.Data;

@Data
public class UsersDto {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}
