package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Roles;
import net.integrio.test_project.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long>, CustomUserRepository {
    Users findByLogin(String login);
    List<Users> findUsersByRolesId(Long id);
    //TODO:dv methods
}
