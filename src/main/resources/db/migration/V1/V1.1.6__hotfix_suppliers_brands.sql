alter table t_suppliers
    drop column delivery_by;

alter table t_suppliers_brands
    rename to t_brands_suppliers;
