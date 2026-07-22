package pl.koder95.sbp.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final SecretKey secret;
    @Value("${jwt.expiration}")
    private long expiration;

    public JwtUtil() {
        byte[] bytes = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
        this.secret = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String username) {
        long millis = System.currentTimeMillis();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(millis))
                .expiration(new Date(millis + expiration))
                .signWith(secret)
                .compact();
    }

    public VerifiedToken verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return new VerifiedToken("", "", false);
        }
        Claims claims = getClaims(token);
        return new VerifiedToken(
                token,
                claims.getSubject(),
                claims.getExpiration().after(new Date())
        );
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public record VerifiedToken(String token, String username, boolean accepted) {
    }
}
