package org.pit.fetegeo.importer.objects;

import java.io.File;

/**
 * Author: Pit Apps
 * Date: 10/31/12
 * Time: 6:00 PM
 * <p/>
 * This class contains constants used throughout the program.
 */
public class Constants {

  // Null String needed for the writer to write an empty Postgres field
  public static final String NULL_STRING = null;

  // URL for a comma-separated-list of language ISO codes
  public static final String LANG_ISO_CODE_URL = "http://opengeocode.org/download/iso639lang.txt";

  // URL for a comma-separated-list of country ISO codes
  public static final String COUNTRY_ISO_CODE_URL = "http://opengeocode.org/download/countrynames.txt";

  // The output directory
  public static File OUT_PATH;
}
