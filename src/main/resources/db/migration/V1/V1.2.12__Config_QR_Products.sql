alter table t_ip_quote_request_products
    alter column lead_time drop not null;

alter table t_ip_quote_request_products
    alter column unit_price drop not null;

alter table t_ip_quote_request_products
    alter column lead_time_type drop not null;
