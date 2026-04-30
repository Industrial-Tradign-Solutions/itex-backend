drop table itex_consecutive;

create table itex_consecutive (
    department VARCHAR(3),
    client_code VARCHAR(3) references t_clients(code),
    year VARCHAR(2),
    month VARCHAR(2),
    module VARCHAR(3),
    number INT NOT NULL,
    primary key (department, client_code, year, month, number, module)
);
