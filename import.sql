
DROP TYPE postcode_type CASCADE;
CREATE TYPE postcode_type AS ENUM (
 'Node',
 'Way',
 'Relation'
);


DROP TABLE place CASCADE;
CREATE TABLE place (
  place_id bigserial,
  osm_id bigint,
  type_id bigint,
  location geometry,
  postcode_id bigint,
  population bigint
);

DROP TABLE place_name CASCADE;
CREATE TABLE place_name (
 place_name_id bigserial,
 place_id bigint,
 lang_id bigint,
 type_id bigint,
 name text,
 name_hash varchar(32)
);

DROP TABLE address CASCADE;
CREATE TABLE address (
 address_id bigserial,
 osm_id bigint,
 type_id bigint,
 location geometry,
 postcode_id bigint
);

DROP TABLE address_name CASCADE;
CREATE TABLE address_name (
 address_name_id bigserial,
 address_id bigint,
 lang_id bigint,
 type_id bigint,
 name text,
 name_hash varchar(32)
);

DROP TABLE lang CASCADE;
CREATE TABLE lang (
 lang_id bigserial,
 iso639_1 char(2),
 iso639_2 char(3),
 name text
);

DROP TABLE postcode CASCADE;
CREATE TABLE postcode (
 postcode_id bigserial,
 osm_id bigint,
 type postcode_type,
 location geometry,
 name text
);

DROP TABLE type CASCADE;
CREATE TABLE type (
 type_id bigserial,
 name text
);

\copy address FROM 'address.txt'
\copy address_name FROM 'address_name.txt'
\copy lang FROM 'lang.txt'
\copy place FROM 'place.txt'
\copy place_name FROM 'place_name.txt'
\copy postcode FROM 'postcode.txt'
\copy type FROM 'type.txt'

