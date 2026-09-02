DELETE FROM flyway_schema_history WHERE version = '2.0.2';

-- Products lifecycle rollback
ALTER TABLE t_ip_products DROP CONSTRAINT IF EXISTS ip_product_mfr_reference_unique;
ALTER TABLE t_ip_products ALTER COLUMN mfr_reference DROP NOT NULL;

-- Quotations profit_margin rollback (back to fraction 0.00-1.00)
ALTER TABLE t_ip_quotation_products DROP CONSTRAINT IF EXISTS t_ip_quotation_products_profit_margin_check;
ALTER TABLE t_ip_quotation_products ALTER COLUMN profit_margin TYPE NUMERIC(3,2);
ALTER TABLE t_ip_quotation_products ALTER COLUMN profit_margin SET DEFAULT 0.00;
ALTER TABLE t_ip_quotation_products ADD CONSTRAINT t_ip_quotation_products_profit_margin_check
    CHECK (profit_margin >= 0.00 AND profit_margin <= 1.00);