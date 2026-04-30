alter table t_ip_quote_requests
    drop column client_call;

alter table t_ip_quote_requests
    drop column supplier_call;

alter table t_ip_quote_requests
    add internal_remarks text;

delete from t_actions where id = 4002007;
delete from t_actions where id = 4002004;
