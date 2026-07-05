DELETE FROM flyway_schema_history WHERE version = '2.0.1';
DELETE FROM t_actions WHERE menu_item_id = 4004;
DELETE FROM t_menus WHERE id = 4004;
DELETE FROM itex_consecutive WHERE module = 'PO' AND department = 'IP';
