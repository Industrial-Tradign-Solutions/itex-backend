package com.itradingsolutions.itex.api.common.consecutive.models.entities;


import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ConsecutiveId implements Serializable {

    @Serial
    private static final long serialVersionUID = -9212272742900889998L;

    @Column(name = "department", nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private ConsecutiveDepartment department;

    @Column(name = "client_code", nullable = false, length = 3)
    private String clientCode;

    @Column(name = "year", nullable = false, length = 2)
    private String year;

    @Column(name = "month", nullable = false, length = 2)
    private String month;

    @Column(name = "number", nullable = false)
    private int number;

    @Column(name = "module", nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private ConsecutiveModule module;
}
