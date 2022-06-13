package net.integrio.test_project.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import net.integrio.test_project.entity.User;
import net.integrio.test_project.resources.Constants;
import net.integrio.test_project.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Controller
@Log
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @GetMapping({"/login", "/"})
    public ModelAndView start() {
        User user = new User();
        user.setLogin("");
        return new ModelAndView("login", "user", user);
    }

    @PostMapping("/auth")
    public ModelAndView auth(@RequestParam(Constants.login) String login,
                             @RequestParam(Constants.password) String password,
                             HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (userService.auth(login, password)) {
            session.setAttribute(Constants.isAuthorized, true);
            session.setAttribute(Constants.username, login);

            PageFormModel pageFormModel = new PageFormModel(0L, 1, "id", "asc", "");
            return getModelAndView(pageFormModel);
        } else {
            session.setAttribute(Constants.isAuthorized, false);
            User user = new User();
            user.setLogin(login);
            return new ModelAndView("login", "user", user);
        }
    }

    @GetMapping("users/dashboardStart")
    public ModelAndView dashboardStart(){
        PageFormModel pageFormModel = new PageFormModel(0L, 1, "id", "asc", "");
        return getModelAndView(pageFormModel);
    }

    @GetMapping("/users/dashboard")
    public ModelAndView search(@Validated @ModelAttribute PageFormModel pageFormModel) {
        return getModelAndView(pageFormModel);
    }

    @GetMapping("users/dashboard/delete/{id}")
    public ModelAndView deleting(@Validated @ModelAttribute PageFormModel pageFormModel) {
          if (pageFormModel.id != 0) {
             Sort sort = pageFormModel.sortDir.equals("asc") ? Sort.by(pageFormModel.sortField).ascending() : Sort.by(pageFormModel.sortField).descending();
            userService.deleteById(pageFormModel.id);
            if (userService.search(PageRequest.of(pageFormModel.page - 1, 10, sort), pageFormModel.keyword).isEmpty())
                pageFormModel.setPage(pageFormModel.page--);
        }
        return getModelAndView(pageFormModel);
    }

    @PostMapping("/users/dashboard")
    public String setKeyword(@RequestParam(Constants.keyword) String keyword) {
        return "redirect:/users/dashboard?page=1" + userService.getLinkParameters(keyword, "id", "asc");
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PageFormModel {

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

    private ModelAndView getModelAndView(PageFormModel pageFormModel) {
        int currentPage = Optional.of(pageFormModel.page).orElse(1);
        int pageSize = 10;
        Sort sort = pageFormModel.sortDir.equals("asc") ? Sort.by(pageFormModel.sortField).ascending() : Sort.by(pageFormModel.sortField).descending();
        Page<User> userPage = userService.search(
                PageRequest.of(currentPage - 1, pageSize, sort), pageFormModel.keyword);
        ModelAndView modelAndView = new ModelAndView("users/dashboard");
        modelAndView.addObject("id", pageFormModel.getId());
        modelAndView.addObject("page", currentPage);
        modelAndView.addObject("sortField", pageFormModel.getSortField());
        modelAndView.addObject("sortDir", pageFormModel.getSortDir());
        modelAndView.addObject("keyword", pageFormModel.getKeyword());
        modelAndView.addObject("userPage", userPage);
        modelAndView.addObject("columnSortDir", userService.getColumnsSortDir(pageFormModel.sortField, pageFormModel.sortDir));
        modelAndView.addObject("pageNumbers",  userService.getNumberPages(userPage));
        modelAndView.addObject("linkParameters", userService.getLinkParameters(pageFormModel.keyword, pageFormModel.sortField, pageFormModel.sortDir));
        return modelAndView;
    }
}