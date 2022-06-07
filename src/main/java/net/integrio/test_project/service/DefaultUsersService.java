package net.integrio.test_project.service;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
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
@Log
@AllArgsConstructor
public class DefaultUsersService implements UserService {

    private final UsersRepository usersRepository;
    private final UsersConverter usersConverter;

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
    public Page<Users> search(Pageable pageable, String keyword, String sortedBy, String sortDir) {
        Set<String> keywords = new HashSet<>();
        keywords.add(keyword);
        Page<Users> result;
        if (keyword != null && !keyword.equals(""))
            result = usersRepository.findUsersByLoginOrFirstnameOrLastname(keywords, sortedBy, sortDir, pageable);
        else
            result = usersRepository.findAll(pageable);
        return result;
    }

    @Override
    public List<Integer> getNumberPages(Page<Users> usersPage) {
        int totalPages = usersPage.getTotalPages();
        if (totalPages > 0) {
            return IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void deleteById(long id) {
        usersRepository.deleteById(id);
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
    public UsersDto findById(Long id) {
        Users user = usersRepository.getById(id);
        return usersConverter.fromUserToUserDto(user);
    }

    @Override
    public void saveUserInfo(long id, String login, String password, String firstname, String lastname) {
        UsersDto userById = new UsersDto();
        if (id != 0) {//old user, only new fields updated
            userById = findById(id);
            userById.setLogin(login.equals("") ? userById.getLogin() : login);
            userById.setPassword(password.equals("") ? userById.getPassword() : password);
            userById.setFirstname(firstname.equals("") ? userById.getFirstname() : firstname);
            userById.setLastname(lastname.equals("") ? userById.getLastname() : lastname);
            usersRepository.save(usersConverter.fromUserDtoToUser(userById));
        } else if (!login.equals("") && !password.equals("") && !firstname.equals("") && !lastname.equals("")) {
            userById.setId(id);//new user, all fields not null
            userById.setLogin(login);
            userById.setPassword(password);
            userById.setFirstname(firstname);
            userById.setLastname(lastname);
            usersRepository.save(usersConverter.fromUserDtoToUser(userById));
        }
    }

}

