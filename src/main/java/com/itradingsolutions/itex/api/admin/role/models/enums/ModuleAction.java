package com.itradingsolutions.itex.api.admin.role.models.enums;

import lombok.Getter;

@Getter
public enum ModuleAction {

    /*USERS MODULE ACTIONS*/
    CREATE_USER (1001001L, ModuleOption.USERS, "Create Users"),
    UPDATE_USER (1001002L, ModuleOption.USERS, "Update Users"),
    ENABLE_USER (1001003L, ModuleOption.USERS, "Enable Users"),
    DISABLE_USER (1001004L, ModuleOption.USERS, "Disable Users"),
    RESET_PASS_USER (1001005L, ModuleOption.USERS, "Reset Password Users"),
    CLOSE_ALL_SESSIONS (1001006L, ModuleOption.USERS, "Close All Sessions"),

    /*ROLES MODULE ACTIONS*/
    CREATE_ROLE (1002001L, ModuleOption.ROLES_AND_PERMISSIONS, "Create Roles"),
    UPDATE_ROLE (1002002L,  ModuleOption.ROLES_AND_PERMISSIONS, "Update Roles"),
    ENABLE_ROLE (1002003L, ModuleOption.ROLES_AND_PERMISSIONS, "Enable Roles"),
    DISABLE_ROLE (1002004L, ModuleOption.ROLES_AND_PERMISSIONS, "Disable Roles"),
    UPDATE_ROLE_ACTIONS (1002005L, ModuleOption.ROLES_AND_PERMISSIONS, "Update Role Actions"),
    UPDATE_ROLE_MENUS(1002006L, ModuleOption.ROLES_AND_PERMISSIONS, "Update Role Menus"),

    /*DEPARTMENTS MODULE ACTIONS*/
    CREATE_DEPARTMENT (2001001L, ModuleOption.DEPARTMENTS, "Create Departments"),
    UPDATE_DEPARTMENT (2001002L,  ModuleOption.DEPARTMENTS, "Update Departments"),
    ENABLE_DEPARTMENT (2001003L, ModuleOption.DEPARTMENTS, "Enable Departments"),
    DISABLE_DEPARTMENT (2001004L, ModuleOption.DEPARTMENTS, "Disable Departments"),

    /*LOCATIONS MODULE ACTIONS*/
    CREATE_COUNTRY (2002101L, ModuleOption.LOCATIONS, "Create Countries"),
    UPDATE_COUNTRY (2002102L, ModuleOption.LOCATIONS, "Update Countries"),
    CREATE_STATE (2002201L, ModuleOption.LOCATIONS, "Create State"),
    UPDATE_STATE (2002202L, ModuleOption.LOCATIONS, "Update State"),
    CREATE_CITY (2002301L, ModuleOption.LOCATIONS, "Create City"),
    UPDATE_CITY (2002302L, ModuleOption.LOCATIONS, "Update City"),

    /*INDUSTRIES MODULE ACTIONS*/
    CREATE_INDUSTRY (2003001L, ModuleOption.INDUSTRIES, "Create Industries"),
    UPDATE_INDUSTRY (2003002L,  ModuleOption.INDUSTRIES, "Update Industries"),
    ENABLE_INDUSTRY (2003003L, ModuleOption.INDUSTRIES, "Enable Industries"),
    DISABLE_INDUSTRY (2003004L, ModuleOption.INDUSTRIES, "Disable Industries"),

    /*CLIENTS MODULE ACTIONS*/
    CREATE_CLIENT (3001001L, ModuleOption.CLIENTS, "Create Client"),
    UPDATE_CLIENT (3001002L,  ModuleOption.CLIENTS, "Update Client"),
    CHANGE_STATUS_CLIENT (3001003L, ModuleOption.CLIENTS, "Change Status Client"),
    CHANGE_TARGET_CLIENT_INFO (3001004L, ModuleOption.CLIENTS, "Change Target Client Info"),

    /*SUPPLIER MODULE ACTIONS*/
    CREATE_SUPPLIER (3002001L, ModuleOption.SUPPLIERS, "Create Supplier"),
    UPDATE_SUPPLIER (3002002L,  ModuleOption.SUPPLIERS, "Update Supplier"),
    CHANGE_STATUS_SUPPLIER (3002003L, ModuleOption.SUPPLIERS, "Change Status Supplier"),

    /*BRANDS MODULE ACTIONS*/
    CREATE_BRAND (2004001L, ModuleOption.BRANDS, "Create Brands"),
    ENABLE_BRAND (2004002L, ModuleOption.BRANDS, "Enable Brands"),
    DISABLE_BRAND (2004003L, ModuleOption.BRANDS, "Disable Brands"),
    IMPORT_EXCEL_BRAND (2004004L, ModuleOption.BRANDS, "Import Excel Brands"),
    DELETE_SUPPLIER_BRAND (2004005L, ModuleOption.BRANDS, "Unassign Supplier By Brand ID"),

    /*IP PRODUCTS MODULE ACTIONS*/
    CREATE_IP_PRODUCT (4001001L, ModuleOption.IP_PRODUCTS, "Create Product"),
    ENABLE_IP_PRODUCT (4001002L, ModuleOption.IP_PRODUCTS, "Enable Product"),
    DISABLE_IP_PRODUCT (4001003L, ModuleOption.IP_PRODUCTS, "Disable Product"),
    UPDATE_IP_PRODUCT (4001004L, ModuleOption.IP_PRODUCTS, "Update Product"),
    VIEW_HISTORY_IP_PRODUCT (4001005L, ModuleOption.IP_PRODUCTS, "View History Product"),
    REPLACE_IP_PRODUCT (4001006L, ModuleOption.IP_PRODUCTS, "Replace product"),
    IMPORT_IP_PRODUCT (4001007L, ModuleOption.IP_PRODUCTS, "Import product"),

    /*IP QUOTE_REQUEST MODULE ACTIONS*/
    CREATE_IP_QUOTE_REQUESTS (4002001L, ModuleOption.IP_QUOTE_REQUESTS, "Create QR"),
    UPDATE_IP_QUOTE_REQUESTS (4002002L, ModuleOption.IP_QUOTE_REQUESTS, "Update QR"),
    VIEW_HISTORY_IP_QUOTE_REQUESTS (4002003L, ModuleOption.IP_QUOTE_REQUESTS, "View History QR"),
    //Usable el 4002004
    CLONE_IP_QUOTE_REQUESTS (4002005L, ModuleOption.IP_QUOTE_REQUESTS, "Clone QR"),
    REJECT_IP_QUOTE_REQUESTS (4002006L, ModuleOption.IP_QUOTE_REQUESTS, "Reject QR"),
    //Usable el 4002007
    EDIT_PAYMENT_TERMS_IP_QUOTE_REQUESTS (4002008L, ModuleOption.IP_QUOTE_REQUESTS, "Edit Payment Terms from QR"),

    /*IP QUOTATIONS MODULE ACTIONS*/
    CREATE_IP_QUOTATIONS (4003001L, ModuleOption.IP_QUOTATIONS, "Create Q"),
    UPDATE_IP_QUOTATIONS (4003002L, ModuleOption.IP_QUOTATIONS, "Update Q"),
    VIEW_HISTORY_IP_QUOTATIONS (4003003L, ModuleOption.IP_QUOTATIONS, "View History Q"),
    CLONE_IP_QUOTATIONS (4003004L, ModuleOption.IP_QUOTATIONS, "Clone Q"),
    REJECT_IP_QUOTATIONS (4003005L, ModuleOption.IP_QUOTATIONS, "Reject Q"),
    EDIT_PAYMENT_TERMS_IP_QUOTATIONS (4003006L, ModuleOption.IP_QUOTATIONS, "Edit Payment Terms from Q"),

    /*IP PURCHASE ORDERS MODULE ACTIONS*/
    CREATE_PURCHASE_ORDER (4004001L, ModuleOption.IP_PURCHASE_ORDERS, "Create Purchase Order"),
    UPDATE_PURCHASE_ORDER (4004002L, ModuleOption.IP_PURCHASE_ORDERS, "Update Purchase Order"),
    VIEW_HISTORY_PURCHASE_ORDER (4004003L, ModuleOption.IP_PURCHASE_ORDERS, "View History Purchase Order"),
    REJECT_PURCHASE_ORDER (4004004L, ModuleOption.IP_PURCHASE_ORDERS, "Reject Purchase Order"),
    EDIT_PAYMENT_TERMS_PURCHASE_ORDER (4004005L, ModuleOption.IP_PURCHASE_ORDERS, "Edit Payment Terms Purchase Order"),
    ;
    private final Long id;
    private final ModuleOption moduleOption;
    private final String name;

    ModuleAction(Long id, ModuleOption moduleOption, String name) {
        this.id = id;
        this.moduleOption = moduleOption;
        this.name = name;
    }
}
