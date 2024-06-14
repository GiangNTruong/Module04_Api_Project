package ra.project_api.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
    @Value("${jwt.secret-key}")
    private String SECRET;
    @Value("${jwt.expired.access-token}")
    private long expiredAccess;
    @Value("${jwt.expired.refresh-token}")
    private long expiredRefresh;

    public String generateAccessToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiredAccess))
                .signWith(SignatureAlgorithm.HS512,SECRET)
                .compact();
    }
    public String generateRefreshToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiredRefresh))
                .signWith(SignatureAlgorithm.HS512,SECRET)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token); //Lưu ý chính tả
            return true;

        } catch (ExpiredJwtException e) {
            log.error("Failed -> Expired Token Message {}", e.getMessage());
        }catch (UnsupportedJwtException e) {
            log.error("Failed -> Unsupported Token Message {}", e.getMessage());
        }catch (MalformedJwtException e) {
            log.error("Failed -> Invalid Format Token Message {}", e.getMessage());
        }catch (SignatureException e) {
            log.error("Failed -> Invalid Signature Token Message {}", e.getMessage());
        }catch (IllegalArgumentException e) {
            log.error("Failed -> Claims Empty Token Message {}", e.getMessage());
        }
        return false;
    }

    public String getUserNameFromToken(String token){
        return Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(token).getBody().getSubject();
    }

}
