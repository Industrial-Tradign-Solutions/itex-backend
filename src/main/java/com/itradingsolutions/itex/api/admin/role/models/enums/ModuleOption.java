package com.itradingsolutions.itex.api.admin.role.models.enums;

import lombok.Getter;

@Getter
public enum ModuleOption {

    NOT_MODULE(0L, "Not Module", null),
    /*----------Admin Category---------*/
    CATEGORY_ADMINISTRATION(1000L, "Administration", NOT_MODULE),
    USERS(1001L, "Users", CATEGORY_ADMINISTRATION),
    ROLES_AND_PERMISSIONS(1002L, "Roles & Permissions", CATEGORY_ADMINISTRATION),
    /*---------------------------------*/

    /*----------Masters Category---------*/
    CATEGORY_MASTERS(2000L, "Masters", NOT_MODULE),
    DEPARTMENTS(2001L, "Departments", CATEGORY_MASTERS),
    LOCATIONS(2002L, "Locations", CATEGORY_MASTERS),
    INDUSTRIES(2003L, "Industries", CATEGORY_MASTERS),
    BRANDS(2004L, "Brands", CATEGORY_MASTERS),
    /*----------------------------------*/

    /*----------Partners Category---------*/
    CATEGORY_PARTNERS(3000L, "Partners", NOT_MODULE),
    CLIENTS(3001L, "Clients", CATEGORY_PARTNERS),
    SUPPLIERS(3002L, "Suppliers", CATEGORY_PARTNERS),
    /*----------------------------------*/

    /*----------Industrial Purchase Category---------*/
    CATEGORY_IP(4000L, "Industrial Purchase", NOT_MODULE),
    IP_PRODUCTS(4001L, "IP Products", CATEGORY_IP),
    IP_QUOTE_REQUESTS(4002L, "IP Quote Requests", CATEGORY_IP),
    IP_QUOTATIONS(4003L, "IP Quotations", CATEGORY_IP),
    /*----------------------------------*/

    ;
    private final Long id;
    private final String optionName;
    private final ModuleOption category;

    ModuleOption(Long id, String optionName, ModuleOption category) {
        this.id = id;
        this.optionName = optionName;
        this.category = category;
    }
}
