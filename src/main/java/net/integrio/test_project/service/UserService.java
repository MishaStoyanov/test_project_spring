package net.integrio.test_project.service;

import net.integrio.test_project.dto.UsersDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UsersDto findByLogin(String login);
    boolean auth(String login, String password);
    Page<UsersDto> search(Pageable pageable);

}
