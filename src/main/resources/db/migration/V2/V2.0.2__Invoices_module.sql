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
VALUES (5001005, 'Cancel Invoices', 'Allows you to cancel an Invoice', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001006, 'Edit Payment Terms Invoices', 'Allows you to edit payment terms and not use those of the Client', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001007, 'View Invoices', 'Allows you to view/list Invoices', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001008, 'Issue Invoices', 'Allows you to issue a Draft Invoice, assigning its final number', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001009, 'Register Payment Invoices', 'Allows you to register a payment on an Invoice', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001010, 'Delete Invoices', 'Allows you to delete a Draft Invoice', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001011, 'Revert Invoice to Draft', 'Allows you to revert an Issued Invoice back to Draft status, keeping its assigned number reserved', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001012, 'Void Payment Invoices', 'Allows you to void a registered payment on an Invoice', 5001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (5001013, 'View All Invoices', 'Allows you to view and edit all Invoices, regardless of assigned sales rep', 5001, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/

CREATE TABLE t_invoices (
    id                      UUID            NOT NULL    PRIMARY KEY,
    draft_number            BIGINT          NOT NULL    CONSTRAINT ip_invoice_unique_draft_number UNIQUE,
    number                  BIGINT                      CONSTRAINT ip_invoice_unique_number UNIQUE,
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

    total_amount            NUMERIC(15, 5)  NOT NULL    DEFAULT 0,
    paid_amount             NUMERIC(15, 5)  NOT NULL    DEFAULT 0,

    due_at                  TIMESTAMP,
    is_overdue              BOOLEAN         NOT NULL    DEFAULT false,
    overdue_notified_at     TIMESTAMP,

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

CREATE TABLE t_invoice_charges (
    id                      UUID            NOT NULL    PRIMARY KEY,
    invoice_id              UUID            NOT NULL    REFERENCES t_invoices,
    description             VARCHAR(100)    NOT NULL,
    type                    VARCHAR(100)    NOT NULL,
    value                   NUMERIC(15, 5)  NOT NULL,
    created_at              TIMESTAMP       NOT NULL
);

CREATE TABLE t_invoice_taxes (
    id                  UUID            NOT NULL    PRIMARY KEY,
    invoice_id          UUID            NOT NULL    REFERENCES t_invoices,
    type                VARCHAR(30)     NOT NULL,   -- ver enum InvoiceTaxType
    description         VARCHAR(100)    NOT NULL,   -- ej. "Colombia VAT", "US Sales Tax NY"
    rate                NUMERIC(5, 4)   NOT NULL,   -- ej. 0.1900 para 19%
    taxable_base        NUMERIC(15, 5)  NOT NULL,   -- monto sobre el que se calculó (subtotal, u otro)
    value               NUMERIC(15, 5)  NOT NULL,   -- rate * taxable_base, persistido
    created_at          TIMESTAMP       NOT NULL
);

CREATE TABLE t_invoice_payments (
    id                      UUID            NOT NULL    PRIMARY KEY,
    invoice_id              UUID            NOT NULL    REFERENCES t_invoices(id),
    amount                  NUMERIC(15, 5)  NOT NULL,
    payment_date            DATE            NOT NULL,
    payment_method          varchar(40)     not null,
    receipt_path            VARCHAR(1000)   NOT NULL,
    receipt_original_name   VARCHAR(255)    NOT NULL,
    notes                   TEXT,
    is_voided               BOOLEAN         NOT NULL    DEFAULT false,
    voided_reason           TEXT,
    voided_at               TIMESTAMP,
    voided_by_user_id       UUID                        REFERENCES t_users(id),
    registered_by_user_id   UUID            NOT NULL    REFERENCES t_users(id),
    created_at              TIMESTAMP       NOT NULL
);

CREATE TABLE t_invoice_ip_products(
    id                      UUID            NOT NULL    PRIMARY KEY,
    invoice_id              UUID            NOT NULL    REFERENCES t_invoices,
    product_id              uuid            NOT NULL    REFERENCES t_ip_products,
    number                  int             not null    DEFAULT 1,
    quantity                NUMERIC(15, 5)  NOT NULL    DEFAULT 0,
    unit_type               VARCHAR(50)     NOT NULL,
    lead_time               INT             NOT NULL    DEFAULT 0,
    lead_time_type          VARCHAR(10)     NOT NULL    DEFAULT 'WEEKS',
    unit_price              NUMERIC(15,5)   NOT NULL    DEFAULT 0,
    profit_margin           NUMERIC(3,2)    NOT NULL    DEFAULT 0,
    condition               VARCHAR(20)     NOT NULL,
    created_at              TIMESTAMP       NOT NULL
);

alter table t_invoice_ip_products
    add constraint inv_product_unique
        unique (invoice_id, product_id);

create table t_invoice_ip_po (
    invoice_id              UUID            NOT NULL    REFERENCES t_invoices,
    ip_po_id                UUID            NOT NULL    REFERENCES t_ip_purchase_orders,
    primary key (invoice_id, ip_po_id)
);

create table t_invoice_cloned (
    main_invoice_id              uuid            not null    references t_invoices,
    clone_invoice_id             uuid            not null    references t_invoices,
    primary key (main_invoice_id, clone_invoice_id)
);

alter table t_invoice_cloned
    add constraint invoice_clone_unique_id
        unique (clone_invoice_id);

create table t_invoice_history (
    id uuid not null primary key,
    invoice_id uuid not null references t_invoices,
    user_id uuid not null references t_users,
    action varchar(50) not null,
    created_at timestamp not null,
    data json
);

/*----------------------------------------------------------------------------------------------------------------------*/
/* Consecutivos de factura (mecanismo dedicado e independiente de itex_consecutive).                                     */
/* DRAFT: secuencia global que reutiliza el menor hueco liberado. FINAL: secuencia global max+1 sin reuso.              */
/* Los numeros usan BIGINT para un margen amplio.                                                                        */
/*----------------------------------------------------------------------------------------------------------------------*/

-- Contador high-water por tipo de consecutivo de factura
CREATE TABLE t_sales_consecutive_sequence (
    type                    VARCHAR(15)     NOT NULL    PRIMARY KEY,    -- 'DRAFT' | 'FINAL'
    current_value           BIGINT          NOT NULL                    -- ultimo numero asignado (high-water)
);

-- Free list: numeros de DRAFT liberados (al borrar una factura en draft), disponibles para reuso.
-- FINAL nunca se libera porque las facturas emitidas no se borran.
CREATE TABLE t_sales_consecutive_free (
    type                    VARCHAR(15)     NOT NULL,
    number                  BIGINT          NOT NULL,
    created_at              TIMESTAMP       NOT NULL,
    PRIMARY KEY (type, number)
);

-- Semillas: DRAFT arranca en 1 (0 + 1). FINAL arranca en el valor que se indique al iniciar el
-- modulo de facturacion: current_value = (valor_inicial - 1). Ej. 999 -> primera factura 1000.
-- Este valor se ajusta con un UPDATE simple cuando se defina, sin recompilar.
INSERT INTO t_sales_consecutive_sequence (type, current_value) VALUES ('DRAFT_INV', 0);
INSERT INTO t_sales_consecutive_sequence (type, current_value) VALUES ('INV', 999);
