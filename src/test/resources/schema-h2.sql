CREATE TABLE IF NOT EXISTS product
(
    id                  SERIAL         PRIMARY KEY,
    name                VARCHAR(30),
    min_sum             NUMERIC(19, 4) NOT NULL,
    max_sum             NUMERIC(19, 4) NOT NULL,
    currency_code       VARCHAR(3)     NOT NULL,
    min_interest_rate   NUMERIC(6, 4)  NOT NULL,
    max_interest_rate   NUMERIC(6, 4)  NOT NULL,
    need_guarantees     BOOLEAN        NOT NULL DEFAULT FALSE,
    delivery_in_cash    BOOLEAN        NOT NULL DEFAULT TRUE,
    early_repayment     BOOLEAN        NOT NULL DEFAULT TRUE,
    need_income_details BOOLEAN        NOT NULL DEFAULT FALSE,
    min_period_months   INTEGER        NOT NULL,
    max_period_months   INTEGER        NOT NULL,
    is_active           BOOLEAN        NOT NULL DEFAULT TRUE,
    details             VARCHAR(255)   NOT NULL,
    calculation_mode    VARCHAR(30)    NOT NULL,
    grace_period_months INTEGER,
    rate_is_adjustable  BOOLEAN,
    rate_base           VARCHAR(20),
    rate_fix_part       NUMERIC(6, 4),
    increased_rate      NUMERIC(6, 4),
    auto_processing     BOOLEAN        NOT NULL DEFAULT FALSE,
    type_credit         VARCHAR(11)    NOT NULL default 'CONSUMER',
    CONSTRAINT max_sum_check           CHECK ( max_sum >= min_sum ),
    CONSTRAINT max_interest_rate_check CHECK ( max_interest_rate >= min_interest_rate ),
    CONSTRAINT max_period_months_check CHECK ( max_period_months >= min_period_months )
);

CREATE TABLE IF NOT EXISTS credit_order
(
    id                             UUID           PRIMARY KEY,
    number                         VARCHAR(20),
    client_id                      UUID           NOT NULL,
    product_id                     INTEGER        NOT NULL REFERENCES product (id),
    status                         VARCHAR(30)    NOT NULL,
    amount                         NUMERIC(19, 4) NOT NULL,
    period_months                  INTEGER        NOT NULL,
    creation_date                  DATE           NOT NULL,
    monthly_income                 NUMERIC(19, 4),
    monthly_expenditure            NUMERIC(19, 4),
    employer_identification_number VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS credit
(
    id                  UUID           PRIMARY KEY,
    order_id            UUID           NOT NULL REFERENCES credit_order (id),
    type                VARCHAR(30)    NOT NULL,
    credit_limit        NUMERIC(19, 4) ,
    currency_code       VARCHAR(3)     NOT NULL,
    interest_rate       NUMERIC(19, 4) NOT NULL,
    personal_guarantees BOOLEAN        NOT NULL,
    grace_period_months INTEGER,
    status              VARCHAR(30)    NOT NULL,
    late_payment_rate   NUMERIC(6, 4),
    CONSTRAINT credit_limit_check      CHECK ( credit_limit >= 0 )
);

CREATE TABLE IF NOT EXISTS agreement
(
    id                        UUID         PRIMARY KEY,
    credit_id                 UUID         UNIQUE NOT NULL REFERENCES credit (id),
    number                    VARCHAR(20)  NOT NULL,
    agreement_date            DATE         NOT NULL,
    termination_date          DATE         NOT NULL,
    responsible_specialist_id VARCHAR(20),
    is_active                 BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT termination_date_check      CHECK ( now() < termination_date )
);

CREATE TABLE IF NOT EXISTS account
(
    id                        UUID           PRIMARY KEY,
    credit_id                 UUID           NOT NULL UNIQUE REFERENCES credit (id),
    account_number            VARCHAR(30)    NOT NULL,
    principal_debt            NUMERIC(19, 4) NOT NULL,
    interest_debt             NUMERIC(19, 4) NOT NULL,
    is_active                 BOOLEAN        NOT NULL DEFAULT TRUE,
    opening_date              DATE           NOT NULL,
    closing_date              DATE           NOT NULL,
    currency_code             VARCHAR(3)     NOT NULL,
    outstanding_principal     NUMERIC(19, 4),
    outstanding_interest_debt NUMERIC(19, 4),
    CONSTRAINT interest_debt_check           CHECK ( interest_debt >= 0 ),
    CONSTRAINT closing_date_check            CHECK ( closing_date > opening_date )
);

CREATE TABLE IF NOT EXISTS operation_type
(
    id       UUID        PRIMARY KEY,
    type     VARCHAR(30) NOT NULL,
    is_debit BOOLEAN     NOT NULL
);

INSERT IGNORE INTO operation_type
VALUES ('e3376ede-920b-4f48-abda-b565dd29a102', 'REPLENISHMENT', true);
INSERT IGNORE INTO operation_type
VALUES ('595cef39-29f3-4bf9-b9e1-30198a1d3df1', 'PAYMENT', false);
INSERT IGNORE INTO operation_type
VALUES ('f806a8b9-82dd-444f-9e93-429e1e42d480', 'TRANSFER', false);
INSERT IGNORE INTO operation_type
VALUES ('b02d27b1-3716-47c6-8160-d7e9ca4b6f6a', 'OTHER_EXPENDITURE', false);

CREATE TABLE IF NOT EXISTS operation
(
    id                UUID                        PRIMARY KEY,
    account_id        UUID                        NOT NULL REFERENCES account (id),
    operation_type_id UUID                        NOT NULL REFERENCES operation_type (id),
    sum               NUMERIC(19, 4)              NOT NULL,
    completed_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    details           TEXT,
    currency_code     VARCHAR(3)                  NOT NULL
);

CREATE TABLE IF NOT EXISTS card
(
    id                UUID             PRIMARY KEY,
    card_number       CHAR(16)         NOT NULL,
    account_id        UUID             NOT NULL REFERENCES account (id),
    holder_name       VARCHAR(50)      NOT NULL,
    expiration_date   DATE             NOT NULL,
    payment_system    VARCHAR(30)      NOT NULL,
    balance           NUMERIC(19, 4)   NOT NULL,
    status            VARCHAR(30)      NOT NULL,
    transaction_limit NUMERIC(19, 4),
    delivery_point    VARCHAR(30)      NOT NULL,
    is_digital_wallet BOOLEAN,
    is_virtual        BOOLEAN,
    co_brand          VARCHAR(30),
    CONSTRAINT balance_check           CHECK ( balance >= 0 ),
    CONSTRAINT transaction_limit_check CHECK ( transaction_limit >= 0 )
);

CREATE TABLE IF NOT EXISTS payment_schedule
(
    id             UUID           PRIMARY KEY,
    account_id     UUID           NOT NULL REFERENCES account (id),
    payment_date   DATE           NOT NULL,
    principal      NUMERIC(19, 4) NOT NULL,
    interest       NUMERIC(19, 4) NOT NULL,
    CONSTRAINT principal_check    CHECK ( principal >= 0 ),
    CONSTRAINT interest_check     CHECK ( interest >= 0 )
);