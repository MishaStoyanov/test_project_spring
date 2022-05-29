package net.integrio.test_project.service;

import net.integrio.test_project.dto.UsersDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {
    UsersDto findByLogin(String login);
    boolean auth(String login, String password);
    Page<UsersDto> search(Pageable pageable, String keyword, String sortedBy, String sortDir);
    long deleteById(long id);
    String getLinkParameters(String keyword, String sortedBy, String sortDir);
    List<String> getColumnsSortDir(String sortedBy, String sortDir);
    List<Integer> getNumberPages(Page<UsersDto> rolesPage);
    UsersDto findById(Long id);
    void saveUserInfo(long id, String login, String password, String firstname, String lastname);
}
