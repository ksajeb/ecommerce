package com.ecommerce.project.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min = 3,message = "Username must contain at least 3 characters.")
    private String username;

    @NotBlank
    @Email
    @Size(max = 25)
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 8,max = 40)
    private String password;

}
