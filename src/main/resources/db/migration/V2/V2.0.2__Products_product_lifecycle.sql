alter table t_ip_products
    alter column description set not null;

alter table t_ip_products
    alter column mfr_reference set not null;

alter table t_ip_products
    add constraint ip_product_mfr_reference_unique unique (mfr_reference);