package com.itradingsolutions.itex.api.common.util.models.enums;


import java.util.Arrays;
import java.util.List;

public class UniqueDB {

    private UniqueDB() {}

    public static final String ROLE_NAME = "role_unique_name";
    public static final String DEPARTMENT_NAME = "department_unique_name";
    public static final String USER_USERNAME = "user_unique_username";
    public static final String INDUSTRY_NAME = "industry_unique_name";
    public static final String COUNTRY_NAME_SHORT = "countries_unique_name_short";
    public static final String COUNTRY_NAME = "countries_unique_name";
    public static final String COUNTRY_LATITUDE_LONGITUDE = "countries_unique_latitude_longitude";
    public static final String STATE_LATITUDE_LONGITUDE = "states_unique_latitude_longitude";
    public static final String STATE_NAME = "states_unique_name";
    public static final String STATE_NAME_SHORT = "states_unique_name_short";
    public static final String CITY_LATITUDE_LONGITUDE = "cities_unique_latitude_longitude";
    public static final String CITY_NAME = "cities_unique_name";
    public static final String CLIENT_DEPARTMENT_INFO = "client_department_unique";
    public static final String CLIENT_CODE = "client_code_unique";
    public static final String CLIENT_TAX_ID = "client_tax_id_unique";
    public static final String SUPPLIER_DEPARTMENT_INFO = "supplier_department_unique";
    public static final String SUPPLIER_TAX_ID = "supplier_tax_id_unique";
    public static final String BRAND_NAME = "brand_unique_name";
    public static final String IP_QR_NUMBER = "ip_qr_unique_number";
    public static final String IP_QR_CLONED_ID = "t_ip_quote_requests_cloned_clone_qr_id_unique";
    public static final String IP_QR_PRODUCT_UNIQUE = "t_ip_quote_request_products_unique_product";
    public static final String IP_QR_OTHER_CHARGES_UNIQUE = "t_ip_quote_requests_other_charges_unique_description";
    public static final String IP_Q_NUMBER = "ip_q_unique_number";
    public static final String IP_Q_QR_UNIQUE = "ip_q_qr_unique";
    public static final String IP_Q_OTHER_CHARGES_UNIQUE = "t_ip_quotation_other_charges_unique_description";
    public static final String IP_Q_CLONED_ID = "t_ip_quotations_cloned_clone_q_id_unique";


    public static List<String> getListErrors() {
        return Arrays.asList(
                ROLE_NAME,
                DEPARTMENT_NAME,
                USER_USERNAME,
                INDUSTRY_NAME,
                COUNTRY_NAME_SHORT,
                COUNTRY_NAME,
                COUNTRY_LATITUDE_LONGITUDE,
                STATE_LATITUDE_LONGITUDE,
                STATE_NAME,
                STATE_NAME_SHORT,
                CITY_LATITUDE_LONGITUDE,
                CITY_NAME,
                CLIENT_DEPARTMENT_INFO,
                CLIENT_CODE,
                SUPPLIER_DEPARTMENT_INFO,
                CLIENT_TAX_ID,
                SUPPLIER_TAX_ID,
                BRAND_NAME,
                IP_QR_NUMBER,
                IP_QR_CLONED_ID,
                IP_QR_PRODUCT_UNIQUE,
                IP_QR_OTHER_CHARGES_UNIQUE,
                IP_Q_NUMBER,
                IP_Q_QR_UNIQUE,
                IP_Q_OTHER_CHARGES_UNIQUE,
                IP_Q_CLONED_ID
        );
    }
}

