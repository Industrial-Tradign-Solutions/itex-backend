INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (4000, now(), true, 'System Industrial Purchase category', '', 'Industrial Purchase', '/p/ip', null, TRUE, 0);
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
    VALUES (4001, now(), true, 'Industrial Purchase Products module', 'pi pi-box', 'Products', '/p/ip/products', 4000, FALSE, 1, 'https://jdbayer.notion.site/IP-Productos-1fbdb6690de7806a8208c00c8749ff8b?pvs=4');

/*Acciones modulo Productos*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4001001, 'Create Product', 'Allows you to create a Product', 4001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4001002, 'Enable Product', 'Allows you to enable a Product', 4001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4001003, 'Disable Product', 'Allows you to disable a Product', 4001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4001004, 'Update Product', 'Allows you to update a Product', 4001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4001005, 'View History Product', 'Allows you to view history a Product', 4001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (4001006, 'Replace product', 'Allows you to replace a product', 4001, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/

create table t_ip_products (
    id uuid not null primary key,
    brand_id uuid references t_brands,
    description varchar(1000) not null constraint ip_product_unique_description unique,
    client_description varchar(1000),
    mfr_reference int,
    net_weight_lbs numeric(15,3),
    nmfc int,
    freight_class varchar(150),
    status varchar(35) not null,
    notes text,
    keywords text,
    hts_schedule_b_number int,
    eccn varchar(100),
    coo uuid references t_countries,
    is_battery boolean not null default false,
    is_hazmat boolean not null default false,
    created_at timestamp not null,
    open_at timestamp ,
    open_by_user_id uuid references t_users,
    substitute_product_id uuid references t_ip_products
);

create table t_ip_products_surplus (
    id uuid not null primary key,
    product_id uuid not null references t_ip_products,
    quantity numeric(15,3) not null,
    price numeric(15,3),
    wh_number varchar(100),
    location varchar(100),
    type varchar(20) not null,
    created_at timestamp not null
);

create table t_ip_products_history (
    id uuid not null primary key,
    product_id uuid not null references t_ip_products,
    user_id uuid not null references t_users,
    action varchar(50) not null,
    created_at timestamp not null,
    data json
);
