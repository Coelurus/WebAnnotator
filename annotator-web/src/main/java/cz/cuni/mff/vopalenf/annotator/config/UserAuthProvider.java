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

@Component
public class UserAuthProvider {

    private final UserService userService;

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    @Autowired
    public UserAuthProvider(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(User user) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + 1000 * 60 * 60);

        return JWT.create()
                .withSubject(user.getUserName())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC256(secretKey));
    }

    public Authentication validate(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        DecodedJWT jwt = verifier.verify(token);

        UserRequest user = UserRequest.builder()
                .username(jwt.getSubject())
                .firstName(jwt.getClaim("firstName").asString())
                .lastName(jwt.getClaim("lastName").asString())
                .role(Role.valueOf(jwt.getClaim("role").asString()))
                .build();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));

    }

    public Authentication validateAgainstDB(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        DecodedJWT jwt = verifier.verify(token);
        User user = userService.getUserByUsername(jwt.getSubject());
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

}
