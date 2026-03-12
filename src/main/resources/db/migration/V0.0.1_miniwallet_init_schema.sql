DROP TABLE IF EXISTS txn_history;
DROP TABLE IF EXISTS txn_master;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE txn_master (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    source_user_id BIGINT REFERENCES users(id),
    destination_user_id BIGINT REFERENCES users(id),
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE txn_history (
    id BIGINT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    source_user_id BIGINT REFERENCES users(id),
    destination_user_id BIGINT REFERENCES users(id),
    timestamp TIMESTAMP NOT NULL,
    move_date TIMESTAMP NOT NULL
);

CREATE INDEX idx_txn_master_source ON txn_master(source_user_id);
CREATE INDEX idx_txn_master_dest ON txn_master(destination_user_id);
CREATE INDEX idx_txn_master_status ON txn_master(status);

CREATE INDEX idx_txn_history_source ON txn_history(source_user_id);
CREATE INDEX idx_txn_history_dest ON txn_history(destination_user_id);