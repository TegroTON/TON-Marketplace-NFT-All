CREATE TABLE "accounts"
(
    "address_workchain" INTEGER   NOT NULL,
    "address_hash"      BYTEA     NOT NULL,
    "discovered"        TIMESTAMP NOT NULL,
    "updated"           TIMESTAMP NOT NULL,
    PRIMARY KEY ("address_workchain", "address_hash")
);

CREATE TABLE "attributes"
(
    "id"             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "item_workchain" INTEGER      NOT NULL,
    "item_hash"      BYTEA        NOT NULL,
    "trait"          VARCHAR(255) NOT NULL,
    "value"          VARCHAR(255) NOT NULL
);

CREATE TABLE "collections"
(
    "address_workchain" INTEGER      NOT NULL,
    "address_hash"      BYTEA        NOT NULL,
    "next_item_index"   BIGINT       NOT NULL,
    "owner_workchain"   INTEGER      NOT NULL,
    "owner_hash"        BYTEA        NOT NULL,
    "name"              VARCHAR(255) NOT NULL,
    "description"       TEXT         NOT NULL,
    "image"             TEXT,
    "image_data"        BYTEA        NOT NULL,
    "cover_image"       TEXT,
    "cover_image_data"  BYTEA        NOT NULL,
    "discovered"        TIMESTAMP    NOT NULL,
    "updated"           TIMESTAMP    NOT NULL,
    "metadata_updated"  TIMESTAMP    NOT NULL,
    PRIMARY KEY ("address_workchain", "address_hash")
);

CREATE TABLE "items"
(
    "address_workchain"    INTEGER      NOT NULL,
    "address_hash"         BYTEA        NOT NULL,
    "initialized"          BOOLEAN      NOT NULL,
    "index"                BIGINT       NOT NULL,
    "collection_workchain" INTEGER,
    "collection_hash"      BYTEA,
    "owner_workchain"      INTEGER      NOT NULL,
    "owner_hash"           BYTEA        NOT NULL,
    "name"                 VARCHAR(255) NOT NULL,
    "description"          TEXT         NOT NULL,
    "image"                TEXT,
    "image_data"           BYTEA        NOT NULL,
    "discovered"           TIMESTAMP    NOT NULL,
    "updated"              TIMESTAMP    NOT NULL,
    "metadata_updated"     TIMESTAMP    NOT NULL,
    PRIMARY KEY ("address_workchain", "address_hash")
);

CREATE TABLE "royalties"
(
    "address_workchain"     INTEGER   NOT NULL,
    "address_hash"          BYTEA     NOT NULL,
    "numerator"             INTEGER   NOT NULL,
    "denominator"           INTEGER   NOT NULL,
    "destination_workchain" INTEGER   NOT NULL,
    "destination_hash"      BYTEA     NOT NULL,
    "discovered"            TIMESTAMP NOT NULL,
    "updated"               TIMESTAMP NOT NULL,
    PRIMARY KEY ("address_workchain", "address_hash")
);

CREATE TABLE "sales"
(
    "address_workchain"             INTEGER   NOT NULL,
    "address_hash"                  BYTEA     NOT NULL,
    "marketplace_workchain"         INTEGER   NOT NULL,
    "marketplace_hash"              BYTEA     NOT NULL,
    "item_workchain"                INTEGER   NOT NULL,
    "item_hash"                     BYTEA     NOT NULL,
    "owner_workchain"               INTEGER   NOT NULL,
    "owner_hash"                    BYTEA     NOT NULL,
    "full_price"                    BIGINT    NOT NULL,
    "marketplace_fee"               BIGINT    NOT NULL,
    "royalty"                       BIGINT    NOT NULL,
    "royalty_destination_workchain" INTEGER   NOT NULL,
    "royalty_destination_hash"      BYTEA     NOT NULL,
    "discovered"                    TIMESTAMP NOT NULL,
    "updated"                       TIMESTAMP NOT NULL,
    PRIMARY KEY ("address_workchain", "address_hash")
);

