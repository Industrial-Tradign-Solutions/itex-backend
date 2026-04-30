alter table t_ip_products
    add dual_use boolean default false not null;

alter table t_ip_products
    drop constraint ip_product_unique_description;
