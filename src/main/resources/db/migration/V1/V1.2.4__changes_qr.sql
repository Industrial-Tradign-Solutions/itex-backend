alter table t_ip_quote_requests
    drop column gross_weight_lbs;

alter table t_ip_quote_requests
    drop column other_charges;

alter table t_ip_quote_requests
    alter column currency set not null;

alter table t_ip_quote_requests
    alter column client_id set not null;

create table t_ip_quote_requests_other_charges (
    id uuid not null primary key,
    ip_qr_id uuid not null references t_ip_quote_requests,
    description varchar(150) not null,
    value numeric(15,2) not null default 0,
    created_at timestamp not null
);

alter table t_ip_quote_requests_other_charges
    add constraint t_ip_quote_requests_other_charges_unique_description
        unique (ip_qr_id, description);

alter table t_ip_quote_request_products
    add constraint t_ip_quote_request_products_unique_product
        unique (ip_qr_id, ip_product_id);
