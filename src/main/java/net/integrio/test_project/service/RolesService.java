package net.integrio.test_project.service;

import net.integrio.test_project.dto.RolesDto;
import net.integrio.test_project.dto.UsersDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RolesService {
    Page<RolesDto> search(Pageable pageable, String keyword, String sortedBy, String sortDir);

    List<Integer> getNumberPages(Page<RolesDto> rolesPage);

    List<String> getColumnsSortDir(String sortedBy, String sortDir);
    //сделать общий сервис для некоторых методов?(дубликат кода)
    String getLinkParameters(String keyword, String sortedBy, String sortDir, long deleteId);
    long deleteById(long id);
    void setRoleInfoById(long id, String role);
    List<RolesDto> findAll();
    List<Boolean> findByUsersId(Long id);
    void deleteByUsersId(Long id);
    void saveRolesByUserId(Long userId, List<String> roleNames);
}
