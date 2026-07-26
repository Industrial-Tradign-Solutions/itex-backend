INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
VALUES (
    5000,
    now(),
    true,
    'Systems Sales Category Sales',
    '',
    'Sales',
    '/p/sales',
    null,
    TRUE,
    2,
    null
);
UPDATE t_menus set position = 1 WHERE id = 4000;
UPDATE t_menus set position = 2 WHERE id = 5000;
UPDATE t_menus set position = 3 WHERE id = 3000;
UPDATE t_menus set position = 4 WHERE id = 2000;
UPDATE t_menus set position = 5 WHERE id = 1000;

INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
VALUES (
    5001,
    now(),
    true,
    'Industrial Purchase Invoices',
    'pi pi-receipt', 'Invoices',
    '/p/sales/inv',
    5000,
    FALSE,
    1,
    null
);
/*Acciones modulo Invoices*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001001, 'Create Invoices', 'Allows you to create a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001002, 'Update Invoices', 'Allows you to update a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001003, 'View History Invoices', 'Allows you to view history a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001004, 'Clone Invoices', 'Clone a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001005, 'Reject Invoices', 'Reject a Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001006, 'Edit Payment Terms Invoices', 'Allows you to edit payment terms and not use those of the Client', 5001, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/

CREATE TABLE t_invoices (
    id                      UUID            NOT NULL    PRIMARY KEY,
    number                  VARCHAR(20)     NOT NULL    CONSTRAINT ip_invoice_unique_number UNIQUE,
    department              VARCHAR(3)      NOT NULL    DEFAULT 'IP',
    status                  VARCHAR(20)     NOT NULL    DEFAULT 'DRAFT',
    currency                VARCHAR(20)     NOT NULL,
    client_id               UUID            NOT NULL    REFERENCES t_clients(id),
    client_contact_id       UUID                        REFERENCES t_clients_contacts(id),
    ship_to_name            VARCHAR(300)    NOT NULL,
    ship_to_address         VARCHAR(500)    NOT NULL,
    ship_to_city            UUID            NOT NULL    REFERENCES t_cities(id),
    ship_to_phone           VARCHAR(20)     NOT NULL,
    ship_to_contact_name    VARCHAR(50)     NOT NULL,
    ship_to_email           VARCHAR(100)    NOT NULL,
    order_number            VARCHAR(100),
    via                     VARCHAR(10),
    incoterms               VARCHAR(20)     NOT NULL,
    payment_terms           VARCHAR(40),
    awb_bl                  VARCHAR(100),
    sales_rep_id            uuid            not null    references t_users,
    remarks                 TEXT,
    internal_remarks        TEXT,
    packing_list            VARCHAR(100),

    --freight_to_miami        NUMERIC(15, 2)  NOT NULL    DEFAULT 0,
    --international_freight   NUMERIC(15, 2)  NOT NULL    DEFAULT 0,
    --wire_transfer_fee       NUMERIC(15, 2)  NOT NULL    DEFAULT 0,
    --insurance               NUMERIC(15, 2)  NOT NULL    DEFAULT 0,
    --tax_us                  NUMERIC(15, 2)  NOT NULL    DEFAULT 0,

    total_amount            NUMERIC(15, 2)  NOT NULL    DEFAULT 0,
    paid_amount             NUMERIC(15, 2)  NOT NULL    DEFAULT 0,

    issued_at               TIMESTAMP,
    partial_paid_at         TIMESTAMP,
    paid_at                 TIMESTAMP,
    cancelled_at            TIMESTAMP,
    cancel_reason           TEXT,

    created_at              TIMESTAMP       NOT NULL,
    path_pdf                VARCHAR(1000),
    open_at                 TIMESTAMP,
    open_by_user_id         UUID                        REFERENCES t_users(id)
);

CREATE TABLE t_invoices_charges (
    id                      UUID            NOT NULL    PRIMARY KEY,
    invoice_id              UUID            NOT NULL    REFERENCES t_invoices,
    description             VARCHAR(100)    NOT NULL,
    type                    VARCHAR(100)    NOT NULL,
    value                   NUMERIC(15, 2)  NOT NULL,
    created_at              TIMESTAMP       NOT NULL
);

CREATE TABLE t_invoices_ip_products(
    id                      UUID            NOT NULL    PRIMARY KEY,
    invoice_id              UUID            NOT NULL    REFERENCES t_invoices,
    product_id              uuid            NOT NULL    REFERENCES t_ip_products

);
