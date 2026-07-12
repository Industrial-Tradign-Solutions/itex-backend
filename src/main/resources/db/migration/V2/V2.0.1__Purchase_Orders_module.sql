
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
VALUES (
        4004,
        now(),
        true,
        'Industrial Purchase Purchase Orders module',
        'pi pi-shop', 'Purchase Orders',
        '/p/ip/po',
        4000,
        FALSE,
        3,
        null
       );/*Acciones modulo Purchase Order*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004001, 'Create Purchase Order', 'Allows you to create a Purchase Order', 4004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004002, 'Update Purchase Order', 'Allows you to update a Purchase Order', 4004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004003, 'View History Purchase Order', 'Allows you to view history a Purchase Order', 4004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004004, 'Reject Purchase Order', 'Reject a Purchase Order', 4004, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4004005, 'Edit Payment Terms Purchase Order', 'Allows you to edit payment terms and not use those of the Client', 4004, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/
CREATE TABLE t_ip_purchase_orders (
    id                      UUID            NOT NULL    PRIMARY KEY,
    number                  VARCHAR(20)     NOT NULL    CONSTRAINT ip_po_unique_number UNIQUE,
    status                  VARCHAR(20)     NOT NULL,
    currency                VARCHAR(20)     NOT NULL,
    quotation_id            UUID            NOT NULL    REFERENCES t_ip_quotations(id),
    supplier_id             UUID            NOT NULL    REFERENCES t_suppliers(id),
    supplier_contact_id     UUID                        REFERENCES t_suppliers_contacts(id),
    supplier_po_number      VARCHAR(50),
    payment_terms           VARCHAR(40)     NOT NULL,
    lead_time               INTEGER         NOT NULL    DEFAULT 0,
    lead_time_type          VARCHAR(20)     NOT NULL    DEFAULT 'WEEKS',
    shipping_method         VARCHAR(50)     NOT NULL,
    remarks                 TEXT,
    internal_remarks        TEXT,
    ship_to_name            VARCHAR(300)    NOT NULL,
    ship_to_address         VARCHAR(500)    NOT NULL,
    ship_to_city            UUID            NOT NULL    REFERENCES t_cities(id),
    ship_to_phone           VARCHAR(20)     NOT NULL,
    ship_to_contact_name    VARCHAR(50)     NOT NULL,
    ship_to_email           VARCHAR(100)    NOT NULL,
    sales_tax               NUMERIC(15, 2)  NOT NULL    DEFAULT 0,
    created_at              TIMESTAMP       NOT NULL,
    sent_at                 TIMESTAMP,
    answered_at             TIMESTAMP,
    complete_at             TIMESTAMP,
    reject_at               TIMESTAMP,
    path_pdf                VARCHAR(1000),
    open_at                 TIMESTAMP,
    open_by_user_id         UUID                        REFERENCES t_users(id)
);

create table t_ip_purchase_order_products (
    id                      UUID            NOT NULL    PRIMARY KEY,
    ip_po_id                uuid            not null    references t_ip_purchase_orders,
    quotation_product_id    UUID                        REFERENCES t_ip_quotation_products(id),
    number                  INT             NOT NULL,
    created_at              TIMESTAMP       NOT NULL
);

create table t_ip_purchase_orders_other_charges (
    id                      uuid            not null    primary key,
    ip_po_id                uuid            not null    references t_ip_purchase_orders,
    description             varchar(150)    not null,
    value                   numeric(15,2)   not null    default 0,
    created_at              timestamp       not null
);

create table t_ip_purchase_order_other_charges_quotation (
    id                      uuid            not null    primary key,
    ip_po_id                uuid            not null    references t_ip_purchase_orders(id),
    ip_q_other_charge_id    uuid            not null    references t_ip_quotation_other_charges,
    created_at              timestamp       not null
);

alter table t_ip_purchase_order_other_charges_quotation
    add constraint t_ip_po_other_charges_q_unique
        unique (ip_po_id, ip_q_other_charge_id);

create table t_ip_purchase_order_other_charges_quotation_qr (
    id                      uuid            not null    primary key,
    ip_po_id                uuid            not null    references t_ip_purchase_orders(id),
    ip_q_qr_other_charge_id uuid            not null    references t_ip_quotation_other_charges_quote_request,
    created_at              timestamp       not null
);

alter table t_ip_purchase_order_other_charges_quotation_qr
    add constraint t_ip_po_other_charges_q_qr_unique
        unique (ip_po_id, ip_q_qr_other_charge_id);

create table t_ip_purchase_orders_cloned (
    main_po_id              uuid            not null    references t_ip_purchase_orders,
    clone_po_id             uuid            not null    references t_ip_purchase_orders,
    primary key (main_po_id, clone_po_id)
);

create table t_ip_purchase_orders_history (
    id                      uuid            not null    primary key,
    ip_po_id                uuid            not null    references t_ip_purchase_orders(id),
    user_id                 uuid            not null    references t_users,
    action                  varchar(50)     not null,
    created_at              timestamp       not null,
    data                    json
);




