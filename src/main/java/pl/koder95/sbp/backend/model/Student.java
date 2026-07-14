package pl.koder95.sbp.backend.model;

import jakarta.persistence.Entity;

@Entity
public class Student extends User {
    public Student() {
        setAuthority(Authority.ROLE_STUDENT);
    }

    @Override
    public void setPasswordHash(String passwordHash) {
        super.setPasswordHash(null);
    }
}
