package com.itradingsolutions.itex.api.admin.user.models.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangePasswordUserRequest {

    @NotBlank(message = "The password field cannot be empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d\\W]{8,}$", message = "The password must be longer than 8 characters with numbers, upper and lower case letters.")
    private String password;

    @NotBlank(message = "The confirm password field cannot be empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d\\W]{8,}$", message = "The confirmation password must be longer than 8 characters with numbers, upper and lower case letters.")
    private String confirmPassword;
}
