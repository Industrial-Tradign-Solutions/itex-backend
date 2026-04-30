INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
    VALUES (2004, now(), true, 'Brands management module', 'pi pi-bookmark-fill', 'Brands', '/p/masters/brands', 2000, FALSE, 1, 'https://jdbayer.notion.site/M-dulo-de-Marcas-1bbdb6690de780b2bb58dadcc3b69b31?pvs=4');

/*Acciones modulo Brands*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2004001, 'Create Brand', 'Allows you to create a Brand', 2004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2004002, 'Enable Brand', 'Allows you to enable a Brand', 2004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2004003, 'Disable Brand', 'Allows you to disable a Brand', 2004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2004004, 'Import Excel Brands', 'Allows you to Import Excel Brands', 2004, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/

create table t_brands (
    id uuid not null primary key,
    name varchar(1000) not null constraint brand_unique_name unique,
    is_active boolean default false not null,
    created_at  timestamp not null
);

create table t_suppliers_brands (
    brand_id uuid not null references t_brands,
    supplier_id uuid not null references t_suppliers,
    primary key (brand_id, supplier_id)
);
