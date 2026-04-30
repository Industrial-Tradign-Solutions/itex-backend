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
public class MenuItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -369136261080413588L;

    private Long id;
    private String name;
    private String url;
    private String icon;
    private String description;
    private boolean active;
    private Integer position;
    private ZonedDateTime createdAt;
    private String docsUrl;
}
