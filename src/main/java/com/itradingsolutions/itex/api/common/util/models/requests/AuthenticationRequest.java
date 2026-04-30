package com.itradingsolutions.itex.api.common.util.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "The username cannot be empty")
    private String username;
    @NotBlank(message = "The password cannot be empty")
    private String password;
}
