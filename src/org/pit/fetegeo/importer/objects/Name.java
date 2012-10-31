package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.processors.LanguageProcessor;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 2:19 PM
 */
public class Name {

  private String nameType;
  private String name;
  private Long languageId;

  public Name(String name, String nameType) {
    this.name = name;
    this.nameType = nameType;

    // Set language
    if (nameType.startsWith("name:")) {
      this.languageId = LanguageProcessor.findLanguageId(nameType.split(":")[1]);
      this.nameType = "name";
    }
  }

  public String getNameType() {
    return nameType;
  }

  public String getName() {
    return name;
  }

  public Long getLanguageId() {
    return languageId;
  }
}
