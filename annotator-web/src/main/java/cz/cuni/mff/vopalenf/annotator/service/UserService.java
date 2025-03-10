package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.LoginCredentials;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.SignupCredentials;
import cz.cuni.mff.vopalenf.annotator.api.request.UserRequest;
import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import cz.cuni.mff.vopalenf.annotator.exception.api.BadCredentialsException;
import cz.cuni.mff.vopalenf.annotator.exception.api.BadRequestException;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.mapper.TeamMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.UserMapper;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userEntity -> userMapper.mapUser(
                        userEntity,
                        teamMapper.mapTeam(
                                userEntity.getTeam()
                        )
                ))
                .toList();
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
        throw new BadCredentialsException("Invalid credentials", UserService.class.getSimpleName());
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

    /**
     * Update existing user in db
     *
     * @param userId      ID of a user to update
     * @param userRequest Update payload
     * @return updated user
     */
    public User updateUser(Long userId, UserRequest userRequest) {
        UserEntity userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Cannot update user", UserService.class.getSimpleName()));

        userToUpdate.setFirstName(userRequest.getFirstName());
        userToUpdate.setLastName(userRequest.getLastName());
        userToUpdate.setUsername(userRequest.getUsername());
        userToUpdate.setRole(userRequest.getRole());
        userToUpdate.setTeam(teamMapper.mapTeamEntity(userRequest.getTeamId()));

        return userMapper.mapUser(userRepository.save(userToUpdate));
    }

    public User createUser(UserRequest userRequest) {
        UserEntity newUser = UserEntity.builder()
                .username(userRequest.getUsername())
                .passwordHash(passwordEncoder.encode(CharBuffer.wrap(userRequest.getUsername())))
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .team(teamMapper.mapTeamEntity(userRequest.getTeamId()))
                .role(userRequest.getRole() != null ? userRequest.getRole() : Role.ROLE_USER)
                .build();
        return userMapper.mapUser(userRepository.save(newUser));
    }

    public List<Role> getAllRoles() {
        return List.of(Role.values());
    }
}
