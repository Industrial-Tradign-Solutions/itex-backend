alter table t_clients
    alter column code type varchar(3) using code::varchar(3);

alter table t_clients
    alter column nit type varchar(35) using nit::varchar(35);

alter table t_clients
    alter column phone_country_code type varchar(3) using phone_country_code::varchar(3);

alter table t_clients
    alter column phone_city_code type varchar(3) using phone_city_code::varchar(3);

alter table t_clients
    alter column phone_number type varchar(15) using phone_number::varchar(15);

alter table t_clients_contacts_phones
    alter column country_code type varchar(3) using country_code::varchar(3);

alter table t_clients_contacts_phones
    alter column city_code type varchar(3) using city_code::varchar(3);

alter table t_clients_contacts_phones
    alter column phone_number type varchar(15) using phone_number::varchar(15);

alter table t_clients_contacts_phones
    alter column ext type varchar(5) using ext::varchar(5);


alter table t_suppliers
    alter column tax_id type varchar(35) using tax_id::varchar(35);

alter table t_suppliers
    alter column phone_country_code type varchar(3) using phone_country_code::varchar(3);

alter table t_suppliers
    alter column phone_city_code type varchar(3) using phone_city_code::varchar(3);

alter table t_suppliers
    alter column phone_number type varchar(15) using phone_number::varchar(15);

alter table t_suppliers_contacts_phones
    alter column country_code type varchar(3) using country_code::varchar(3);

alter table t_suppliers_contacts_phones
    alter column city_code type varchar(3) using city_code::varchar(3);

alter table t_suppliers_contacts_phones
    alter column phone_number type varchar(15) using phone_number::varchar(15);

alter table t_suppliers_contacts_phones
    alter column ext type varchar(5) using ext::varchar(5);

create table t_user_departments
(
    id_user uuid not null constraint t_user_departments_t_users_id_fk references t_users,
    id_department uuid not null constraint t_user_departments_t_departments_id_fk references t_departments,
    constraint t_user_departments_pk primary key (id_user, id_department)
);

INSERT INTO t_user_departments (id_department, id_user) SELECT department_id, id FROM t_users;

alter table t_users
    drop column department_id;
