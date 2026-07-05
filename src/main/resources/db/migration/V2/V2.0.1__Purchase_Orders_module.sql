
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
VALUES (
        4004,
        now(),
        true,
        'Industrial Purchase Purchase Orders module',
        'pi pi-shop', 'Purchase Orders',
        '/p/ip/po',
        4000,
        FALSE,
        3,
        null
       );/*Acciones modulo Purchase Order*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004001, 'Create Purchase Order', 'Allows you to create a Purchase Order', 4004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004002, 'Update Purchase Order', 'Allows you to update a Purchase Order', 4004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004003, 'View History Purchase Order', 'Allows you to view history a Purchase Order', 4004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004004, 'Reject Purchase Order', 'Reject a Purchase Order', 4004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004005, 'Edit Payment Terms Purchase Order', 'Allows you to edit payment terms and not use those of the Client', 4004, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/
