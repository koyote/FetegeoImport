FetegeoImport
=============

FetegeoImport is a plugin for the OpenStreetMap data processing tool Osmosis.
It will process any OpenStreetMap data and output PostgreSQL files containing database import statments of the resulting data.
This data, when imported to a PostGIS enabled PostgreSQL database, can be used in conjunction with the Fetegeo geocoder.

##Requirements:
 - JDK 7
 - Osmosis 0.42

##Installation
Compiling the sources manually involves building a jar file from the resulting classes.
This file must be built including the osmosis-plugins.conf file found at the root.
Otherwise a pre-compiled .jar file can be found from [here](FetegeoImport.jar).

Getting Osmosis to recognise the new task is detailed here:

http://wiki.openstreetmap.org/wiki/Osmosis/Detailed_Usage#Plugin_Tasks

(it basically involves copying the .jar file to the Osmosis plugins directory)

##Usage
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

##Licence
    Copyright (C) 2013 Pit Apps
  
    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
    documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
    the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
    and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
    The above copyright notice and this permission notice shall be included in all copies or substantial portions 
    of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
    TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
    THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
    CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
    IN THE SOFTWARE.
