package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.SignupCredentials;
import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert UserEntity to User and vice versa.
 */
@Component
public class UserMapper {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    /**
     * Constructor for UserMapper.
     *
     * @param modelMapper the ModelMapper instance to use for mapping
     * @param userRepository the UserRepository instance to use for fetching UserEntity by ID
     */
    @Autowired
    public UserMapper(ModelMapper modelMapper, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    /**
     * Maps a UserEntity to a User without associating it with a Team.
     *
     * @param userEntity the UserEntity to map
     * @return the mapped User, or null if the input is null
     */
    public User mapUser(UserEntity userEntity) {
        return mapUser(userEntity, null);
    }

    /**
     * Maps a UserEntity to a User, associating it with a Team.
     *
     * @param userEntity the UserEntity to map
     * @param team the Team to associate with the User
     * @return the mapped User, or null if the input is null
     */
    public User mapUser(UserEntity userEntity, Team team) {
        if (userEntity == null) {
            return null;
        }

        return User.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .team(team)
                .role(userEntity.getRole().name())
              //  .password(userEntity.getPasswordHash())
                .build();
    }

    /**
     * Maps a User to a UserEntity.
     *
     * @param user the User to map
     * @return the mapped UserEntity, or null if the input is null
     */
    public UserEntity mapUserEntity(User user) {
        if (user == null) {
            return null;
        }

        return modelMapper.map(user, UserEntity.UserEntityBuilder.class).build();
    }

    /**
     * Maps SignupCredentials to a UserEntity.
     *
     * @param credentials the SignupCredentials to map
     * @return the mapped UserEntity, or null if the input is null
     */
    public UserEntity signupCredentialsToUserEntity(SignupCredentials credentials) {
        if (credentials == null) {
            return null;
        }

        return UserEntity.builder()
                .firstName(credentials.firstName())
                .lastName(credentials.lastName())
                .username(credentials.username())
                .team(null)
                .role(Role.ROLE_USER)
                .build();
    }

    /**
     * Maps a user ID to a UserEntity.
     *
     * @param userId the ID of the user to map
     * @return the mapped UserEntity, or null if the ID is null or not found
     */
    public UserEntity mapUserEntity(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }
}
