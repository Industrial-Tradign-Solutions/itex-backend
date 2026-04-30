INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (0, now(), false, 'Not Module', '', 'Not Module', '', null, TRUE, 0);

create table itex_history(
    id uuid not null primary key,
    module_id bigint not null references t_menus,
    action varchar(100) not null,
    user_executed_action_id uuid not null references t_users,
    modified_record_id uuid,
    old_data json,
    new_data json,
    is_basic boolean default true,
    created_at timestamp not null
);