package net.integrio.test_project.service;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import net.integrio.test_project.entity.Role;
import net.integrio.test_project.entity.User;
import net.integrio.test_project.repository.RolesRepository;
import net.integrio.test_project.repository.UsersRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public Page<User> search(Pageable pageable, String keyword) {
        return this.usersRepository.findAll((Specification<User>) (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                return criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.upper(
                                        root.get("login")
                                ),
                                criteriaBuilder.upper(
                                        criteriaBuilder.literal("%" + keyword + "%")
                                )
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.upper(
                                        root.get("firstname")
                                ),
                                criteriaBuilder.upper(
                                        criteriaBuilder.literal("%" + keyword + "%")
                                )
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.upper(
                                        root.get("lastname")
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

