# --- !Ups

CREATE TABLE customer (
    id serial NOT NULL PRIMARY KEY,
    name varchar NOT NULL
);

# --- !Downs

DROP TABLE customer;