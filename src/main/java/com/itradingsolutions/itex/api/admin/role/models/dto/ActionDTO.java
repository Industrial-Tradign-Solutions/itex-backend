package com.itradingsolutions.itex.api.admin.role.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class ActionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 320350888004635153L;

    private Long id;
    private String name;
    private String description;
    private MenuItemDTO menu;
    private boolean active;
    private ZonedDateTime createdAt;
}
