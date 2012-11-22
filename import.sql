
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
  country_id bigint,
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
 main text,
 sup text
);

DROP TABLE type CASCADE;
CREATE TABLE type (
 type_id bigserial,
 name text
);

DROP TABLE country CASCADE;
CREATE TABLE country (
 country_id bigserial,
 iso3166_2 char(2),
 iso3166_3 char(3)
);

\copy address FROM 'address.txt'
\copy address_name FROM 'address_name.txt'
\copy lang FROM 'lang.txt'
\copy place FROM 'place.txt'
\copy place_name FROM 'place_name.txt'
\copy postcode FROM 'postcode.txt'
\copy type FROM 'type.txt'
\copy country FROM 'country.txt'

ALTER TABLE postcode ADD COLUMN country_id bigint;

CREATE INDEX place_location_idx ON place USING GIST(location);
CREATE INDEX address_location_idx ON address USING GIST(location);
CREATE INDEX postcode_location_idx ON postcode USING GIST(location);
CREATE INDEX place_country_idx ON place(country_id);

VACUUM ANALYZE;

-- WHY DOES THIS NOT WORK??????~!!!!! ALSO: VERY SLOW :(
UPDATE postcode
SET country_id = place.country_id
FROM place
WHERE place.country_id IS NOT NULL AND ST_Covers(place.location, postcode.location);


-- THIS WORKS WHEN THE LOOPS ARE REVERSED BUT ONLY FINDS ONE MATCH -_-
-- Takes 5 minutes

--CREATE OR REPLACE FUNCTION updatePostCodeCountry()
-- RETURNS VOID AS $$
--DECLARE
-- cty_id int;
-- cty_loc geometry;
-- count int;
-- total int;
-- pc_id int;
-- pc_loc geometry;
--BEGIN
-- SELECT count(*) INTO total FROM postcode;
-- count := total;
-- RAISE NOTICE 'Initial count: %',count;
-- FOR cty_id, cty_loc 99
-- LOOP
--  IF count % 100 = 0 THEN
--   RAISE NOTICE 'Remaining: %', (count*100/total);
--  END IF;
--  FOR pc_loc, pc_id IN SELECT location, postcode_id FROM postcode LOOP
--   IF ST_Covers(cty_loc, pc_loc) THEN
--    RAISE NOTICE 'IT COVERS!';
--    UPDATE postcode SET country_id = cty_id WHERE postcode_id = pc_id;
--   END IF;
--  END LOOP;
-- count := count - 1;
-- END LOOP;
--END;
--$$  LANGUAGE plpgsql
--
-- SELECT updatePostCodeCountry();
