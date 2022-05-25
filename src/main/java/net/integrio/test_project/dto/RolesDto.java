package net.integrio.test_project.dto;

public class RolesDto {
    private Long id;
    private String role;

    public void setId(long id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public long getId() {
        return id;
    }


}
