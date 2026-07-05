
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
VALUES (
        4005,
        now(),
        true,
        'Industrial Purchase Invoices',
        'pi pi-receipt', 'Invoices',
        '/p/ip/inv',
        4000,
        FALSE,
        4,
        null
       );/*Acciones modulo Invoices*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4005001, 'Create Invoices', 'Allows you to create a Invoices', 4005, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4005002, 'Update Invoices', 'Allows you to update a Invoices', 4005, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4005003, 'View History Invoices', 'Allows you to view history a Invoices', 4005, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4005004, 'Clone Invoices', 'Clone a Invoices', 4005, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4005005, 'Reject Invoices', 'Reject a Invoices', 4005, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4005006, 'Edit Payment Terms Invoices', 'Allows you to edit payment terms and not use those of the Client', 4005, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/
