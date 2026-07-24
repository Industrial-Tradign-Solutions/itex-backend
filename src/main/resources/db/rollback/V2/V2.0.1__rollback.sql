DELETE FROM flyway_schema_history WHERE version = '2.0.1';
DELETE FROM t_actions WHERE menu_item_id = 4004;
DELETE FROM t_menus WHERE id = 4004;
DELETE FROM itex_consecutive WHERE module = 'PO' AND department = 'IP';

DROP TABLE IF EXISTS t_ip_purchase_orders_history;
DROP TABLE IF EXISTS t_ip_purchase_orders_cloned;
DROP TABLE IF EXISTS t_ip_purchase_order_other_charges_quotation_qr;
DROP TABLE IF EXISTS t_ip_purchase_order_other_charges_quotation;
DROP TABLE IF EXISTS t_ip_purchase_orders_other_charges;
DROP TABLE IF EXISTS t_ip_purchase_order_products;
DROP TABLE IF EXISTS t_ip_purchase_orders;
