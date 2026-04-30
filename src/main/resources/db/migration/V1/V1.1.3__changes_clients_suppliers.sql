alter table t_suppliers_contacts
    alter column title type varchar(255) using title::varchar(255);

alter table t_suppliers_contacts
    alter column name type varchar(255) using name::varchar(255);

alter table t_suppliers_contacts
    alter column title drop not null;

alter table t_suppliers_contacts
    alter column email drop not null;

alter table t_suppliers_contacts_phones
    alter column phone_number drop not null;

alter table t_clients_contacts
    alter column title drop not null;

alter table t_clients_contacts
    alter column email drop not null;

alter table t_clients_contacts_phones
    alter column phone_number drop not null;

INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (3001004, 'Change Target Client Info', 'Allows you to change Target Client Info', 3001, true, now());