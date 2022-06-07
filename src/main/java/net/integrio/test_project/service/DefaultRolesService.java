package net.integrio.test_project.service;

import lombok.AllArgsConstructor;
import net.integrio.test_project.dto.RolesDto;
import net.integrio.test_project.entity.Roles;
import net.integrio.test_project.entity.Users;
import net.integrio.test_project.repository.RolesRepository;
import net.integrio.test_project.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.integrio.test_project.resources.SortingColumns.userSortingColumns;

@Service
@AllArgsConstructor
public class DefaultRolesService implements RolesService {

    private final RolesRepository rolesRepository;
    private final RolesConvertor rolesConvertor;
    private final UsersRepository usersRepository;

    @Override
    public Page<Roles> search(Pageable pageable, String keyword, String sortedBy, String sortDir) {
        Set<String> keywords = new HashSet<>();
        keywords.add(keyword);
        Page<Roles> result;
        if (keyword != null && !keyword.equals(""))
            result = rolesRepository.findRolesByRole(keywords, sortedBy, sortDir, pageable);
        else
            result = rolesRepository.findAll(pageable);

        return result;
    }

    @Override
    public List<Integer> getNumberPages(Page<Roles> rolesPage) {
        int totalPages = rolesPage.getTotalPages();
        if (totalPages > 0) {
            return IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<String> getColumnsSortDir(String sortedBy, String sortDir) {
        List<String> columnSortDir = new ArrayList<>();//2 columns that sorted, get their sortDirections for links
        for (String column : userSortingColumns) {
            columnSortDir.add(column.equals(sortedBy) && sortDir.equals("asc") ? "desc" : "asc");
        }
        return columnSortDir;
    }

    @Override
    public String getLinkParameters(String keyword, String sortedBy, String sortDir, long deleteId) {
        return (!keyword.equals("") ? ("&keyword=" + keyword) : "") +
                (!sortedBy.equals("") ? ("&sortedBy=" + sortedBy) : "") +
                (!sortDir.equals("") ? ("&sortDir=" + sortDir) : "") +
                (deleteId != 0 ? ("&deleteID=" + deleteId) : "");

    }

    @Override
    public void deleteById(long id) {
        rolesRepository.deleteById(id);
    }

    @Override
    public void setRoleInfoById(long id, String role) {
        Roles roles = new Roles();
        if (id != 0)
            roles.setId(id);
        roles.setRole(role);

        rolesRepository.save(roles);
    }

    @Override
    public List<RolesDto> findAll() {
        return rolesConvertor.fromRolesListToRoleDtoList(rolesRepository.findAll());
    }

    @Override
    public List<Boolean> findByUsersId(Long id) {
        List<Boolean> result = new ArrayList<>();
        boolean isChecked;
        for (RolesDto role : rolesConvertor.fromRolesListToRoleDtoList(rolesRepository.findAll())) {
            isChecked = false;
            for (RolesDto roleById : rolesConvertor.fromRolesListToRoleDtoList(rolesRepository.findRolesByUsersIdOrderById(id)))
                if (Objects.equals(role.getId(), roleById.getId())) {
                    isChecked = true;
                    break;
                }
            result.add(isChecked);
        }
        return result;
    }

    @Override
    public void deleteByUsersId(Long id) {
        rolesRepository.deleteRolesByUsersId(id);
    }

    @Override
    public void saveRolesByUserId(Long userId, List<String> roleNames) {
        Set<Roles> roles = new HashSet<>();
        Users user = usersRepository.getById(userId);
        for (String roleName : roleNames) {
            roles.add(rolesRepository.findRolesByRole(roleName));
        }
        user.setRoles(roles);
        for (Roles role : roles) {
            role.getUsers().add(user);
        }
    }

}
