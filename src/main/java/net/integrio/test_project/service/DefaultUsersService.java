package net.integrio.test_project.service;

import net.integrio.test_project.dto.RolesDto;
import net.integrio.test_project.dto.UsersDto;
import net.integrio.test_project.entity.Users;
import net.integrio.test_project.repository.UsersRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.integrio.test_project.resources.SortingColumns.userSortingColumns;

@Service

public class DefaultUsersService implements UserService {

    private final UsersRepository usersRepository;
    private final UsersConverter usersConverter;

    public DefaultUsersService(UsersRepository usersRepository, UsersConverter usersConverter) {
        this.usersRepository = usersRepository;
        this.usersConverter = usersConverter;
    }

    @Override
    public UsersDto findByLogin(String login) {
        Users users = usersRepository.findByLogin(login);
        if (users != null) {
            return usersConverter.fromUserToUserDto(users);
        }
        return null;
    }

    public boolean auth(String login, String password) {
        UsersDto usersDto = findByLogin(login);
        if (usersDto != null) {
            return usersDto.getLogin().equals(login) && usersDto.getPassword().equals(password);
        } else return false;
    }

    @Override
    public Page<UsersDto> search(Pageable pageable, String keyword, String sortedBy, String sortDir) {
        Set<String> keywords = new HashSet<>();
        keywords.add(keyword);
        List<Users> users;
        if (keyword != null && !keyword.equals(""))
            users = usersRepository.findUsersByLoginOrFirstnameOrLastname(keywords, sortedBy, sortDir);
        else
            users = usersRepository.findAll(pageable.getSort());

        List<Users> result;
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        if (users.size() < startItem) {
            result = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, users.size());
            result = users.subList(startItem, toIndex);
        }
        return new PageImpl<>(usersConverter.fromUserListToUserDtoList(result), PageRequest.of(currentPage, pageSize), users.size());
    }

    @Override
    public long deleteById(long id) {
        usersRepository.deleteById(id);
        return id;
    }

    @Override
    public String getLinkParameters(String keyword, String sortedBy, String sortDir) {
        return (!keyword.equals("") ? ("&keyword=" + keyword) : "") +
                (!sortedBy.equals("") ? ("&sortedBy=" + sortedBy) : "") +
                (!sortDir.equals("") ? ("&sortDir=" + sortDir) : "");

    }

    @Override
    public List<String> getColumnsSortDir(String sortedBy, String sortDir) {
        List<String> columnSortDir = new ArrayList<>();//4 columns that sorted, get their sortDirections for links
        for (String column : userSortingColumns) {
            columnSortDir.add(column.equals(sortedBy) && sortDir.equals("asc") ? "desc" : "asc");
        }
        return columnSortDir;
    }

    @Override
    public List<Integer> getNumberPages(Page<UsersDto> rolesPage) {
        int totalPages = rolesPage.getTotalPages();
        if (totalPages > 0) {
            return IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
        }
        return null;
    }
}
