package org.pit.fetegeo.importer;

import org.openstreetmap.osmosis.pgsimple.common.CopyFileWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Pit Apps
 * Date: 10/28/12
 * Time: 3:26 PM
 */
public class LanguageProcessor {

  private static final String OPENGEO_URL = "http://opengeocode.org/download/iso639lang.txt";
  private static Map<String, Language> languageMap = new HashMap<String, Language>();

  private CleverWriter langWriter;

  public LanguageProcessor(CleverWriter langWriter) {
    this.langWriter = langWriter;

    try {
      fetchAndSaveLangs();
    } catch (IOException ioe) {
      System.out.print(ioe);
    }
  }

  private void fetchAndSaveLangs() throws IOException {
    URL fetchURL = new URL(OPENGEO_URL);
    InputStream inputStream = fetchURL.openStream();

    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

    long langId = 0l;
    String line, name, iso639_1, iso639_2;

    while ((line = br.readLine()) != null) {
      String[] tokens = line.split(";");
      if (tokens.length != 4 || tokens[0].startsWith("#")) continue;

      Language language = new Language(langId);
      langWriter.writeField(langId++);

      iso639_1 = tokens[0];
      iso639_2 = tokens[1];
      name = tokens[2];

      if (!iso639_1.isEmpty()) {
        language.setIso639_1(iso639_1);
        langWriter.writeField(iso639_1);
        languageMap.put(iso639_1, language);
      } else {
        langWriter.writeField(Constants.NULL_STRING);
      }
      if (!iso639_2.isEmpty()) {
        language.setIso639_2(iso639_2);
        langWriter.writeField(iso639_2);
        languageMap.put(iso639_2, language);
      } else {
        langWriter.writeField(Constants.NULL_STRING);
      }

      langWriter.writeField(name);
      langWriter.endRecord();
    }

    br.close();
    inputStream.close();
  }

  public static Long findLanguageId(String code) {
    Language language = languageMap.get(code);

    return language == null ? null : language.getId();
  }

}
