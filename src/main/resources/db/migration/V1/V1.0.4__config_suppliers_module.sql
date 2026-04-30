/*CREACION DE TABLAS DE CLIENTES*/
create table t_suppliers (
    id uuid not null primary key,
    name varchar(300) not null,
    tax_id bigint,

    status varchar(25) not null,
    language varchar(30) not null,
    payment_method varchar(40) not null,
    payment_terms varchar(40),
    delivery_by varchar(40),

    address varchar(500),
    city_id uuid references t_cities,
    zip_code varchar(30),

    notes TEXT,
    wire_ach_instructions TEXT,
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
    constraint supplier_tax_id_unique unique (tax_id)
);
create table t_suppliers_info_by_department (
    id uuid not null primary key,
    department_id uuid not null references t_departments,
    supplier_id uuid not null references t_suppliers,
    notes TEXT,
    created_at timestamp(6)  not null,
    constraint supplier_department_unique unique (supplier_id, department_id)
);
create table t_suppliers_contacts (
    id uuid not null primary key,
    title varchar(50) not null,
    name varchar(50) not null,
    email varchar(150) not null,
    is_main boolean default false not null,
    is_active boolean default true not null,
    supplier_info_dep_id uuid not null references t_suppliers_info_by_department,
    created_at timestamp  not null
);
create table t_suppliers_contacts_phones (
    id uuid not null primary key ,
    type varchar(10) not null,
    country_code integer,
    city_code integer,
    phone_number bigint not null,
    ext integer,
    is_main boolean not null,
    supplier_contact_id uuid not null references t_suppliers_contacts,
    created_at timestamp  not null
);
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Configuracion de modulos iniciales para partners*/
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (3002, now(), true, 'Suppliers management module', 'pi pi-truck', 'Suppliers', '/p/partners/suppliers', 3000, FALSE, 3);
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Acciones modulo Supplier*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (3002001, 'Create Supplier', 'Allows you to create a Supplier', 3002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (3002002, 'Update Supplier', 'Allows you to update a Supplier', 3002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (3002003, 'Change Status Supplier', 'Allows you to change status a Supplier', 3002, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/