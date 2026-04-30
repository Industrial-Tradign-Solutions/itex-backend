
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
    VALUES (4002, now(), true, 'Industrial Purchase Quote Request module', 'pi pi-file-o', 'Quote Requests', '/p/ip/qr', 4000, FALSE, 1, 'https://jdbayer.notion.site/IP-Quote-Request-24adb6690de7803ba69aca0bb16681a0');

/*Acciones modulo Quote Request*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4002001, 'Create Quote Request', 'Allows you to create a Quote Request', 4002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4002002, 'Update Quote Request', 'Allows you to update a Quote Request', 4002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4002003, 'View History Quote Request', 'Allows you to view history a Quote Request', 4002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4002004, 'Change consecutive Quote Request', 'Change consecutive a Quote Request', 4002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4002005, 'Clone Quote Request', 'Clone a Quote Request', 4002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4002006, 'Reject Quote Request', 'Reject a Quote Request', 4002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4002007, 'Create Quotation from Quote Request', 'Create Quotation from Quote Request', 4002, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/
create table t_ip_quote_requests (
    id uuid not null primary key,
    number varchar(20) not null constraint ip_qr_unique_number unique,
    created_at timestamp not null,
    status varchar(20) not null,
    currency varchar(20),
    client_id uuid references t_clients,
    client_address varchar(500),
    client_contact_id uuid references t_clients_contacts,
    client_qr_number varchar(50),
    client_call text,
    sales_rep_id uuid not null references t_users,
    supplier_id uuid references t_suppliers,
    supplier_address varchar(500),
    supplier_contact_id uuid references t_suppliers_contacts,
    supplier_qr_number varchar(50),
    supplier_call text,
    remarks text,
    gross_weight_lbs numeric(15,2) not null default 0,
    shipping_point_zip_code varchar(50),
    freight_class varchar(150),
    fob_shipping_point varchar(150),
    payment_terms varchar(40),
    freight_charges numeric(15,2) not null default 0,
    other_charges numeric(15,2) not null default 0,
    open_at timestamp ,
    open_by_user_id uuid references t_users
);

create table t_ip_quote_request_products (
    id uuid not null primary key,
    ip_qr_id uuid not null references t_ip_quote_requests,
    ip_product_id uuid not null references t_ip_products,
    number int not null,
    quantity numeric(15,2) not null default 0,
    unit_type varchar(50),
    lead_time int not null default 0,
    unit_price numeric(15,2) not null default 0,
    dual_use boolean not null default false,
    created_at timestamp not null
);

create table t_ip_quote_request_history (
    id uuid not null primary key,
    ip_qr_id uuid not null references t_ip_quote_requests,
    user_id uuid not null references t_users,
    action varchar(50) not null,
    created_at timestamp not null,
    data json
);

