package com.bahadircolak.library.service;

import com.bahadircolak.library.config.PasswordEncoder;
import com.bahadircolak.library.model.User;
import com.bahadircolak.library.repository.UserRepository;
import com.bahadircolak.library.web.advice.UserConflictException;
import com.bahadircolak.library.web.advice.UserNotFoundException;
import com.bahadircolak.library.web.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : users){
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setPassword(user.getPassword());
            userDto.setEmail(user.getEmail());
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getEmail()))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }


    public UserDto createUser(UserDto userDto) {

        if (doesUserExist(userDto.getEmail())) {
            throw new UserConflictException("User with email: " + userDto.getEmail() + " already exists!");
        }

        String salt = passwordEncoder.generateSalt();
        String hashedPassword = passwordEncoder.hash(userDto.getPassword(), salt);

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        user.setEmail(userDto.getEmail());

        User savedUser = userRepository.save(user);

        UserDto savedUserDto = new UserDto();
        savedUserDto.setId(savedUser.getId());
        savedUserDto.setUsername(savedUser.getUsername());
        savedUserDto.setPassword(savedUser.getPassword());
        savedUserDto.setEmail(savedUser.getEmail());

        return savedUserDto;
    }

    private boolean doesUserExist(String email) {
        return userRepository.existsByEmail(email);
    }
}
