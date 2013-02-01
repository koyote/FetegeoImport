\timing

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
 iso3166_3 char(3),
 name text
);

\copy lang FROM 'lang.txt'
\copy place FROM 'place.txt'
\copy place_name FROM 'place_name.txt'
\copy postcode FROM 'postcode.txt'
\copy type FROM 'type.txt'
\copy country FROM 'country.txt'

ALTER TABLE postcode ADD COLUMN country_id bigint;
ALTER TABLE postcode ADD COLUMN parent_id bigint;
ALTER TABLE place ADD COLUMN parent_id bigint;

-- Make some indices
CREATE INDEX place_location_idx ON place USING GIST(location);
CREATE INDEX road_location_idx ON road USING GIST(location);
CREATE INDEX postcode_location_idx ON postcode USING GIST(location);
CREATE INDEX place_country_idx ON place USING btree(country_id);

-- Cluster it all!
CLUSTER place_location_idx ON place;
CLUSTER road_location_idx ON road;
CLUSTER postcode_location_idx ON postcode;

VACUUM ANALYZE;

---- Clean up locations (make linestring polygons if they are etc)
-- Do not change order of BuildArea vs CollectionHomogenize! Weird things happen!
UPDATE postcode
SET location = ST_BuildArea(location)
WHERE ST_BuildArea(location) IS NOT NULL;

UPDATE place
SET location = ST_BuildArea(location)
WHERE ST_BuildArea(location) IS NOT NULL;

UPDATE place
SET location = ST_CollectionHomogenize(location);

UPDATE postcode
SET location = ST_CollectionHomogenize(location);

UPDATE place
SET location = null
WHERE NOT ST_IsValid(location);


---- Update postcodes with their country
-- TODO: contains vs covers vs within? (they all seem to perform exactly the same)
UPDATE postcode
SET country_id = place.country_id
FROM place
WHERE place.country_id IS NOT NULL AND ST_Contains(place.location, postcode.location);

---- Update place's country_id
-- TODO: Combine this and the next query somehow?
UPDATE place
SET country_id = p2.country_id
FROM place as p2
WHERE p2.country_id IS NOT NULL AND ST_Contains(p2.location, place.location);

---- Update place's parents
-- TODO: do some performance testing and sanity checks  (Maybe cache ST_Area on Polygons?)
UPDATE place
SET parent_id = b_id
FROM (
  SELECT small.place_id as s_id, big.place_id as b_id
  FROM place as small, place as big
  WHERE ST_Area(big.location)= (
	  SELECT MIN(ST_Area(b2.location))
	  FROM place as b2
	  WHERE NOT ST_Equals(small.location, b2.location)
	  AND ST_Covers(b2.location, small.location)
	  AND (ST_GeometryType(b2.location) = 'ST_Polygon' OR ST_GeometryType(b2.location) = 'ST_MultiPolygon')
	  )
) pp
WHERE place_id = s_id;

---- Update postcode's parents
UPDATE postcode
SET parent_id = b_id
FROM (
  SELECT small.postcode_id as s_id, big.place_id as b_id
  FROM postcode as small, place as big
  WHERE ST_Area(big.location)= (
	  SELECT MIN(ST_Area(b2.location))
	  FROM place as b2
	  WHERE ST_Covers(b2.location, small.location)
	  AND (ST_GeometryType(b2.location) = 'ST_Polygon' OR ST_GeometryType(b2.location) = 'ST_MultiPolygon')
	  )
) pp
WHERE postcode_id = s_id;


---- TODO: Maybe this is faster:
-- select all polygon and multipolygon and order by st_area smallest descending
-- If current element e1 covers a location that has smaller area e2 and e2 does not yet have a parent; set e2 parent to e1

VACUUM ANALYZE;
