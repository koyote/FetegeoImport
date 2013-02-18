package org.pit.fetegeo.importer.processors;

import org.pit.fetegeo.importer.objects.Constants;
import org.pit.fetegeo.importer.objects.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * Author: Pit Apps
 * Date: 10/28/12
 * Time: 3:26 PM
 * <p/>
 * Handles fetching language ISO data from the internet and saving it to a postgres import file
 * as well as storing the id to a map.
 */
public class LanguageProcessor {

  private final static Map<String, Long> langMap = new HashMap<String, Long>();
  private final static Set<Long> saveSet = new HashSet<Long>();
  private final static List<Language> languageList = new ArrayList<Language>();
  private final CleverWriter langWriter;

  /*
    Constructor takes a language writer object as parameter and immediately populates the langMap and lang.txt
   */
  public LanguageProcessor(CleverWriter langWriter) {
    this.langWriter = langWriter;

    try {
      fetchAndSaveLangs();
    } catch (IOException ioe) {
      System.out.println(ioe);
    }
  }

  /*
    This method finds language codes from LANG_ISO_CODE_URL, parses them and then adds them to
     the langMap and languageList.
   */
  private void fetchAndSaveLangs() throws IOException {
    URL url = new URL(Constants.LANG_ISO_CODE_URL);
    InputStream inputStream = url.openStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));

    long langId = 0l;
    String line, name, iso639_1, iso639_2;

    while ((line = br.readLine()) != null) {
      if (line.startsWith("#")) continue;

      String[] tokens = line.split(";");

      if (tokens.length < 3) continue;

      iso639_1 = tokens[0].trim().toUpperCase();
      iso639_2 = tokens[1].trim().toUpperCase();
      name = tokens[2].trim();

      // Clean up Names
      if (name.contains("/")) {
        name = name.split("/")[0];
      }

      Language language = new Language(langId, name);

      if (!iso639_1.isEmpty()) {
        iso639_1 = iso639_1.substring(0, 2);
        language.setIso639_1(iso639_1);
        langMap.put(iso639_1, langId);
      }

      if (!iso639_2.isEmpty()) {
        iso639_2 = iso639_2.substring(0, 3); // cut off B and T
        language.setIso639_2(iso639_2);
        langMap.put(iso639_2, langId);
      }

      languageList.add(language);
      langId++;
    }

    br.close();
    inputStream.close();
  }

  /*
    Returns the database id of a specified ISO language code

    Also adds the id to a Set. This is so that we can keep track of which languages are actually used
    during the import. There's no need to catalogue 500 obscure languages when only a small percentage of these
    are actually referenced. This is especially true for a web interface that displays a list of all available languages.
   */
  public static Long findLanguageId(String code) {
    Long id = langMap.get(code);
    saveSet.add(id);
    return id;
  }

  /*
    Only write languages that have been referenced to file.
   */
  public void write() {
    for (Language l : languageList) {
      if (saveSet.contains(l.getId())) {
        langWriter.writeField(l.getId());
        langWriter.writeField(l.getIso639_1());
        langWriter.writeField(l.getIso639_2());
        langWriter.writeField(l.getName());
        langWriter.endRecord();
      }
    }
  }
}
