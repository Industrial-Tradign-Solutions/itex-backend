alter table t_clients_contacts
    alter column title type varchar(255) using title::varchar(255);

alter table t_clients_contacts
    alter column name type varchar(255) using name::varchar(255);
