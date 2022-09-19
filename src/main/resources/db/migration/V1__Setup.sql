-- Record of entities determining whether or not they're shown to the client. Note that:
--   - collections are hidden until explicitly approved=true
--   - stand-alone items without a collection are hidden until explicitly approved=true
--   - everything else (items, sales, accounts, etc) is shown until explicitly approved=false
CREATE TABLE approvals
(
    address   BYTEA PRIMARY KEY,
    approved  BOOLEAN     NOT NULL DEFAULT (FALSE),
    timestamp TIMESTAMPTZ NOT NULL DEFAULT (now())
);
