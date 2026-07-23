package pl.koder95.sbp.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.model.Student;
import pl.koder95.sbp.backend.model.User;

@Component
public class AuthenticationUtil {
    public User getAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
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
