package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long>, CustomUserRepository {
    Users findByLogin(String login);
    //TODO:dv methods
}
