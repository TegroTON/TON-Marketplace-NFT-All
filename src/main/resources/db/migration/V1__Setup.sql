CREATE TABLE accounts
(
    address    BYTEA PRIMARY KEY,
    discovered TIMESTAMP NOT NULL,
    updated    TIMESTAMP NOT NULL
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
    next_item_index BIGINT    NOT NULL,
    owner           BYTEA     NOT NULL,
    name            TEXT,
    description     TEXT,
    image           TEXT,
    cover_image     TEXT,
    approved        BOOLEAN   NOT NULL,
    discovered      TIMESTAMP NOT NULL,
    updated         TIMESTAMP NOT NULL
);

CREATE TABLE items
(
    address     BYTEA PRIMARY KEY,
    initialized BOOLEAN   NOT NULL,
    index       BIGINT    NOT NULL,
    collection  BYTEA     NOT NULL,
    owner       BYTEA     NOT NULL,
    name        TEXT,
    description TEXT,
    image       TEXT,
    approved    BOOLEAN   NOT NULL,
    discovered  TIMESTAMP NOT NULL,
    updated     TIMESTAMP NOT NULL
);

CREATE TABLE royalties
(
    address     BYTEA PRIMARY KEY,
    numerator   INTEGER   NOT NULL,
    denominator INTEGER   NOT NULL,
    destination BYTEA     NOT NULL,
    discovered  TIMESTAMP NOT NULL,
    updated     TIMESTAMP NOT NULL
);

CREATE TABLE sales
(
    address             BYTEA PRIMARY KEY,
    marketplace         BYTEA     NOT NULL,
    item                BYTEA     NOT NULL,
    owner               BYTEA     NOT NULL,
    full_price          BIGINT    NOT NULL,
    marketplace_fee     BIGINT    NOT NULL,
    royalty             BIGINT    NOT NULL,
    royalty_destination BYTEA     NOT NULL,
    discovered          TIMESTAMP NOT NULL,
    updated             TIMESTAMP NOT NULL
);

