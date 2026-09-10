DELETE FROM flyway_schema_history WHERE version = '2.0.5';

ALTER TABLE t_ip_quotations DROP COLUMN IF EXISTS profit_margin_freight_charges;
ALTER TABLE t_ip_quotations DROP COLUMN IF EXISTS freight_charge_miami_its;

ALTER TABLE t_ip_quotation_other_charges
    ALTER COLUMN value TYPE numeric(15,2);

ALTER TABLE t_ip_quotation_products DROP COLUMN IF EXISTS its_lead_time;
