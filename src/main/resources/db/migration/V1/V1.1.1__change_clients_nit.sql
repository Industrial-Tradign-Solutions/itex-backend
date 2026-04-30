alter table t_clients
    drop constraint client_nit_unique;

alter table t_clients
    rename column nit to tax_id;

create unique index client_tax_id_unique
    on t_clients (tax_id);