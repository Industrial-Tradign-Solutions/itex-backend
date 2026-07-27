DELETE FROM flyway_schema_history WHERE version = '2.0.2';
DELETE FROM t_actions WHERE menu_item_id = 5001;
DELETE FROM t_menus WHERE id = 5001;
DELETE FROM t_menus WHERE id = 5000;

DROP TABLE IF EXISTS t_sales_consecutive_sequence;
DROP TABLE IF EXISTS t_sales_consecutive_free;
DROP TABLE IF EXISTS t_invoice_cloned;
DROP TABLE IF EXISTS t_invoice_history;
DROP TABLE IF EXISTS t_invoice_ip_products;
DROP TABLE IF EXISTS t_invoice_payments;
DROP TABLE IF EXISTS t_invoice_taxes;
DROP TABLE IF EXISTS t_invoice_charges;
DROP TABLE IF EXISTS t_invoice_ip_po;
DROP TABLE IF EXISTS t_invoices;
