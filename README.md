FetegeoImport
=============
Author: Pit Apps

This is a plugin for the OpenStreetMap data processing tool Osmosis.
It will process any OpenStreetMap data and output PostgreSQL files containing database import statments of the resulting data.
This data, when imported to a PostGIS enables PostgreSQL database can be used in conjunction with the Fetegeo geocoder.

#Requirements:
 - JDK 7
 - Osmosis 0.42

#Installation
After compiling the sources, osmosis requires a jar file to be built from the resulting classes.
This file must be built including the osmosis-plugins.conf file found at the root.

Getting Osmosis to recognise the new task is detailed here:

http://wiki.openstreetmap.org/wiki/Osmosis/Detailed_Usage#Plugin_Tasks

#Running
The plugin is called automatically from the Fetegeo importer.py script.

It can however also be run manually using the 'fimp' or 'fetegeo-import' suffix:

```
osmosis --read-xml file="luxembourg.osm" --fimp
```

This will output the PostgreSQL files to the current working directory.

A different output directory can be specified with the 'outdir' option:

```
osmosis --read-pbf-fast file="england.osm.pbf" --fetegeo-import outdir=/tmp/
```
