package net.integrio.test_project.controller;

import lombok.extern.java.Log;
import net.integrio.test_project.Constants;
import net.integrio.test_project.dto.UsersDto;
import net.integrio.test_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
            model.addAttribute(Constants.userList, userService.search(PageRequest.of(0, 10)));
            Page<UsersDto> userPage = userService.search(PageRequest.of(0, 10));

            model.addAttribute("userPage", userPage);

            int totalPages = userPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
            } else {
            session.setAttribute(Constants.isAuthorized, false);
                UsersDto usersDto = new UsersDto();
                usersDto.setLogin(login);
                model.addAttribute("user", usersDto);
            }
            return userService.auth(login, password) ? "users/dashboard" : "login";
        }


    @GetMapping("/users/dashboard")
    public String search(@RequestParam("page") int page, @RequestParam("size") int size, Model model){
        int currentPage = Optional.of(page).orElse(1);
        int pageSize = Optional.of(size).orElse(10);
        Page<UsersDto> userPage = userService.search(PageRequest.of(currentPage - 1, pageSize));
        log.info("page = "  + page + " size = " + size);
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/dashboard";
    }
}