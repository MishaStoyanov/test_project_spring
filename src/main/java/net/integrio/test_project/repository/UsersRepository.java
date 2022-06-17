package net.integrio.test_project.repository;

import net.integrio.test_project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UsersRepository extends JpaSpecificationExecutor<User>, JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    Page<User> findUsersByLoginContainsAndFirstnameContainsAndLastnameContains(String keyword1, String keyword2, String keyword3, Pageable pageable);
}
