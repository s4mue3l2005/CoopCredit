CREATE TABLE affiliates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    document VARCHAR(50) NOT NULL UNIQUE,
    salary NUMERIC(19, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE credits (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    term INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    rationale TEXT,
    CONSTRAINT fk_credits_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates(id)
);
