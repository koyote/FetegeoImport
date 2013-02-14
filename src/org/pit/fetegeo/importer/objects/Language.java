package org.pit.fetegeo.importer.objects;

/**
 * Author: Pit Apps
 * Date: 13/02/13
 * Time: 23:46
 */
public class Language {

  private Long id;
  private String iso639_1;
  private String iso639_2;
  private String name;

  public Language(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getIso639_1() {
    return iso639_1;
  }

  public void setIso639_1(String iso639_1) {
    this.iso639_1 = iso639_1;
  }

  public String getIso639_2() {
    return iso639_2;
  }

  public void setIso639_2(String iso639_2) {
    this.iso639_2 = iso639_2;
  }

  public String getName() {
    return name;
  }
}
