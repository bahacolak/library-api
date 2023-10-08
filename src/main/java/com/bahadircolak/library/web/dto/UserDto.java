package com.bahadircolak.library.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotBlank (message = "Username is required")
    private String username;
    @NotBlank (message = "Password is required")
    private String password;
    @NotBlank (message = "Email is required")
    private String email;
}
