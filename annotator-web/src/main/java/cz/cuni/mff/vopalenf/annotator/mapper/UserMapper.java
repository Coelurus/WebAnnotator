package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.SignupCredentials;
import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User mapUser(UserEntity userEntity) {
        return mapUser(userEntity, null);
    }

    public User mapUser(UserEntity userEntity, Team team) {
        if (userEntity == null) {
            return null;
        }

        return User.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUsername())
                .team(team)
                .role(userEntity.getRole().name())
                .password(userEntity.getPasswordHash())
                .build();
    }

    public UserEntity mapUserEntity(User user) {
        if (user == null) {
            return null;
        }

        return modelMapper.map(user, UserEntity.UserEntityBuilder.class).build();
    }

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
}
