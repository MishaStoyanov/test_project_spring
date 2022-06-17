package net.integrio.test_project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import net.integrio.test_project.entity.Role;
import net.integrio.test_project.models.RolesListFormModel;
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
        if (rolesListFormModel.getId() != 0) {
            Sort sort = rolesListFormModel.getSortDir().equals("asc") ? Sort.by(rolesListFormModel.getSortField()).ascending() : Sort.by(rolesListFormModel.getSortField()).descending();
            rolesService.deleteById(rolesListFormModel.getId());
            if (rolesService.search(PageRequest.of(rolesListFormModel.getPage() - 1, 10, sort), rolesListFormModel.getKeyword()).isEmpty())
                rolesListFormModel.setPage(rolesListFormModel.getPage() - 1);
        }
        return getModelAndView(rolesListFormModel);
    }

    private ModelAndView getModelAndView(RolesListFormModel rolesListFormModel) {
        int currentPage = Optional.of(rolesListFormModel.getPage()).orElse(1);
        int pageSize = 10;
        Sort sort = rolesListFormModel.getSortDir().equals("asc") ? Sort.by(rolesListFormModel.getSortField()).ascending() : Sort.by(rolesListFormModel.getSortField()).descending();
        Page<Role> rolesPage = rolesService.search(
                PageRequest.of(currentPage - 1, pageSize, sort), rolesListFormModel.getKeyword());
        ModelAndView modelAndView = new ModelAndView("users/roleslist");
        modelAndView.addObject("id", rolesListFormModel.getId());
        modelAndView.addObject("page", currentPage);
        modelAndView.addObject("sortField", rolesListFormModel.getSortField());
        modelAndView.addObject("sortDir", rolesListFormModel.getSortDir());
        modelAndView.addObject("keyword", rolesListFormModel.getKeyword());
        modelAndView.addObject("rolesPage", rolesPage);
        modelAndView.addObject("columnSortDir", rolesService.getColumnsSortDir(rolesListFormModel.getSortField(), rolesListFormModel.getSortDir()));
        modelAndView.addObject("pageNumbers", rolesService.getNumberPages(rolesPage));
        modelAndView.addObject("linkParameters", rolesService.getLinkParameters(rolesListFormModel.getKeyword(), rolesListFormModel.getSortField(), rolesListFormModel.getSortDir()));
        return modelAndView;
    }
}
