package net.integrio.test_project.service;

import lombok.AllArgsConstructor;
import net.integrio.test_project.entity.Role;
import net.integrio.test_project.entity.User;
import net.integrio.test_project.repository.RolesRepository;
import net.integrio.test_project.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.integrio.test_project.resources.SortingColumns.userSortingColumns;

@Service
@AllArgsConstructor
public class DefaultRolesService implements RolesService {

    private final RolesRepository rolesRepository;
    private final UsersRepository usersRepository;

    @Override
    public Page<Role> search(Pageable pageable, String keyword) {
        return this.rolesRepository.findAll((Specification<Role>) (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                return criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.upper(
                                        root.get("role")
                                ),
                                criteriaBuilder.upper(
                                        criteriaBuilder.literal("%" + keyword + "%")
                                )
                        )
                );
            }
            return null;
        }, pageable);
    }

    @Override
    public List<Integer> getNumberPages(Page<Role> rolesPage) {
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
        Role roles = new Role();
        if (id != 0)
            roles.setId(id);
        roles.setRole(role);

        rolesRepository.save(roles);
    }

    @Override
    public List<Role> findAll() {
        return rolesRepository.findAll();
    }

    @Override
    public List<Boolean> findByUsersId(Long id) {
        List<Boolean> result = new ArrayList<>();
        boolean isChecked;
        for (Role role : rolesRepository.findAll()) {
            isChecked = false;
            for (Role roleById : rolesRepository.findRolesByUsersIdOrderById(id))
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
        Set<Role> roles = new HashSet<>();
        User user = usersRepository.getById(userId);
        for (String roleName : roleNames) {
            roles.add(rolesRepository.findRolesByRole(roleName));
        }
        user.setRoles(roles);
        for (Role role : roles) {
            role.getUsers().add(user);
        }
    }

    public Role findByRole(String role){
        return rolesRepository.findRolesByRole(role);
    }
}
