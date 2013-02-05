\timing

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
 type_id bigint,
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

---- Fill tables with data
\copy lang FROM 'lang.txt'
\copy place FROM 'place.txt'
\copy place_name FROM 'place_name.txt'
\copy postcode FROM 'postcode.txt'
\copy type FROM 'type.txt'
\copy country FROM 'country.txt'

---- Add some special columns which take data from PostGIS calculations later in this script
ALTER TABLE postcode ADD COLUMN country_id bigint;
ALTER TABLE postcode ADD COLUMN parent_id bigint;
ALTER TABLE postcode ADD COLUMN area float;
ALTER TABLE place ADD COLUMN parent_id bigint;
ALTER TABLE place ADD COLUMN area float;

---- Create some indices
CREATE INDEX place_location_idx ON place USING GIST(location);
CREATE INDEX postcode_location_idx ON postcode USING GIST(location);
CREATE INDEX place_country_idx ON place(country_id);

---- Cluster it all!
CLUSTER place_location_idx ON place;
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

---- Get rid of invalid locations
--(TODO: maybe run Buffer or MakeValid on these)
UPDATE place
SET location = null
WHERE NOT ST_IsValid(location);

UPDATE postcode
SET location = null
WHERE NOT ST_IsValid(location);

---- Add postcodes' and places' country
UPDATE postcode
SET country_id = place.country_id
FROM place
WHERE place.country_id IS NOT NULL AND ST_Covers(place.location, postcode.location);

UPDATE place
SET country_id = p2.country_id
FROM place as p2
WHERE p2.country_id IS NOT NULL AND ST_Covers(p2.location, place.location);

---- Separately calculate area (speeds up the next updates)
UPDATE place
SET area = ST_Area(location);
CREATE INDEX place_area_idx ON place(area);
UPDATE postcode
SET area = ST_Area(location);
CREATE INDEX postcode_area_idx ON postcode(area);

---- Update place's parents
-- For some reason, the scheduler makes ST_Area+area nearly twice as fast as opposed to having 'WHERE big.area = MIN(b2.area)'...
UPDATE place
SET parent_id = b_id
FROM (
  SELECT small.place_id as s_id, big.place_id as b_id
  FROM place as small, place as big
  WHERE ST_Area(big.location) = (
	  SELECT MIN(b2.area)
	  FROM place as b2
	  WHERE NOT ST_Equals(small.location, b2.location)
	  AND ST_Covers(b2.location, small.location)
	  AND (ST_GeometryType(b2.location) IN ('ST_Polygon', 'ST_MultiPolygon'))
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
	  SELECT MIN(b2.area)
	  FROM place as b2
	  WHERE NOT ST_Equals(small.location, b2.location)
	  AND ST_Covers(b2.location, small.location)
	  AND (ST_GeometryType(b2.location) IN ('ST_Polygon', 'ST_MultiPolygon'))
	  )
) pp
WHERE postcode_id = s_id;


-- Turns out this is about 40% slower than the above :(
-- Keeping it here for keepsakes...
--
-- select all polygon and multipolygon and order by st_area smallest descending
-- If current element e1 covers a location that has smaller area e2 and e2 does not yet have a parent; set e2 parent to e1
--CREATE OR REPLACE TYPE PlaceCombo AS (location geometry, parent_id bigint, place_id bigint);
--
--CREATE OR REPLACE FUNCTION place_parent() RETURNS boolean AS $$
--DECLARE
--    candidates PlaceCombo[];
--    polygon PlaceCombo;
--    candidate PlaceCombo;
--BEGIN
--  FOR polygon IN select location, parent_id, place_id from place where location is not null order by ST_Area(location) asc LOOP
--    IF ST_GeometryType(polygon.location) in ('ST_Polygon','ST_MultiPolygon')  THEN
--      FOREACH candidate IN ARRAY candidates LOOP
--        IF candidate.parent_id IS NULL THEN
--          IF ST_Covers(polygon.location, candidate.location) THEN
--            candidate.parent_id := polygon.place_id;
--            UPDATE place SET parent_id = polygon.place_id WHERE place_id = candidate.place_id;
--          END IF;
--        END IF;
--      END LOOP;
--    END IF;
--    candidates := array_append(candidates, polygon);
--  END LOOP;
--  return true;
--END;
--$$
--LANGUAGE plpgsql;

VACUUM ANALYZE;
