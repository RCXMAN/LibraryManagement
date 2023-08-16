package com.example.librarymanagement.model;

import com.example.librarymanagement.model.enums.AccountStatusEnum;
import com.example.librarymanagement.model.enums.RoleEnum;
import com.example.librarymanagement.model.support.Person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class User implements UserDetails {
    @Id
    @GeneratedValue
    Long userId;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private AccountStatusEnum status;

    @Embedded
    private Person person = new Person();

    //Bidirectional @OneToOne lazy association
    //Although you might annotate the parent-side association to be fetched lazily,
    // Hibernate cannot honor this request since it cannot know whether the association is null or not.
    //
    //The only way to figure out whether there is an associated record on the child side is to fetch the child association using a secondary query.
    // Because this can lead to N+1 query issues, it’s much more efficient to use unidirectional @OneToOne associations with the @MapsId annotation in place.
    // using @MapsId so that the PRIMARY KEY is shared between the child and the parent entities.
    // When using @MapsId, the parent-side association becomes redundant since the child-entity can be easily fetched using the parent entity identifier.
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @MapsId
    private LibraryCard card = new LibraryCard();

    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
