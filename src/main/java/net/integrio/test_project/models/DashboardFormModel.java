package net.integrio.test_project.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class DashboardFormModel {

    Long id;
    @NotNull(message = "Set page")
    private int page;
    @NotNull(message = "Set sorting by")
    @NotEmpty
    private String sortField;
    @NotNull(message = "Set sort direction")
    @NotEmpty
    private String sortDir;

    private String keyword;

}