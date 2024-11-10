package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import cz.cuni.mff.vopalenf.annotator.mapper.TeamMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final TeamMapper teamMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       TeamMapper teamMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.teamMapper = teamMapper;
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
}
