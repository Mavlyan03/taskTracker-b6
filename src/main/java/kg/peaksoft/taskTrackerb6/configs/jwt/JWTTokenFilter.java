package kg.peaksoft.taskTrackerb6.configs.jwt;

import kg.peaksoft.taskTrackerb6.entities.User;
import kg.peaksoft.taskTrackerb6.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class JWTTokenFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Optional<String> optionalJwt = getJwtFromRequest(request);

        optionalJwt.ifPresent(token -> {


            String email = jwtUtil.validateJWTToken(token);

            User user = userRepository.findByEmail(email).orElseThrow(
                    ()-> new UsernameNotFoundException("user with email: " + email + " not found")
            );

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        });

        filterChain.doFilter(request, response);
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return Optional.of(bearerToken.substring(BEARER.length()));
        }

        return Optional.empty();
    }
}
