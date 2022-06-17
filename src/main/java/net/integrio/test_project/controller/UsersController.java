package net.integrio.test_project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.integrio.test_project.entity.User;
import net.integrio.test_project.models.DashboardFormModel;
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

            DashboardFormModel dashboardFormModel = new DashboardFormModel(0L, 1, "id", "asc", "");
            return getModelAndView(dashboardFormModel);
        } else {
            session.setAttribute(Constants.isAuthorized, false);
            User user = new User();
            user.setLogin(login);
            return new ModelAndView("login", "user", user);
        }
    }

    @GetMapping("users/dashboardStart")
    public ModelAndView dashboardStart() {
        DashboardFormModel dashboardFormModel = new DashboardFormModel(0L, 1, "id", "asc", "");
        return getModelAndView(dashboardFormModel);
    }

    @GetMapping("/users/dashboard")
    public ModelAndView search(@Validated @ModelAttribute DashboardFormModel dashboardFormModel) {
        return getModelAndView(dashboardFormModel);
    }

    @GetMapping("users/dashboard/delete/{id}")
    public ModelAndView deleting(@Validated @ModelAttribute DashboardFormModel dashboardFormModel) {
        if (dashboardFormModel.getId() != 0) {
            Sort sort = dashboardFormModel.getSortDir().equals("asc") ? Sort.by(dashboardFormModel.getSortField()).ascending() : Sort.by(dashboardFormModel.getSortField()).descending();
            userService.deleteById(dashboardFormModel.getId());
            if (userService.search(PageRequest.of(dashboardFormModel.getPage() - 1, 10, sort), dashboardFormModel.getKeyword()).isEmpty())
                dashboardFormModel.setPage(dashboardFormModel.getPage() - 1);
        }
        return getModelAndView(dashboardFormModel);
    }

    @PostMapping("/users/dashboard")
    public String setKeyword(@RequestParam(Constants.keyword) String keyword) {
        return "redirect:/users/dashboard?page=1" + userService.getLinkParameters(keyword, "id", "asc");
    }

    private ModelAndView getModelAndView(DashboardFormModel dashboardFormModel) {
        int currentPage = Optional.of(dashboardFormModel.getPage()).orElse(1);
        int pageSize = 10;
        Sort sort = dashboardFormModel.getSortDir().equals("asc") ? Sort.by(dashboardFormModel.getSortField()).ascending() : Sort.by(dashboardFormModel.getSortField()).descending();
        Page<User> userPage = userService.search(
                PageRequest.of(currentPage - 1, pageSize, sort), dashboardFormModel.getKeyword());
        ModelAndView modelAndView = new ModelAndView("users/dashboard");
        modelAndView.addObject("id", dashboardFormModel.getId());
        modelAndView.addObject("page", currentPage);
        modelAndView.addObject("sortField", dashboardFormModel.getSortField());
        modelAndView.addObject("sortDir", dashboardFormModel.getSortDir());
        modelAndView.addObject("keyword", dashboardFormModel.getKeyword());
        modelAndView.addObject("userPage", userPage);
        modelAndView.addObject("columnSortDir", userService.getColumnsSortDir(dashboardFormModel.getSortField(), dashboardFormModel.getSortDir()));
        modelAndView.addObject("pageNumbers", userService.getNumberPages(userPage));
        modelAndView.addObject("linkParameters", userService.getLinkParameters(dashboardFormModel.getKeyword(), dashboardFormModel.getSortField(), dashboardFormModel.getSortDir()));
        return modelAndView;
    }
}