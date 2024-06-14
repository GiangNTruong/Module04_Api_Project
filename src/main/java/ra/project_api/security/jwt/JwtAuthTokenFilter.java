package ra.project_api.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
           String token = getTokenFromRequest(request);
           if (token!=null && jwtProvider.validateToken(token)){
               String username = jwtProvider.getUserNameFromToken(token);

               UserDetails principle = userDetailsService.loadUserByUsername(username);
               UsernamePasswordAuthenticationToken authentication = new
                       UsernamePasswordAuthenticationToken(principle,null,principle.getAuthorities());
               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
        filterChain.doFilter(request,response);
    }

    // ly token từ header ra
    private String getTokenFromRequest(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if (authorization!=null&&authorization.startsWith("Bearer ")){
            return authorization.substring("Bearer ".length());
        }
        return null;
    }
}
