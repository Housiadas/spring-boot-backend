CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    first_name varchar(255) NOT NULL,
    last_name  varchar(255) NOT NULL,
    email      varchar(100) NOT NULL UNIQUE,
    password   varchar(255) NOT NULL,
    created_at timestamp(6) NOT NULL,
    updated_at timestamp(6)
);

CREATE TABLE company
(
    id                    uuid PRIMARY KEY,
    slug                  varchar(255) NOT NULL UNIQUE,
    name                  varchar(255) NOT NULL UNIQUE,
    official_name         varchar(255) UNIQUE,
    state_tax_id          varchar(255),
    federal_tax_id        varchar(255) UNIQUE, -- CNPJ
    phone                 varchar(255),
    email                 varchar(255),

    address_street        varchar(255),
    address_street_number varchar(255),
    address_complement    varchar(255),
    address_city_district varchar(255),
    address_post_code     varchar(255),
    address_city          varchar(255),
    address_state_code    varchar(255),
    address_country       varchar(255),
    address_latitude      numeric,
    address_longitude     numeric,

    created_by            varchar(255),
    updated_by            varchar(255),

    created_at            timestamp    NOT NULL DEFAULT current_timestamp,
    updated_at            timestamp    NOT NULL DEFAULT current_timestamp
);
