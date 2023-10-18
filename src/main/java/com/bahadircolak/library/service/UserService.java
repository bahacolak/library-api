package com.bahadircolak.library.service;

import com.bahadircolak.library.config.PasswordEncoderService;
import com.bahadircolak.library.model.User;
import com.bahadircolak.library.repository.UserRepository;
import com.bahadircolak.library.web.advice.exception.UserConflictException;
import com.bahadircolak.library.web.advice.exception.UserNotFoundException;
import com.bahadircolak.library.web.dto.UserDto;
import com.bahadircolak.library.web.request.RegisterUserRequest;
import com.bahadircolak.library.web.request.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    public UserService(UserRepository userRepository, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : users){
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setUsername(user.getUsername());
            userDto.setPassword(user.getPassword());
            userDto.setEmail(user.getEmail());
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName()))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }


    public UserDto createUser(RegisterUserRequest request) {

        if (doesUserExist(request.getEmail())) {
            throw new UserConflictException("User with email: " + request.getEmail() + " already exists!");
        }

        String salt = passwordEncoderService.generateSalt();
        String hashedPassword = passwordEncoderService.hash(request.getPassword(), salt);

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        user.setEmail(request.getEmail());

        User savedUser = userRepository.save(user);

        UserDto savedUserDto = new UserDto();
        savedUserDto.setId(savedUser.getId());
        savedUserDto.setFirstName(savedUser.getFirstName());
        savedUserDto.setLastName(savedUser.getLastName());
        savedUserDto.setUsername(savedUser.getUsername());
        savedUserDto.setPassword(savedUser.getPassword());
        savedUserDto.setEmail(savedUser.getEmail());

        return savedUserDto;
    }

    private boolean doesUserExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        Optional<User> existingUserOptional = userRepository.findById(id);

        if (existingUserOptional.isPresent()){
            User existingUser = existingUserOptional.get();

            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            existingUser.setUsername(request.getUsername());
            existingUser.setEmail(request.getEmail());

            existingUser = userRepository.save(existingUser);

            UserDto updatedUserDto = new UserDto();

            updatedUserDto.setId(existingUser.getId());
            updatedUserDto.setFirstName(existingUser.getFirstName());
            updatedUserDto.setLastName(existingUser.getLastName());
            updatedUserDto.setUsername(existingUser.getUsername());
            updatedUserDto.setEmail(existingUser.getEmail());

            return updatedUserDto;
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }
}
