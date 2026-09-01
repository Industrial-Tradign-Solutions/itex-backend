package com.itradingsolutions.itex.api.masters.department.models.enums;

import lombok.Getter;

import java.util.UUID;

@Getter
public enum Departments {
    IP("e25ba53d-0d35-473e-b8da-99eb84c06c35", "INDUSTRIAL PURCHASES"),
    ACC("72214552-2ddc-417f-b58e-05f28eabc80f", "ACCOUNTING"),
    IT("3470959b-7f1f-4ec8-a03d-bd9a29dedcb0", "SYSTEMS AND TECHNOLOGY"),
    IF("3febfa66-2948-4b5a-af30-81dbc9402045", "INLAND FREIGHT"),
    RM("b4ceccbc-3de5-419f-9cb7-58633eba51ce", "RAW MATERIALS"),
    LO("38dd94f1-6583-4a00-b290-9ec2e630574f", "LOGISTICS OPERATIONS")
    ;
    private final UUID departmentId;
    private final String name;

    Departments(final String departmentId, final String name) {
        this.departmentId = UUID.fromString(departmentId);
        this.name = name;
    }
}
