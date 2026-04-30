package com.itradingsolutions.itex.api.admin.user.models.requests;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -5239498997217827437L;

    @NotNull(message = "The role field cannot be empty")
    private UUID roleId;

    @Size(min = 1, message = "Minimum one department")
    private List<UUID> departmentsIds;

    @NotBlank(message = "The name field cannot be empty")
    private String name;

    @NotBlank(message = "The username field cannot be empty")
    private String user;

    @NotBlank(message = "The last name field cannot be empty")
    private String lastName;

    @NotBlank(message = "The email field cannot be empty")
    @Email(message = "You must enter a valid email address")
    private String email;

    private String emailPassword;

    @NotBlank(message = "The title field cannot be empty")
    private String title;

    @NotBlank(message = "The extension field cannot be empty")
    private String extension;
}