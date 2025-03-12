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

    /**
     * Get all users from database
     *
     * @return List of all users
     */
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

    /**
     * Get user by their username
     *
     * @param username Username to find user by
     * @return User by given username
     * @throws NotFoundException when username does not exist
     */
    public User getUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with " + username + " not found", UserService.class.getSimpleName()));
        return userMapper.mapUser(user);
    }

    /**
     * Login user to the system and return them token
     *
     * @param credentials Login credentials containing username and password
     * @return User information about logged-in user
     * @throws NotFoundException       when username does not exist
     * @throws BadCredentialsException when the credentials are invalid
     */
    public User login(LoginCredentials credentials) {
        UserEntity userEntity = userRepository.findByUsername(credentials.username())
                .orElseThrow(() -> new NotFoundException("Unknown user", UserService.class.getSimpleName()));

        if (passwordEncoder.matches(CharBuffer.wrap(credentials.password()), userEntity.getPasswordHash())) {
            return userMapper.mapUser(userEntity);
        }
        throw new BadCredentialsException("Invalid credentials", UserService.class.getSimpleName());
    }

    /**
     * Create new account for user
     *
     * @param credentials New user data containing name, username and password
     * @return newly created user
     * @throws BadRequestException when username already exists
     */
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

    /**
     * Delete user from db by ID
     *
     * @param userId ID of user to delete
     * @throws NotFoundException when user with given ID does not exist
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found", UserService.class.getSimpleName());
        }
        userRepository.deleteById(userId);
    }

    /**
     * Update existing user in database
     *
     * @param userId      ID of a user to update
     * @param userRequest Update payload
     * @return updated user
     * @throws NotFoundException when user with given ID does not exist
     */
    public User updateUser(Long userId, UserRequest userRequest) {
        UserEntity userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found", UserService.class.getSimpleName()));

        userToUpdate.setFirstName(userRequest.getFirstName());
        userToUpdate.setLastName(userRequest.getLastName());
        userToUpdate.setUsername(userRequest.getUsername());
        userToUpdate.setRole(userRequest.getRole());
        userToUpdate.setTeam(teamMapper.mapTeamEntity(userRequest.getTeamId()));

        return userMapper.mapUser(userRepository.save(userToUpdate));
    }

    /**
     * Create new user and save it to the database
     *
     * @param userRequest User payload
     * @return newly created user
     * @throws BadRequestException when username already exists
     */
    public User createUser(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists", UserService.class.getSimpleName());
        }

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

    /**
     * Get all roles in app
     *
     * @return List of all roles in app
     */
    public List<Role> getAllRoles() {
        return List.of(Role.values());
    }
}
