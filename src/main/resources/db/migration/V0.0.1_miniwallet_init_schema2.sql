DROP TABLE IF EXISTS txn_history;
DROP TABLE IF EXISTS txn_master;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS txn_ref_seq;

CREATE SEQUENCE txn_ref_seq START 1 MAXVALUE 99999999 CYCLE;

CREATE TABLE users (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE txn_master (
    id BIGSERIAL PRIMARY KEY,
    reference_number VARCHAR(18) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    source_user_id VARCHAR(20) REFERENCES users(id),
    destination_user_id VARCHAR(20) REFERENCES users(id),
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE txn_history (
    id BIGINT PRIMARY KEY,
    reference_number VARCHAR(18) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    source_user_id VARCHAR(20) REFERENCES users(id),
    destination_user_id VARCHAR(20) REFERENCES users(id),
    timestamp TIMESTAMP NOT NULL,
    move_date TIMESTAMP NOT NULL
);

CREATE INDEX idx_txn_master_source ON txn_master(source_user_id);
CREATE INDEX idx_txn_master_dest ON txn_master(destination_user_id);
CREATE INDEX idx_txn_master_status ON txn_master(status);

CREATE INDEX idx_txn_history_source ON txn_history(source_user_id);
CREATE INDEX idx_txn_history_dest ON txn_history(destination_user_id);