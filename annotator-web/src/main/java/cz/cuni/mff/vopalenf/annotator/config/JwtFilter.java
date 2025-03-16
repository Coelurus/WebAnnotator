package cz.cuni.mff.vopalenf.annotator.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    private final UserAuthProvider userAuthProvider;

    public JwtFilter(UserAuthProvider userAuthProvider) {
        this.userAuthProvider = userAuthProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith(AUTH_HEADER_PREFIX)) {
                authHeader = authHeader.substring(AUTH_HEADER_PREFIX.length());
                if ("GET".equals(request.getMethod())) {
                    SecurityContextHolder.getContext().setAuthentication(
                            userAuthProvider.validate(authHeader)
                    );
                } else {
                    SecurityContextHolder.getContext().setAuthentication(
                            userAuthProvider.validateAgainstDB(authHeader)
                    );
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw e;
        }

        filterChain.doFilter(request, response);
    }
}
