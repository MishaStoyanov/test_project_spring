package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Users;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public interface CustomUserRepository {
    List<Users> findUsersByLoginOrFirstnameOrLastname(Set<String> keyword, String sortedBy, String sortDir);
}
