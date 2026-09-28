--liquibase formatted sql

--changeset makson:1

CREATE TYPE report_status AS ENUM ('PENDING', 'SENT', 'FAILED');

--rollback DROP TYPE report_status;

--changeset makson:2

CREATE TABLE reports
(
    id              SERIAL PRIMARY KEY,
    recipient_email VARCHAR(256) NOT NULL,
    title           VARCHAR(256) NOT NULL,
    content         VARCHAR(256) NOT NULL,
    `period`        DATE,
    status          report_status DEFAULT 'PENDING'
);
--rollback DROP TABLE reports;
