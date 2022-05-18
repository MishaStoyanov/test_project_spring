package net.integrio.test_project.dto;

public class UsersDto {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
    public void setId(long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
