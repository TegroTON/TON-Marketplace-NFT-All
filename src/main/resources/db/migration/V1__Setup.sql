CREATE TABLE accounts
(
    address BYTEA PRIMARY KEY,
    updated TIMESTAMPTZ NOT NULL
);

CREATE TABLE collections
(
    address         BYTEA PRIMARY KEY,
    next_item_index BIGINT      NOT NULL,
    owner           BYTEA       NOT NULL,
    name            TEXT,
    description     TEXT,
    image           TEXT,
    cover_image     TEXT,
    updated         TIMESTAMPTZ NOT NULL
);

CREATE TABLE items
(
    address     BYTEA PRIMARY KEY,
    initialized BOOLEAN     NOT NULL,
    index       BIGINT      NOT NULL,
    collection  BYTEA       NOT NULL,
    owner       BYTEA       NOT NULL,
    name        TEXT,
    description TEXT,
    image       TEXT,
    attributes  JSONB,
    updated     TIMESTAMPTZ NOT NULL
);

CREATE TABLE royalties
(
    address     BYTEA PRIMARY KEY,
    numerator   INTEGER     NOT NULL,
    denominator INTEGER     NOT NULL,
    destination BYTEA       NOT NULL,
    updated     TIMESTAMPTZ NOT NULL
);

CREATE TABLE sales
(
    address             BYTEA PRIMARY KEY,
    marketplace         BYTEA       NOT NULL,
    item                BYTEA       NOT NULL,
    owner               BYTEA       NOT NULL,
    full_price          NUMERIC(80) NOT NULL,
    marketplace_fee     NUMERIC(80) NOT NULL,
    royalty             NUMERIC(80) NOT NULL,
    royalty_destination BYTEA       NOT NULL,
    updated             TIMESTAMPTZ NOT NULL
);
