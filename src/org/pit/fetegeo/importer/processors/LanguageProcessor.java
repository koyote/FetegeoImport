package org.pit.fetegeo.importer.processors;

import org.pit.fetegeo.importer.objects.Constants;

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

  private final static Map<String, Long> languageMap = new HashMap<String, Long>();

  private final CleverWriter langWriter;

  public LanguageProcessor(CleverWriter langWriter) {
    this.langWriter = langWriter;

    try {
      fetchAndSaveLangs();
    } catch (IOException ioe) {
      System.out.print(ioe);
    }
  }

  private void fetchAndSaveLangs() throws IOException {
    URL url = new URL(Constants.LANG_ISO_CODE_URL);
    InputStream inputStream = url.openStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

    long langId = 0l;
    String line, name, iso639_1, iso639_2;

    while ((line = br.readLine()) != null) {
      if (line.startsWith("#")) continue;

      String[] tokens = line.split(";");

      if (tokens.length != 4) continue;

      iso639_1 = tokens[0];
      iso639_2 = tokens[1];
      name = tokens[2];

      langWriter.writeField(langId);

      if (!iso639_1.isEmpty()) {
        langWriter.writeField(iso639_1);
        languageMap.put(iso639_1, langId);
      } else {
        langWriter.writeField(Constants.NULL_STRING);
      }

      if (!iso639_2.isEmpty()) {
        iso639_2 = iso639_2.substring(0, 3); // cut off B and T
        langWriter.writeField(iso639_2);
        languageMap.put(iso639_2, langId);
      } else {
        langWriter.writeField(Constants.NULL_STRING);
      }

      langWriter.writeField(name);
      langWriter.endRecord();
      langId++;
    }

    br.close();
    inputStream.close();
  }

  public static Long findLanguageId(String code) {
    return languageMap.get(code);
  }

}
