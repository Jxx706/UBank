
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS movements;

CREATE TABLE clients(
  c_username VARCHAR PRIMARY KEY,
  c_password VARCHAR NOT NULL, 
  c_name     VARCHAR NOT NULL,
  c_address  VARCHAR(255) NOT NULL,
  c_phone    VARCHAR NOT NULL
);

CREATE TABLE accounts(
  c_username VARCHAR REFERENCES clients ON DELETE CASCADE,
  a_number     integer PRIMARY KEY,
  a_balance    float NOT NULL DEFAULT 0.0 CHECK (a_balance >= 0.0)
);

CREATE TABLE movements(
 a_number integer REFERENCES accounts ON DELETE CASCADE,
 m_type varchar NOT NULL CHECK (m_type in ('debit', 'credit')),
 m_date date NOT NULL
);


