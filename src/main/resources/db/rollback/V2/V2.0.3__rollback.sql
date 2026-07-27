DELETE FROM flyway_schema_history WHERE version = '2.0.3';
DELETE FROM t_actions WHERE menu_item_id = 5002;
DELETE FROM t_menus WHERE id = 5002;

DELETE FROM t_sales_consecutive_sequence WHERE type = 'DRAFT_MEMO';
DELETE FROM t_sales_consecutive_sequence WHERE type = 'MEMO';
