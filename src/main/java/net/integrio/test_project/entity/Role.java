package net.integrio.test_project.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String role;


    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role roles = (Role) o;
        return id != null && Objects.equals(id, roles.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
