package net.integrio.test_project.service;

import lombok.AllArgsConstructor;
import net.integrio.test_project.entity.Role;
import net.integrio.test_project.entity.Role_;
import net.integrio.test_project.repository.RolesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.integrio.test_project.resources.SortingColumns.roleSortingColumns;

@Service
@AllArgsConstructor
public class DefaultRolesService implements RolesService {

    private final RolesRepository rolesRepository;

    @Override
    public Page<Role> search(Pageable pageable, String keyword) {
        return this.rolesRepository.findAll((Specification<Role>) (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                return criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.upper(
                                        root.get(Role_.role)
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
    public List<String> getColumnsSortDir(String sortField, String sortDir) {
        List<String> columnSortDir = new ArrayList<>();//2 columns that sorted, get their sortDirections for links
        for (String column : roleSortingColumns) {
            if (column.equals(sortField) && sortDir.equals("asc"))
                columnSortDir.add("desc");
            else
                columnSortDir.add("asc");
        }
        return columnSortDir;
    }

    @Override
    public String getLinkParameters(String keyword, String sortField, String sortDir) {
        return (keyword != null ? ("&keyword=" + keyword) : "") +
                (!sortField.equals("") ? ("&sortField=" + sortField) : "") +
                (!sortDir.equals("") ? ("&sortDir=" + sortDir) : "");
    }

    @Override
    public void deleteById(long id) {
        rolesRepository.deleteById(id);
    }

    @Override
    public void saveInfo(Role role) {
        rolesRepository.save(role);
    }

    @Override
    public List<Role> findAll() {
        return rolesRepository.findAll();
    }

    @Override
    public List<Boolean> findByUsersId(Long id) {
        List<Boolean> result = new ArrayList<>();
        boolean isChecked;
        if (id != null) {
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
        } else
            return getEmptyBooleanRolesList();
    }

    public List<Boolean> getEmptyBooleanRolesList() {
        List<Boolean> result = new ArrayList<>();
        for (Role role : rolesRepository.findAll()) {
            result.add(false);
        }
        return result;
    }

    @Override
    public Role findById(Long id) {
        return rolesRepository.getById(id);
    }
}
