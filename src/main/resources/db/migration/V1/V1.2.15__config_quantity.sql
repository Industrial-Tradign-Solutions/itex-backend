alter table t_ip_quote_request_products
alter column quantity type numeric(15, 5) using quantity::numeric(15, 5);
