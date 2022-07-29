CREATE TYPE account_kind AS ENUM ('user', 'sale');

CREATE TABLE accounts
(
    address             BYTEA PRIMARY KEY,
    kind                account_kind NOT NULL DEFAULT ('user'),

    -- sales
    marketplace         BYTEA        NOT NULL,
    item                BYTEA        NOT NULL,
    owner               BYTEA        NOT NULL,
    full_price          NUMERIC(80)  NOT NULL,
    marketplace_fee     NUMERIC(80)  NOT NULL,
    royalty             NUMERIC(80)  NOT NULL,
    royalty_destination BYTEA        NOT NULL,

    --
    discovered          TIMESTAMPTZ  NOT NULL,
    updated             TIMESTAMPTZ  NOT NULL
);

CREATE TABLE attributes
(
    id    BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    item  BYTEA NOT NULL,
    trait TEXT  NOT NULL,
    value TEXT  NOT NULL,
    UNIQUE (item, trait) -- Unique pairs
);

CREATE TABLE collections
(
    address         BYTEA PRIMARY KEY,

    -- Basic
    next_item_index BIGINT      NOT NULL,
    owner           BYTEA       NOT NULL,

    -- Metadata
    name            TEXT,
    description     TEXT,
    image           TEXT,
    cover_image     TEXT,

    --
    approved        BOOLEAN     NOT NULL DEFAULT (FALSE),
    discovered      TIMESTAMPTZ NOT NULL,
    updated         TIMESTAMPTZ NOT NULL
);

CREATE TABLE items
(
    address     BYTEA PRIMARY KEY,

    -- Basic
    initialized BOOLEAN     NOT NULL,
    index       BIGINT      NOT NULL,
    collection  BYTEA       NOT NULL,
    owner       BYTEA       NOT NULL,

    -- Metadata
    name        TEXT,
    description TEXT,
    image       TEXT,

    --
    approved    BOOLEAN     NOT NULL DEFAULT (FALSE),
    discovered  TIMESTAMPTZ NOT NULL,
    updated     TIMESTAMPTZ NOT NULL
);

CREATE TABLE royalties
(
    address     BYTEA PRIMARY KEY,

    -- Basic
    numerator   INTEGER     NOT NULL,
    denominator INTEGER     NOT NULL,
    destination BYTEA       NOT NULL,

    --
    discovered  TIMESTAMPTZ NOT NULL,
    updated     TIMESTAMPTZ NOT NULL
);


