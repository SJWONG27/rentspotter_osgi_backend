package authentication.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private final SecretKey signingKey;
    // 24 hours expiration (example)
    private static final long EXPIRATION_TIME = 86400000;

    public JwtUtil(String secret) {
        // JJWT 0.12+ requires a key of at least 256 bits for HS256
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(signingKey) // New syntax: no second argument needed
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey) // Replaces setSigningKey()
                    .build()
                    .parseSignedClaims(token) // Replaces parseClaimsJws()
                    .getPayload();           // Replaces getBody()
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }
    }

    public String getUserId(String token) {
        return validateToken(token).getSubject();
    }
}