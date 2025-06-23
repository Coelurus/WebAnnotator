package cz.cuni.mff.vopalenf.annotator.security;

import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of UserDetailsService to load user-specific data.
 * This service is used by Spring Security to retrieve user details for authentication and authorization.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor for UserDetailsServiceImpl.
     *
     * @param userRepository the UserRepository instance to use for fetching UserEntity by username
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(
                userEntity.getUsername(),
                userEntity.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(userEntity.getRole().name()))
        );
    }
}
