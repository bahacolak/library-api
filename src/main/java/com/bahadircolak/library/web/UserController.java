package com.bahadircolak.library.web;

import com.bahadircolak.library.service.UserService;
import com.bahadircolak.library.web.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public List<UserDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto){
        UserDto createdUser = userService.createUser(userDto);
        URI location = URI.create("/api/users/" + createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(createdUser);
    }

//    @PutMapping
//    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto){
//        UserDto updatedUser = userService.updateUser(id, userDto);
//        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
//    }
//
//    public void deleteUser(@PathVariable Long id){
//        userService.deleteUser(id);
//    }

}
