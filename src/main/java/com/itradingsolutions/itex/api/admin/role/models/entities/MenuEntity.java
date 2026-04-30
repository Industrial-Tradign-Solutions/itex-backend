package com.itradingsolutions.itex.api.admin.role.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "t_menus")
@Getter
@Setter
public class MenuEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6631703446289156499L;

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false, length = 150, unique = true)
    private String name;

    @Column(name = "url", nullable = false, length = 150, unique = true)
    private String url;

    @Column(name = "icon", nullable = false, length = 150)
    private String icon;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_main_option", nullable = false)
    private boolean mainOption;

    @Column(name = "position", nullable = false)
    private int position;

    @JoinColumn(name = "main_menu_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MenuEntity mainMenu;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mainMenu")
    private List<MenuEntity> items;

    @Column(name = "docs_url")
    private String docsUrl;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    protected void prePersist() {
        this.setCreatedAt(ZonedDateTime.now());
    }
}
