package net.integrio.test_project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import net.integrio.test_project.entity.Roles;
import net.integrio.test_project.service.RolesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@Log
@RequiredArgsConstructor
public class RolesController {

    private final RolesService rolesService;

    @GetMapping("/users/roleslist")
    public String search(@RequestParam(value = "page", defaultValue = "1") int page,
                         @RequestParam(value = "keyword", defaultValue = "") String keyword,
                         @RequestParam(value = "sortedBy", defaultValue = "id") String sortedBy,
                         @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                         @RequestParam(value = "deleteID", defaultValue = "0") long deleteID,
                         Model model) {
        int currentPage = Optional.of(page).orElse(1);
        int pageSize = 10;
        Sort sort = sortDir.equals("asc") ? Sort.by(sortedBy).ascending() : Sort.by(sortedBy).descending();

        if (deleteID != 0) {
            rolesService.deleteById(deleteID);
            if (rolesService.search(PageRequest.of(currentPage - 1, pageSize, sort), keyword, sortedBy, sortDir).isEmpty())
                currentPage--;//check if last element on the page
        }
        Page<Roles> rolesPage = rolesService.search(PageRequest.of(currentPage - 1, pageSize, sort), keyword, sortedBy, sortDir);

        if (rolesService.getNumberPages(rolesPage) != null) {
            model.addAttribute("pageNumbers", rolesService.getNumberPages(rolesPage));
        }

        model.addAttribute("rolesPage", rolesPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("columnSortDir", rolesService.getColumnsSortDir(sortedBy, sortDir));
        model.addAttribute("linkParameters", rolesService.getLinkParameters(keyword, sortedBy, sortDir, 0));
        model.addAttribute("keyword", keyword);
        log.info("page = " + page + ", keyword = " + keyword + ", sort = " + sort + ", deleteID = " + deleteID);
        return "users/roleslist";
    }

    @PostMapping("/users/roleslist")
    public String setParameters(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                @RequestParam(value = "id", defaultValue = "0") long id,
                                @RequestParam(value = "role", defaultValue = "") String role,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "sortedBy", defaultValue = "id") String sortedBy,
                                @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                                Model model) {
        if (!role.equals("")) {
            rolesService.setRoleInfoById(id, role);
        }
        search(keyword.equals("") ? page : 1, keyword, sortedBy, sortDir, 0, model);
        return "users/roleslist";
    }

}
