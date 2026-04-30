alter table t_ip_products
    alter column mfr_reference type varchar(100) using mfr_reference::varchar(100);

alter table t_ip_products
    add client_reference varchar(100);
