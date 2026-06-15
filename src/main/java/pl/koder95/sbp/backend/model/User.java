package pl.koder95.sbp.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Setter
@Getter
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Email email;
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private boolean isDeleted = false;

    @Override
    public List<Authority> getAuthorities() {
        return List.of(authority);
    }

    @Override
    public String getUsername() {
        return email.getValue();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!Objects.equals(getEmail(), ((User) o).getEmail())) {
            return false;
        }
        return Objects.equals(getAuthority(), ((User) o).getAuthority());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getAuthority());
    }
}
