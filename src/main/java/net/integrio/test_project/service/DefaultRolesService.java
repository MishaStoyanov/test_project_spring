package net.integrio.test_project.service;

import lombok.AllArgsConstructor;
import net.integrio.test_project.dto.RolesDto;
import net.integrio.test_project.dto.UsersDto;
import net.integrio.test_project.entity.Roles;
import net.integrio.test_project.repository.RolesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    @Override
    public Page<RolesDto> search(Pageable pageable, String keyword, String sortedBy, String sortDir) {
        List<Roles> roles;
        Set<String> keywords = new HashSet<>();
        keywords.add(keyword);

        if (keyword.equals(""))
            roles = rolesRepository.findAll(pageable.getSort());
        else
            roles = rolesRepository.findRolesByRole(keywords, sortedBy, sortDir);

        List<Roles> result;
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        if (roles.size() < startItem) {
            result = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, roles.size());
            result = roles.subList(startItem, toIndex);
        }
        return new PageImpl<>(rolesConvertor.fromRolesListToRoleDtoList(result), PageRequest.of(currentPage, pageSize, pageable.getSort()), roles.size());
    }

    @Override
    public List<Integer> getNumberPages(Page<RolesDto> rolesPage) {
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
    public long deleteById(long id) {
        rolesRepository.deleteById(id);
        return id;
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
}
