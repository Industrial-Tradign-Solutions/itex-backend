create table itex_consecutive (
    prefix VARCHAR(8),
    department VARCHAR(3),
    counter INT NOT NULL,
    primary key (prefix, department)
);
