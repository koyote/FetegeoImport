package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.processors.LanguageProcessor;

import java.util.Map;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 2:19 PM
 */
public class Name {

  private String nameType;
  private final String name;
  private Long languageId;
  private boolean localised;

  public Name(String name, String nameType) {
    this.name = name;
    this.nameType = nameType;

    // Set language
    if (nameType.startsWith("name:")) {
      this.languageId = LanguageProcessor.findLanguageId(nameType.split(":")[1]);
      this.nameType = "name:";
      this.localised = true;
    } else {
      this.localised = false;
    }

    // Check if the type has been added already
    Map<String, Long> typeMap = GenericTag.getTypeMap();
    if (!typeMap.containsKey(this.nameType)) {
      typeMap.put(this.nameType, Long.valueOf(typeMap.size()));
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

  public boolean isLocalised() {
    return localised;
  }
}
