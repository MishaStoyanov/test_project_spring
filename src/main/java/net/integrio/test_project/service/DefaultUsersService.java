package net.integrio.test_project.service;

import net.integrio.test_project.dto.UsersDto;
import net.integrio.test_project.entity.Users;
import net.integrio.test_project.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
    public Page<UsersDto> search(Pageable pageable) {
        List<Users> users = usersRepository.findAll();
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
}
