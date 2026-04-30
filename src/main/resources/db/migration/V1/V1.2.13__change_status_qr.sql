alter table t_ip_quote_requests
    add sent_at timestamp;

alter table t_ip_quote_requests
    add answered_at timestamp;

alter table t_ip_quote_requests
    add complete_at timestamp;

alter table t_ip_quote_requests
    add reject_at timestamp;
