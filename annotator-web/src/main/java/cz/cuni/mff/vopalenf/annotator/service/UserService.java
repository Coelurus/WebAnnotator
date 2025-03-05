package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.LoginCredentials;
import cz.cuni.mff.vopalenf.annotator.api.model.SignupCredentials;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import cz.cuni.mff.vopalenf.annotator.exception.api.BadRequestException;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.mapper.TeamMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final TeamMapper teamMapper;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       TeamMapper teamMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.teamMapper = teamMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .map(userEntity -> userMapper.mapUser(
                                userEntity,
                                teamMapper.mapTeam(
                                        userEntity.getTeam()
                                )
                        ))
                        .toList()
        );
    }

    public User getUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with " + username + " not found", UserService.class.getSimpleName()));
        return userMapper.mapUser(user);
    }

    public User login(LoginCredentials credentials) {
        UserEntity userEntity = userRepository.findByUsername(credentials.username())
                .orElseThrow(() -> new NotFoundException("Unknown user", UserService.class.getSimpleName()));

        if (passwordEncoder.matches(CharBuffer.wrap(credentials.password()), userEntity.getPasswordHash())) {
            return userMapper.mapUser(userEntity);
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    public User signup(SignupCredentials credentials) {
        Optional<UserEntity> existingUser = userRepository.findByUsername(credentials.username());
        if (existingUser.isPresent()) {
            throw new BadRequestException("Username already exists", UserService.class.getSimpleName());
        }

        UserEntity user = userMapper.signupCredentialsToUserEntity(credentials);
        user.setPasswordHash(passwordEncoder.encode(CharBuffer.wrap(credentials.password())));

        UserEntity createdUser = userRepository.save(user);
        return userMapper.mapUser(createdUser);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
