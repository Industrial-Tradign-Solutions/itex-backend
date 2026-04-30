/*CREACION DE TABLAS DE CLIENTES*/
create table t_clients (
    id uuid not null primary key,
    code varchar(4) not null constraint client_code_unique unique,
    name varchar(300) not null,
    nit bigint,

    status varchar(25) not null,
    language varchar(30) not null,
    payment_method varchar(40) not null,
    payment_terms varchar(40),

    address varchar(500),
    city_id uuid references t_cities,
    zip_code varchar(30),

    notes TEXT,
    industry_id uuid references t_industries,
    web_site varchar(200),

    phone_country_code integer,
    phone_city_code integer,
    phone_number bigint,

    created_at timestamp  not null,
    created_by_user_id uuid not null references t_users,

    open_at timestamp ,
    open_by_user_id uuid references t_users,

    updated_at timestamp ,
    updated_by_user_id uuid references t_users,

    change_prospect_to_client_at timestamp ,
    change_prospect_to_client_by_user_id uuid references t_users,
    constraint client_nit_unique unique (nit)
);
create table t_clients_info_by_department (
    id uuid not null primary key,
    department_id uuid not null references t_departments,
    client_id uuid not null references t_clients,
    notes TEXT,
    target numeric(15,3) not null,
    account_rep uuid references t_users,
    created_at timestamp(6)  not null,
    constraint client_department_unique unique (client_id, department_id)
);
create table t_clients_contacts (
    id uuid not null primary key,
    title varchar(50) not null,
    name varchar(50) not null,
    email varchar(150) not null,
    is_main boolean default false not null,
    is_active boolean default true not null,
    client_info_dep_id uuid not null references t_clients_info_by_department,
    created_at timestamp  not null
);
create table t_clients_contacts_phones (
    id uuid not null primary key ,
    type varchar(10) not null,
    country_code integer,
    city_code integer,
    phone_number bigint not null,
    ext integer,
    is_main boolean not null,
    client_contact_id uuid not null references t_clients_contacts,
    created_at timestamp  not null
);
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Configuracion de modulos iniciales para partners*/
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (3000, now(), true, 'System partners category', '', 'Partners', '/p/partners', null, TRUE, 8);
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (3001, now(), true, 'Clients management module', 'pi pi-users', 'Clients', '/p/partners/clients', 3000, FALSE, 2);
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Acciones modulo clients*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (3001001, 'Create Client', 'Allows you to create a Client', 3001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (3001002, 'Update Client', 'Allows you to update a Client', 3001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (3001003, 'Change status Client', 'Allows you to change status a Client', 3001, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/