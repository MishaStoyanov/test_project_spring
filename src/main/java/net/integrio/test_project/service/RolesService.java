package net.integrio.test_project.service;

import net.integrio.test_project.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RolesService {
    Page<Role> search(Pageable pageable, String keyword);

    List<Integer> getNumberPages(Page<Role> rolesPage);

    List<String> getColumnsSortDir(String sortedBy, String sortDir);
    //сделать общий сервис для некоторых методов?(дубликат кода)
    String getLinkParameters(String keyword, String sortedBy, String sortDir, long deleteId);
    void deleteById(long id);
    void setRoleInfoById(long id, String role);
    List<Role> findAll();
    List<Boolean> findByUsersId(Long id);
    void deleteByUsersId(Long id);
    void saveRolesByUserId(Long userId, List<String> roleNames);
    Role findByRole(String role);
}
