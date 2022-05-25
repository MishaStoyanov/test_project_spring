package net.integrio.test_project.controller;

import lombok.extern.java.Log;
import net.integrio.test_project.resources.Constants;
import net.integrio.test_project.dto.UsersDto;
import net.integrio.test_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@Log
//@AllArgsConstructor
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping({"/login", "/"})
    public String start(Model model) {
        UsersDto usersDto = new UsersDto();
        usersDto.setLogin("");
        model.addAttribute("user", usersDto);
        return "login";
    }

    @PostMapping("/auth")
    public String auth(@RequestParam(Constants.login) String login, @RequestParam(Constants.password) String password, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (userService.auth(login, password)) {
            session.setAttribute(Constants.isAuthorized, true);
            session.setAttribute(Constants.username, login);
            search(1, "", 0, "id", "asc", model);//???
        } else {
            session.setAttribute(Constants.isAuthorized, false);
            UsersDto usersDto = new UsersDto();
            usersDto.setLogin(login);
            model.addAttribute("user", usersDto);
        }
        return userService.auth(login, password) ? "users/dashboard" : "login";
    }


    @GetMapping("/users/dashboard")
    public String search(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                         @RequestParam(value = "deleteID", defaultValue = "0") long deleteID,
                         @RequestParam(value = "sortedBy", defaultValue = "id") String sortedBy,
                         @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, Model model) {
        int currentPage = Optional.of(page).orElse(1);
        int pageSize = 10;
        Sort sort = sortDir.equals("asc") ? Sort.by(sortedBy).ascending() : Sort.by(sortedBy).descending();

        if (deleteID != 0) {
            userService.deleteById(deleteID);
            if (userService.search(PageRequest.of(currentPage - 1, pageSize, sort), keyword, sortedBy, sortDir).isEmpty())
                currentPage--;//check if last element on the page
        }

        Page<UsersDto> userPage = userService.search(PageRequest.of(currentPage - 1, pageSize, sort),
                keyword, sortedBy, sortDir);

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
        log.info("page = " + page + ", keyword = " + keyword + ", deleteID = " + deleteID + ", sortedBy = " + sort);
        return "users/dashboard";
    }

    @PostMapping("/users/dashboard")
    public String setKeyword(@RequestParam(Constants.keyword) String keyword, Model model) {
        search(1, keyword, 0, "id", "asc", model);
        return "users/dashboard";
    }
}