-- Incrementa la precision de net_weight_lbs de 3 a 5 decimales para permitir
-- medidas en gramos (1 g = 0.00220462 lb). Los pesos ya no se redondean en los
-- calculos de QR/Q/PO, asi que la columna debe conservar el detalle.
ALTER TABLE t_ip_products ALTER COLUMN net_weight_lbs TYPE numeric(15,5);