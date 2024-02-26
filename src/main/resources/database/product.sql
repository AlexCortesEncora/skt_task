create table if not exists public.product
(
    id          integer default nextval('table_name_id_seq'::regclass) not null
    constraint table_name_pkey
    primary key,
    name        varchar(10)                                            not null,
    description varchar(100)                                           not null,
    price       real                                                   not null
    );

alter table public.product
    owner to postgres;

