package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Roles;

import java.util.List;
import java.util.Set;

public interface CustomRolesRepository {
    List<Roles> findRolesByRole(Set<String> keyword, String sortedBy, String sortDir);//??название??
}
