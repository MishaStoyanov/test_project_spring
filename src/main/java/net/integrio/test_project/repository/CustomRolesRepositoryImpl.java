package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Roles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomRolesRepositoryImpl implements CustomRolesRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Roles> findRolesByRole(Set<String> keywords, String sortedBy, String sortDir) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Roles> query = cb.createQuery(Roles.class);
        Root<Roles> role = query.from(Roles.class);

        Path<String> rolePath = role.get("role");

        List<Predicate> predicates = new ArrayList<>();
        for (String keyword : keywords) {
            predicates.add(cb.like(rolePath, "%" + keyword + "%"));
        }
        query.select(role)
                .where(cb.or(predicates.toArray(new Predicate[0])))
                .orderBy(sortDir.equals("asc") ? cb.asc(role.get(sortedBy)) : cb.desc(role.get(sortedBy)));

        return entityManager.createQuery(query)
                .getResultList();
    }


}

