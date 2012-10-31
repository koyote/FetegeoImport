package org.pit.fetegeo.importer.objects;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 2:19 PM
 */
public class Name {

  private Long id;
  private String nameType;
  private String name;
  private String language;

  public Name(String name, String nameType) {
    this.name = name;
    this.nameType = nameType;

    // Set language
    if (nameType.startsWith("name:")) {
      String[] nameToken = nameType.split(":");
      setLanguage(nameToken[1]);
      setNameType("name");
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNameType() {
    return nameType;
  }

  public void setNameType(String nameType) {
    this.nameType = nameType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }


}
