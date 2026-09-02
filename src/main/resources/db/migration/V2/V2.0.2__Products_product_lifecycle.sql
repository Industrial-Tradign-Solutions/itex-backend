-- Product lifecycle: description and mfr_reference become NOT NULL,
-- mfr_reference must be unique.
alter table t_ip_products
    alter column description set not null;

alter table t_ip_products
    alter column mfr_reference set not null;

alter table t_ip_products
    add constraint ip_product_mfr_reference_unique unique (mfr_reference);

/*--------------------------------------------------------------------------------------------------------------------------
 * Quotations: profit_margin changes from fraction (0.10 = 10%) to direct percentage (10.00 = 10%), range 0.01-100.
 * Both Q and PO modules are not deployed to PDN yet, so no data migration is required.
 *--------------------------------------------------------------------------------------------------------------------------*/
ALTER TABLE t_ip_quotation_products DROP CONSTRAINT IF EXISTS t_ip_quotation_products_profit_margin_check;
ALTER TABLE t_ip_quotation_products ALTER COLUMN profit_margin TYPE NUMERIC(5,2);
ALTER TABLE t_ip_quotation_products ALTER COLUMN profit_margin SET DEFAULT 0.01;
ALTER TABLE t_ip_quotation_products ADD CONSTRAINT t_ip_quotation_products_profit_margin_check
    CHECK (profit_margin >= 0.01 AND profit_margin <= 100.00);