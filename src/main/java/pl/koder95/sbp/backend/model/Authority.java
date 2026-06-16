package pl.koder95.sbp.backend.model;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    ROLE_ADMIN, ROLE_INSTRUCTOR;

    @Override
    public String getAuthority() {
        return name();
    }
}
