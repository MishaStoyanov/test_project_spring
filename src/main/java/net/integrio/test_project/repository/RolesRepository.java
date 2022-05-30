package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolesRepository extends JpaRepository<Roles, Long>, CustomRolesRepository {
    List<Roles> findRolesByUsersIdOrderById(Long id);
}
