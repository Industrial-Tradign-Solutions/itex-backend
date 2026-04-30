create table t_roles (
    id uuid not null primary key,
    name varchar(150) not null constraint role_unique_name unique,
    description text,
    is_active   boolean default false not null,
    is_editable boolean default false not null,
    created_at  timestamp not null
);
create table t_menus (
    id bigint not null primary key,
    name varchar(150) not null constraint menu_unique_name unique,
    url varchar(150) not null constraint menu_unique_url unique,
    description TEXT not null,
    icon varchar(150) not null,
    position integer not null,
    is_main_option boolean not null,
    main_menu_id bigint references t_menus,
    is_active boolean default false not null,
    created_at timestamp not null
);
create table t_actions (
    id bigint not null primary key,
    name varchar(100) not null constraint action_unique_name unique,
    description TEXT not null,
    menu_item_id bigint references t_menus,
    is_active boolean default false not null,
    created_at timestamp not null
);
create table t_roles_menus (
    menu_id bigint not null references t_menus,
    role_id uuid not null references t_roles,
    primary key (menu_id, role_id)
);
create table t_roles_actions (
    action_id bigint not null references t_actions,
    role_id uuid not null references t_roles,
    primary key (action_id, role_id)
);
create table t_departments (
    id uuid not null primary key,
    name varchar(150) not null constraint department_unique_name unique,
    description TEXT,
    is_active boolean default false not null,
    is_client_info boolean default false not null,
    is_supplier_info boolean default false not null,
    created_at  timestamp not null
);
create table t_users (
    id uuid not null primary key,
    username varchar(50) not null constraint user_unique_username unique,
    name varchar(60) not null,
    last_name varchar(60) not null,
    email varchar(100) not null,
    title varchar(50) not null,
    extension varchar(4) not null,
    password varchar(500) not null,
    email_password varchar(500),
    department_id uuid not null references t_departments,
    role_id uuid not null references t_roles,
    is_active boolean default false not null,
    is_pass_changed boolean default false not null,
    pass_changed_at timestamp,
    created_at  timestamp not null
);
create table t_industries (
    id uuid not null primary key,
    name varchar(150) not null constraint industry_unique_name unique,
    description TEXT,
    is_active boolean default false not null,
    created_at  timestamp  not null
);
create table t_countries (
    id uuid not null primary key,
    name_short varchar(3) not null constraint countries_unique_name_short unique,
    name varchar(70) not null constraint countries_unique_name unique,
    latitude varchar(15) not null,
    longitude varchar(15) not null,
    created_at timestamp  not null,
    constraint countries_unique_latitude_longitude unique (latitude, longitude)
);
create table t_states (
    id uuid not null primary key,
    name_short varchar(3) not null,
    name varchar(70) not null,
    latitude varchar(15) not null,
    longitude varchar(15) not null,
    country_id uuid not null references t_countries,
    is_show_short_name boolean default false not null,
    created_at timestamp  not null,
    constraint states_unique_latitude_longitude unique (latitude, longitude),
    constraint states_unique_name unique(name, country_id),
    constraint states_unique_name_short unique(name_short, country_id)
);
create table t_cities (
    id uuid not null primary key,
    name varchar(70) not null ,
    latitude varchar(15) not null,
    longitude varchar(15) not null,
    state_id uuid not null references t_states,
    created_at timestamp  not null,
    constraint cities_unique_latitude_longitude unique (latitude, longitude),
    constraint cities_unique_name unique(name, state_id)
);
