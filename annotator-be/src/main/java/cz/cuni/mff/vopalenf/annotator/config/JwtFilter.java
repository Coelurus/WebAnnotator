package cz.cuni.mff.vopalenf.annotator.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that checks for a JWT in the Authorization header of incoming
 * requests.
 */
public class JwtFilter extends OncePerRequestFilter {
    /**
     * Prefix for the Authorization header containing the JWT token.
     */
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    private final UserAuthProvider userAuthProvider;

    /**
     * Constructor for JwtFilter.
     *
     * @param userAuthProvider
     *            Provider for user authentication that validates JWT tokens.
     */
    public JwtFilter(UserAuthProvider userAuthProvider) {
        this.userAuthProvider = userAuthProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith(AUTH_HEADER_PREFIX)) {
                authHeader = authHeader.substring(AUTH_HEADER_PREFIX.length());
                if ("GET".equals(request.getMethod())) {
                    SecurityContextHolder.getContext().setAuthentication(userAuthProvider.validate(authHeader));
                } else {
                    SecurityContextHolder.getContext()
                            .setAuthentication(userAuthProvider.validateAgainstDB(authHeader));
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw e;
        }

        filterChain.doFilter(request, response);
    }
}
