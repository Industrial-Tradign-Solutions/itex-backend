DELETE FROM flyway_schema_history WHERE version = '2.0.3';
-- Reversa la precision de net_weight_lbs de 5 a 3 decimales.
ALTER TABLE t_ip_products ALTER COLUMN net_weight_lbs TYPE numeric(15,3);
