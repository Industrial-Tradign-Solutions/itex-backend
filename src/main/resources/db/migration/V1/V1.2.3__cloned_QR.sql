create table t_ip_quote_requests_cloned (
    main_qr_id uuid not null references t_ip_quote_requests,
    clone_qr_id uuid not null references t_ip_quote_requests,
    primary key (main_qr_id, clone_qr_id)
);
create unique index t_ip_quote_requests_cloned_clone_qr_id_unique
    on t_ip_quote_requests_cloned (clone_qr_id);
