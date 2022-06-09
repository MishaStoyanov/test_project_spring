package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    public Page<Role> findRolesByRole(Set<String> keywords, String sortedBy, String sortDir, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> query = cb.createQuery(Role.class);
        Root<Role> role = query.from(Role.class);

        Path<String> rolePath = role.get("role");

        List<Predicate> predicates = new ArrayList<>();
        for (String keyword : keywords) {
            predicates.add(cb.like(rolePath, "%" + keyword + "%"));
        }
        query.select(role)
                .where(cb.or(predicates.toArray(new Predicate[0])))
                .orderBy(sortDir.equals("asc") ? cb.asc(role.get(sortedBy)) : cb.desc(role.get(sortedBy)));

        return new PageImpl<>(entityManager.createQuery(query).getResultList(), pageable, 10);
    }


}

