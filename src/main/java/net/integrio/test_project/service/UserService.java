package net.integrio.test_project.service;

import net.integrio.test_project.entity.Role;
import net.integrio.test_project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    Optional<User> findByLogin(String login);
    boolean auth(String login, String password);
    Page<User> search(Pageable pageable, String keyword);
    void deleteById(long id);
    String getLinkParameters(String keyword, String sortedBy, String sortDir);
    List<String> getColumnsSortDir(String sortedBy, String sortDir);
    List<Integer> getNumberPages(Page<User> rolesPage);
    User findById(Long id);
    void saveUserInfo(User newUser, Set<Role> newUserRoles);
}
