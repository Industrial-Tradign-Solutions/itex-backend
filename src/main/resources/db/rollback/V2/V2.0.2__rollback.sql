DELETE FROM flyway_schema_history WHERE version = '2.0.2';
DELETE FROM t_actions WHERE menu_item_id = 5001;
DELETE FROM t_menus WHERE id = 5001;
DELETE FROM t_menus WHERE id = 5000;
DELETE FROM itex_consecutive WHERE module = 'INV' AND department = 'IP';
