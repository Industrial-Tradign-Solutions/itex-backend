alter table t_cities
    add is_active boolean default true;

alter table t_states
    add is_active boolean default true;

alter table t_countries
    add is_active boolean default true;
