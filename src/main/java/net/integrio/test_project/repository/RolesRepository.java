package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RolesRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    List<Role> findRolesByUsersIdOrderById(Long id);
    void deleteRolesByUsersId(Long id);
    Role findRolesByRole(String role);
}
