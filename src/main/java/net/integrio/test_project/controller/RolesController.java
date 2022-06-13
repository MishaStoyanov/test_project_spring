package net.integrio.test_project.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import net.integrio.test_project.entity.Role;
import net.integrio.test_project.resources.Constants;
import net.integrio.test_project.service.RolesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Controller
@Log
@RequiredArgsConstructor
public class RolesController {

    private final RolesService rolesService;

    @GetMapping("users/roleslistStart")
    public ModelAndView start() {
        RolesListFormModel rolesListFormModel = new RolesListFormModel(0L, 1, "id", "asc", "");
        return getModelAndView(rolesListFormModel);
    }

    @GetMapping("/users/roleslist")
    public ModelAndView search(@Validated @ModelAttribute RolesListFormModel rolesListFormModel) {
        return getModelAndView(rolesListFormModel);
    }

    @PostMapping("/users/roleslist")
    public String setKeyword(@RequestParam(Constants.keyword) String keyword) {
        return "redirect:/users/roleslist?page=1" + rolesService.getLinkParameters(keyword, "id", "asc");
    }

    @GetMapping("users/roleslist/delete/{id}")
    public ModelAndView deleting(@Validated @ModelAttribute RolesListFormModel rolesListFormModel) {
        if (rolesListFormModel.id != 0) {
            Sort sort = rolesListFormModel.sortDir.equals("asc") ? Sort.by(rolesListFormModel.sortField).ascending() : Sort.by(rolesListFormModel.sortField).descending();
            rolesService.deleteById(rolesListFormModel.id);
            if (rolesService.search(PageRequest.of(rolesListFormModel.page - 1, 10, sort), rolesListFormModel.keyword).isEmpty())
                rolesListFormModel.setPage(rolesListFormModel.page--);
        }
        return getModelAndView(rolesListFormModel);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RolesListFormModel {

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

    private ModelAndView getModelAndView(RolesListFormModel rolesListFormModel) {
        int currentPage = Optional.of(rolesListFormModel.page).orElse(1);
        int pageSize = 10;
        Sort sort = rolesListFormModel.sortDir.equals("asc") ? Sort.by(rolesListFormModel.sortField).ascending() : Sort.by(rolesListFormModel.sortField).descending();
        Page<Role> rolesPage = rolesService.search(
                PageRequest.of(currentPage - 1, pageSize, sort), rolesListFormModel.keyword);
        ModelAndView modelAndView = new ModelAndView("users/roleslist");
        modelAndView.addObject("id", rolesListFormModel.getId());
        modelAndView.addObject("page", currentPage);
        modelAndView.addObject("sortField", rolesListFormModel.getSortField());
        modelAndView.addObject("sortDir", rolesListFormModel.getSortDir());
        modelAndView.addObject("keyword", rolesListFormModel.getKeyword());
        modelAndView.addObject("rolesPage", rolesPage);
        modelAndView.addObject("columnSortDir", rolesService.getColumnsSortDir(rolesListFormModel.sortField, rolesListFormModel.sortDir));
        modelAndView.addObject("pageNumbers", rolesService.getNumberPages(rolesPage));
        modelAndView.addObject("linkParameters", rolesService.getLinkParameters(rolesListFormModel.keyword, rolesListFormModel.sortField, rolesListFormModel.sortDir));
        return modelAndView;
    }
}
