CREATE TABLE "accounts"
(
    "address"    BYTEA PRIMARY KEY,
    "discovered" TIMESTAMP NOT NULL,
    "updated"    TIMESTAMP NOT NULL
);

CREATE TABLE "attributes"
(
    "id"    BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "item"  BYTEA        NOT NULL,
    "trait" VARCHAR(255) NOT NULL,
    "value" VARCHAR(255) NOT NULL
);

CREATE TABLE "collections"
(
    "address"          BYTEA PRIMARY KEY,
    "next_item_index"  BIGINT    NOT NULL,
    "owner"            BYTEA     NOT NULL,
    "name"             VARCHAR(255),
    "description"      TEXT,
    "image"            TEXT,
    "image_data"       BYTEA     NOT NULL,
    "cover_image"      TEXT,
    "cover_image_data" BYTEA     NOT NULL,
    "discovered"       TIMESTAMP NOT NULL,
    "updated"          TIMESTAMP NOT NULL,
    "metadata_updated" TIMESTAMP NOT NULL
);

CREATE TABLE "items"
(
    "address"          BYTEA PRIMARY KEY,
    "initialized"      BOOLEAN   NOT NULL,
    "index"            BIGINT    NOT NULL,
    "collection"       BYTEA     NOT NULL,
    "owner"            BYTEA     NOT NULL,
    "name"             VARCHAR(255),
    "description"      TEXT,
    "image"            TEXT,
    "image_data"       BYTEA     NOT NULL,
    "discovered"       TIMESTAMP NOT NULL,
    "updated"          TIMESTAMP NOT NULL,
    "metadata_updated" TIMESTAMP NOT NULL
);

CREATE TABLE "royalties"
(
    "address"     BYTEA PRIMARY KEY,
    "numerator"   INTEGER   NOT NULL,
    "denominator" INTEGER   NOT NULL,
    "destination" BYTEA     NOT NULL,
    "discovered"  TIMESTAMP NOT NULL,
    "updated"     TIMESTAMP NOT NULL
);

CREATE TABLE "sales"
(
    "address"             BYTEA PRIMARY KEY,
    "marketplace"         BYTEA     NOT NULL,
    "item"                BYTEA     NOT NULL,
    "owner"               BYTEA     NOT NULL,
    "full_price"          BIGINT    NOT NULL,
    "marketplace_fee"     BIGINT    NOT NULL,
    "royalty"             BIGINT    NOT NULL,
    "royalty_destination" BYTEA     NOT NULL,
    "discovered"          TIMESTAMP NOT NULL,
    "updated"             TIMESTAMP NOT NULL
);

