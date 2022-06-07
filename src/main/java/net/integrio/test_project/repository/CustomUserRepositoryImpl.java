package net.integrio.test_project.repository;

import net.integrio.test_project.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomUserRepositoryImpl implements CustomUserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Users> findUsersByLoginOrFirstnameOrLastname(Set<String> keywords, String sortedBy, String sortDir, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Users> query = cb.createQuery(Users.class);
        Root<Users> user = query.from(Users.class);

        Path<String> loginPath = user.get("login");
        Path<String> firstnamePath = user.get("firstname");
        Path<String> lastnamePath = user.get("lastname");

        List<Predicate> predicates = new ArrayList<>();
        for (String keyword : keywords) {
            predicates.add(cb.like(loginPath, "%" + keyword + "%"));
            predicates.add(cb.like(firstnamePath, "%" + keyword + "%"));
            predicates.add(cb.like(lastnamePath, "%" + keyword + "%"));
        }
        query.select(user).where(cb.or(predicates.toArray(new Predicate[0]))).orderBy(sortDir.equals("asc") ? cb.asc(user.get(sortedBy)) : cb.desc(user.get(sortedBy)));
        return new PageImpl<>(entityManager.createQuery(query).getResultList(), pageable, 10);
    }
}

