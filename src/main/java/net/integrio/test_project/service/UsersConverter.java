package net.integrio.test_project.service;

import net.integrio.test_project.dto.UsersDto;
import net.integrio.test_project.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsersConverter {

    public Users fromUserDtoToUser(UsersDto usersDto) {
        Users users = new Users();
        users.setId(usersDto.getId());
        users.setLogin(usersDto.getLogin());
        users.setFirstname(usersDto.getFirstname());
        users.setPassword(usersDto.getPassword());
        users.setLastname(usersDto.getLastname());
        return users;
    }

    public List<UsersDto> fromUserListToUserDtoList(List<Users> users) {
        List<UsersDto> result = new ArrayList<>();
        for (Users user : users) {
            result.add(fromUserToUserDto(user));
        }
        return result;
    }

    public Page<UsersDto> fromUserPagetoUserDtoPage(Page<Users> users){
        return new PageImpl<>(fromUserListToUserDtoList(users.toList()));
    }

    public UsersDto fromUserToUserDto(Users users) {
        UsersDto usersDto = new UsersDto();
        usersDto.setId(users.getId());
        usersDto.setLogin(users.getLogin());
        usersDto.setPassword(users.getPassword());
        usersDto.setFirstname(users.getFirstname());
        usersDto.setLastname(users.getLastname());
        return usersDto;
    }
}
