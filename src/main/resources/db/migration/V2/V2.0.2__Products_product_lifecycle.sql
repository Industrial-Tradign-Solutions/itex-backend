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

-- Incrementa la precision de net_weight_lbs de 3 a 5 decimales para permitir
-- medidas en gramos (1 g = 0.00220462 lb). Los pesos ya no se redondean en los
-- calculos de QR/Q/PO, asi que la columna debe conservar el detalle.
ALTER TABLE t_ip_products ALTER COLUMN net_weight_lbs TYPE numeric(15,5);

/*--------------------------------------------------------------------------------------------------------------------------
 * Quotations: application_at changes from TIMESTAMP to DATE (solo fecha sin hora).
 * It becomes NULLABLE because a cloned quotation may be created without an application date;
 * the date is required (validated) before moving the quotation to SENT or ANSWERED.
 * The column stores the application/submission date of the quotation.
 *--------------------------------------------------------------------------------------------------------------------------*/
ALTER TABLE t_ip_quotations ALTER COLUMN application_at TYPE DATE USING application_at::date;
ALTER TABLE t_ip_quotations ALTER COLUMN application_at DROP NOT NULL;

/*--------------------------------------------------------------------------------------------------------------------------
 * Rollback: revert application_at from DATE back to TIMESTAMP (nullable -> not null).
 *--------------------------------------------------------------------------------------------------------------------------*/
-- ALTER TABLE t_ip_quotations ALTER COLUMN application_at TYPE TIMESTAMP USING application_at::timestamp;
