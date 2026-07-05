
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position, docs_url)
VALUES (
        4003,
        now(),
        true,
        'Industrial Purchase Quotation module',
        'pi pi-briefcase', 'Quotations',
        '/p/ip/q',
        4000,
        FALSE,
        2,
        'https://jdbayer.notion.site/IP-Quotations-303db6690de780ea99d2ec294c114e5a'
       );/*Acciones modulo Quote Request*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4003001, 'Create Quotation', 'Allows you to create a Quotation', 4003, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4003002, 'Update Quotation', 'Allows you to update a Quotation', 4003, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4003003, 'View History Quotation', 'Allows you to view history a Quotation', 4003, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4003004, 'Clone Quotation', 'Clone a Quotation', 4003, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4003005, 'Reject Quotation', 'Reject a Quotation', 4003, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
VALUES (4003006, 'Edit Payment Terms Quotation', 'Allows you to edit payment terms and not use those of the Client', 4003, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/
create table t_ip_quotations (
    id uuid not null primary key,
    number varchar(20) not null constraint ip_q_unique_number unique,
    created_at timestamp not null,
    status varchar(20) not null,
    currency varchar(20) not null,
    client_id uuid references t_clients,
    client_contact_id uuid references t_clients_contacts,
    client_q_number varchar(50),
    sales_rep_id uuid not null references t_users,
    remarks text,
    internal_remarks text,
    lead_time integer not null default 0,
    lead_time_type varchar(20) not null default 'WEEKS',
    validity integer not null default 0,
    validity_type varchar(20) not null default 'DAYS',
    incoterms varchar(3),
    payment_terms varchar(40) not null,
    application_at timestamp not null,
    path_pdf varchar(1000),
    open_at timestamp ,
    open_by_user_id uuid references t_users,
    sent_at timestamp ,
    answered_at timestamp ,
    complete_at timestamp ,
    reject_at timestamp
);

create table t_ip_quotations_quote_request (
    id uuid not null primary key,
    quotation_id uuid references t_ip_quotations,
    quote_request_id uuid references t_ip_quote_requests,
    constraint ip_q_qr_unique unique (quotation_id, quote_request_id)
);

create table t_ip_quotation_products (
    id uuid not null primary key,
    quotations_quote_request_id uuid not null references t_ip_quotations_quote_request,
    quote_request_product_id uuid references t_ip_quote_request_products,
    number int not null,
    profit_margin NUMERIC(3,2) not null default 0.00 CHECK (profit_margin >= 0.00 AND profit_margin <= 1.00),
    condition varchar(20) not null,
    created_at timestamp not null
);

alter table t_ip_quotation_products
    add constraint t_ip_quotation_products_unique_product
        unique (quote_request_product_id, quotations_quote_request_id);

create table t_ip_quotation_other_charges (
    id uuid not null primary key,
    ip_q_id uuid not null references t_ip_quotations,
    description varchar(150) not null,
    value numeric(15,2) not null default 0,
    created_at timestamp not null
);

alter table t_ip_quotation_other_charges
    add constraint t_ip_quotation_other_charges_unique_description
        unique (ip_q_id, description);

create table t_ip_quotations_cloned (
    main_q_id uuid not null references t_ip_quotations,
    clone_q_id uuid not null references t_ip_quotations,
    primary key (main_q_id, clone_q_id)
);

create unique index t_ip_quotations_cloned_clone_q_id_unique
    on t_ip_quotations_cloned (clone_q_id);

create table t_ip_quotation_history (
    id uuid not null primary key,
    ip_q_id uuid not null references t_ip_quotations,
    user_id uuid not null references t_users,
    action varchar(50) not null,
    created_at timestamp not null,
    data json
);

create table t_ip_quotation_other_charges_quote_request (
    id uuid not null primary key,
    quotations_quote_request_id uuid not null references t_ip_quotations_quote_request,
    qr_other_charge_id uuid not null references t_ip_quote_requests_other_charges,
    created_at timestamp not null
);

alter table t_ip_quotation_other_charges_quote_request
    add constraint t_ip_quotation_other_charges_qqr_unique
        unique (qr_other_charge_id, quotations_quote_request_id);
