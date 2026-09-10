-- V2.0.5: Q flete (margen + Miami ITS), other charges 15,5 y its_lead_time en productos.
-- ATENCION: si V2.0.5 ya se aplico en alguna BD, ejecutar 'flyway repair' (o actualizar
-- el checksum en flyway_schema_history) antes de arrancar, porque este archivo se modifico.

ALTER TABLE t_ip_quotations
    ADD COLUMN profit_margin_freight_charges numeric(15,5) NOT NULL DEFAULT 0,
    ADD COLUMN freight_charge_miami_its numeric(15,5) NOT NULL DEFAULT 0;

ALTER TABLE t_ip_quotation_other_charges
    ALTER COLUMN value TYPE numeric(15,5);

ALTER TABLE t_ip_quotation_products
    ADD COLUMN its_lead_time integer NOT NULL DEFAULT 0;
