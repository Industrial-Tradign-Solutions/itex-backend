alter table t_ip_quote_requests
    drop column client_address;

alter table t_ip_quote_requests
    drop column supplier_address;

alter table t_ip_quote_request_products
    drop column dual_use;

alter table t_ip_quote_request_products
    add lead_time_type varchar(10) default 'WEEKS' not null;

alter table t_ip_quote_request_products
    drop column product_description;

alter table t_ip_quote_request_products
    drop column product_client_description;

alter table t_ip_quote_request_products
    drop column product_mfr_reference;

alter table t_ip_quote_request_products
    drop column product_client_reference;

alter table t_ip_quote_request_products
    drop column product_net_weight_lbs;

alter table t_ip_quote_request_products
    drop column product_hts_schedule_b_number;

alter table t_ip_quote_request_products
    drop column product_eccn;

alter table t_ip_quote_request_products
    alter column unit_price type numeric(15, 5) using unit_price::numeric(15, 5);

alter table t_ip_quote_requests_other_charges
    alter column value type numeric(15, 5) using value::numeric(15, 5);

alter table t_ip_quote_requests
    alter column freight_charges type numeric(15, 5) using freight_charges::numeric(15, 5);
