package cz.cuni.mff.vopalenf.annotator.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.api.request.UserRequest;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import cz.cuni.mff.vopalenf.annotator.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

/**
 * UserAuthProvider is responsible for creating and validating JWT tokens for user authentication.
 */
@Component
public class UserAuthProvider {

    private final UserService userService;

    /**
     * The secret key used for signing JWT tokens.
     */
    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    /**
     * Constructor for UserAuthProvider.
     *
     * @param userService The service used to interact with user data.
     */
    @Autowired
    public UserAuthProvider(UserService userService) {
        this.userService = userService;
    }

    /**
     * Initializes the secret key by encoding it in Base64 format.
     */
    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * Creates a JWT token for the given user.
     *
     * @param user the user for whom the token is created
     * @return the generated JWT token as a String
     */
    public String createToken(User user) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + 1000 * 60 * 60);

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .withClaim("role", user.getRole())
                .withClaim("teamId", user.getTeam() != null
                        ? user.getTeam().getId()
                        : null
                )
                .sign(Algorithm.HMAC256(secretKey));
    }

    /**
     * Validates the given JWT token and returns an Authentication object.
     *
     * @param token the JWT token to validate
     * @return an Authentication object containing user details
     */
    public Authentication validate(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        DecodedJWT jwt = verifier.verify(token);

        UserRequest user = UserRequest.builder()
                .username(jwt.getSubject())
                .firstName(jwt.getClaim("firstName").asString())
                .lastName(jwt.getClaim("lastName").asString())
                .teamId(jwt.getClaim("teamId").asLong())
                .role(Role.valueOf(jwt.getClaim("role").asString()))
                .build();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));

    }

    /**
     * Validates the given JWT token against the database and returns an Authentication object.
     *
     * @param token the JWT token to validate
     * @return an Authentication object containing user details from the database
     */
    public Authentication validateAgainstDB(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        DecodedJWT jwt = verifier.verify(token);
        User user = userService.getUserByUsername(jwt.getSubject());
        return new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }

}
