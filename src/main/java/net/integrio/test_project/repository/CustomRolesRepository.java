package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CustomRolesRepository {
    Page<Roles> findRolesByRole(Set<String> keyword, String sortedBy, String sortDir, Pageable pageable);//??название??
}
