package net.integrio.test_project.service;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import net.integrio.test_project.entity.Role;
import net.integrio.test_project.entity.User;
import net.integrio.test_project.repository.RolesRepository;
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
    private final RolesRepository rolesRepository;

    @Override
    public Optional<User> findByLogin(String login) {
        return usersRepository.findByLogin(login);
    }

    public boolean auth(String login, String password) {
        Optional<User> users = findByLogin(login);
        return users.filter(user -> user.getLogin().equals(login) && user.getPassword().equals(password)).isPresent();
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
    public List<Integer> getNumberPages(Page<User> usersPage) {
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
    public User findById(Long id) {
        return usersRepository.getById(id);
    }

    @Override
    @Transactional
    public void saveUserInfo(User newUser, Set<Role> newUserRoles) {
        //1 -save user
        //2 -save role

       for (Role role: newUserRoles){
            newUser.getRoles().add(role);
            role.getUsers().add(newUser);
        }
        usersRepository.save(newUser);
    }

}

