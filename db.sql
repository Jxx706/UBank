
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS movements;

CREATE TABLE users(
  u_username VARCHAR PRIMARY KEY,
  u_password VARCHAR NOT NULL,
  u_role     VARCHAR NOT NULL CHECK (u_role in ('admin', 'client'))
);

CREATE TABLE clients(
  u_username VARCHAR REFERENCES users,
  c_id       integer PRIMARY KEY,
  c_name     VARCHAR NOT NULL,
  c_address  VARCHAR(255) NOT NULL,
  c_phone    VARCHAR NOT NULL
);

CREATE TABLE accounts(
  c_id integer REFERENCES clients ON DELETE CASCADE,
  a_number     integer PRIMARY KEY,
  a_balance    float NOT NULL DEFAULT 0.0 CHECK (a_balance >= 0.0)
);

CREATE TABLE movements(
 a_number integer REFERENCES accounts ON DELETE CASCADE,
 m_type varchar NOT NULL CHECK (m_type in ('debit', 'credit')),
 m_date bigint NOT NULL,
 m_value float NOT NULL CHECK (m_value > 0.0)
);
