ALTER TABLE t_ip_quotations
    ADD COLUMN profit_margin_freight_charges numeric(15,5) NOT NULL DEFAULT 0,
    ADD COLUMN freight_charge_miami_its numeric(15,5) NOT NULL DEFAULT 0;

ALTER TABLE t_ip_quotation_other_charges
    ALTER COLUMN value TYPE numeric(15,5);
