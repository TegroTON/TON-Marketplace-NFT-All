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

INSERT INTO approvals
VALUES ('\xb5ee9c720101010100240000438007b8f9af6fca53ffcf481eeaebac66028fa5cfb11ab32c1355fc12bb4547325330', TRUE),
       ('\xb5ee9c720101010100240000438006eaf9f465fee73f51141424ce8f955699fe29a16256604f70fdc2f12c0e8669f0', TRUE),
       ('\xb5ee9c720101010100240000438001f7287b637e26b232cc595eb659cf325d160f51701815d695f57ded4b28bfa930', TRUE);
