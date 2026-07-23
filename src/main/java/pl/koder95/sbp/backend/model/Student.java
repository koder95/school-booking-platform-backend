package pl.koder95.sbp.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Student extends User {
    @OneToMany(mappedBy = "student")
    private Set<Booking> bookings;

    public Student() {
        setAuthority(Authority.ROLE_STUDENT);
    }

    @Override
    public void setPasswordHash(String passwordHash) {
        super.setPasswordHash(null);
    }
}
