package pl.koder95.sbp.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.model.Student;
import pl.koder95.sbp.backend.model.User;

@Component
public class AuthenticationUtil {
    private static Authentication getAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() == null) {
            return null;
        }
        return securityContext.getAuthentication();
    }

    public User getAuthenticated() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof User user) {
            return user;
        }
        return null;
    }

    public Student getAuthenticatedStudent() {
        User currentUser = getAuthenticated();
        if (currentUser instanceof Student student) {
            return student;
        }
        return null;
    }
}
