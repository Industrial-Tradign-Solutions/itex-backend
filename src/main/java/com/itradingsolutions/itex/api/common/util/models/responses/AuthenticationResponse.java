package com.itradingsolutions.itex.api.common.util.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private UUID id;
    private String email;
    private String user;
    private String token;
    private String fullName;
    private String expirationToken;
    private UUID roleId;
    private String role;
    private boolean passChanged;
}