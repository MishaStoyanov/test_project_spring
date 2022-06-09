package net.integrio.test_project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.integrio.test_project.entity.User;
import net.integrio.test_project.resources.Constants;
import net.integrio.test_project.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@Log
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @GetMapping({"/login", "/"})
    public String start(Model model) {
        UsersDto usersDto = new UsersDto();
        usersDto.setLogin("");
        model.addAttribute("user", usersDto);
        return "login";
    }

    @PostMapping("/auth")
    public ModelAndView auth(@RequestParam(Constants.login) String login,
                       @RequestParam(Constants.password) String password,
                       Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (userService.auth(login, password)) {
            session.setAttribute(Constants.isAuthorized, true);
            session.setAttribute(Constants.username, login);
            model.addAttribute("columnSortDir", userService.getColumnsSortDir("id", "asc"));
            model.addAttribute("userPage", userService.search(PageRequest.of(0, 10), ""));
            model.addAttribute("pageNumbers", userService.getNumberPages(userService.search(PageRequest.of(1, 10), "")));
            model.addAttribute("linkParameters", userService.getLinkParameters("", "id", "asc"));
            return new ModelAndView("users/dashboard");
        } else {
            session.setAttribute(Constants.isAuthorized, false);
            User user = new User();
            user.setLogin(login);
            model.addAttribute("user", user);
            return new ModelAndView("login", "user", user);
        }
    }


    @GetMapping("/users/dashboard")
    public String search(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                         @RequestParam(value = "sortedBy", defaultValue = "id") String sortedBy,
                         @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, Model model) {
        int currentPage = Optional.of(page).orElse(1);
        int pageSize = 10;
        Sort sort = sortDir.equals("asc") ? Sort.by(sortedBy).ascending() : Sort.by(sortedBy).descending();
        Page<User> userPage = userService.search(PageRequest.of(currentPage - 1, pageSize, sort), keyword);

        if (userService.getNumberPages(userPage) != null) {
            model.addAttribute("pageNumbers", userService.getNumberPages(userPage));
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("sortField", sortedBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("linkParameters", userService.getLinkParameters(keyword, sortedBy, sortDir));
        model.addAttribute("columnSortDir", userService.getColumnsSortDir(sortedBy, sortDir));
        model.addAttribute("keyword", keyword);
        log.info("page = " + page + ", keyword = " + keyword + ", sortedBy = " + sort);
        return "users/dashboard";
    }

    @GetMapping("users/dashboard/delete/{id}")//??надо сохранять данные по которым идет сортировка??
    public String deleting(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                           @RequestParam(value = "sortedBy", defaultValue = "id") String sortedBy,
                           @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                           @PathVariable(value = "id") long deleteID, Model model) {
        int currentPage = Optional.of(page).orElse(1);
        int pageSize = 10;
        Sort sort = sortDir.equals("asc") ? Sort.by(sortedBy).ascending() : Sort.by(sortedBy).descending();
        if (deleteID != 0) {
            userService.deleteById(deleteID);
            if (userService.search(PageRequest.of(currentPage - 1, pageSize, sort), keyword).isEmpty())
                currentPage--;//check if last element on the page
        }
        return search(currentPage, keyword, sortedBy, sortDir, model);
    }

    @PostMapping("/users/dashboard")
    public String setKeyword(@RequestParam(Constants.keyword) String keyword, Model model) {
        return "redirect:/users/dashboard?page=1" + userService.getLinkParameters(keyword,"id","asc");
    }
}