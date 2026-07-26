INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
VALUES (
    5000,
    now(),
    true,
    'Systems Sales Category Sales',
    '',
    'Sales',
    '/p/sales',
    null,
    TRUE,
    2,
    null
);
UPDATE t_menus set position = 1 WHERE id = 4000;
UPDATE t_menus set position = 2 WHERE id = 5000;
UPDATE t_menus set position = 3 WHERE id = 3000;
UPDATE t_menus set position = 4 WHERE id = 2000;
UPDATE t_menus set position = 5 WHERE id = 1000;

INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
VALUES (
    5001,
    now(),
    true,
    'Industrial Purchase Invoices',
    'pi pi-receipt', 'Invoices',
    '/p/sales/inv',
    5000,
    FALSE,
    1,
    null
);
/*Acciones modulo Invoices*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001001, 'Create Invoices', 'Allows you to create a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001002, 'Update Invoices', 'Allows you to update a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001003, 'View History Invoices', 'Allows you to view history a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001004, 'Clone Invoices', 'Clone a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001005, 'Reject Invoices', 'Reject a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001006, 'Edit Payment Terms Invoices', 'Allows you to edit payment terms and not use those of the Client', 5001, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/
