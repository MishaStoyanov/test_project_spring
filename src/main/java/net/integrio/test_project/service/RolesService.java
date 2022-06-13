package net.integrio.test_project.service;

import net.integrio.test_project.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RolesService {
    Page<Role> search(Pageable pageable, String keyword);

    List<Integer> getNumberPages(Page<Role> rolesPage);

    List<String> getColumnsSortDir(String sortedBy, String sortDir);
    String getLinkParameters(String keyword, String sortField, String sortDir);
    void deleteById(long id);
    void saveInfo(Role role);
    List<Role> findAll();
    List<Boolean> findByUsersId(Long id);
    Role findById(Long id);
    List<Boolean> getEmptyBooleanRolesList();
}
