package com.itradingsolutions.itex.api.admin.role.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class MainMenuDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7063982683518531155L;

    private Long id;
    private String name;
    private String url;
    private String icon;
    private String description;
    private boolean active;
    private List<MenuItemDTO> items;
    private ZonedDateTime createdAt;
}
