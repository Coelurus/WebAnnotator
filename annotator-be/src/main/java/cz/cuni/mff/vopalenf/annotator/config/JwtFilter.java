package cz.cuni.mff.vopalenf.annotator.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode;
import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorResponse;
import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorResponseItem;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter that checks for a JWT in the Authorization header of incoming
 * requests.
 */
public class JwtFilter extends OncePerRequestFilter {
    /**
     * Prefix for the Authorization header containing the JWT token.
     */
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final UserAuthProvider userAuthProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        } catch (SignatureVerificationException e) {
            logger.warn("JWT signature verification failed - token invalid due to key change: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            handleJwtAuthenticationError(response, ErrorCode.BAD_CREDENTIALS, "JWT signature verification failed. Please log in again.");
            return;
        } catch (JWTVerificationException e) {
            logger.warn("JWT verification failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            handleJwtAuthenticationError(response, ErrorCode.BAD_CREDENTIALS, "Invalid or expired token. Please log in again.");
            return;
        } catch (Exception e) {
            logger.error("Unexpected error during JWT validation: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            handleJwtAuthenticationError(response, ErrorCode.BAD_CREDENTIALS, "Authentication failed. Please try again.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Handles JWT authentication errors by sending a proper 401 Unauthorized response
     * using the same format as RestExceptionHandler for consistency.
     *
     * @param response HTTP response
     * @param errorCode Error code from the application's error code enum
     * @param message Error message
     * @throws IOException if writing the response fails
     */
    private void handleJwtAuthenticationError(HttpServletResponse response, ErrorCode errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .errors(Collections.singletonList(
                    ErrorResponseItem.builder()
                        .error(errorCode.name())
                        .scope(JwtFilter.class.getSimpleName())
                        .message(message)
                        .build()
                ))
                .stackTrace(null)
                .build();
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}
