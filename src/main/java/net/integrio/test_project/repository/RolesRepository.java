package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, Long>, CustomRolesRepository {
}
