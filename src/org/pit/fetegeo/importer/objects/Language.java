package org.pit.fetegeo.importer.objects;

/**
 * Author: Pit Apps
 * Date: 10/28/12
 * Time: 4:01 PM
 */
public class Language {

  private String iso639_1;
  private String iso639_2;
  private Long id;

  public Language(Long id) {
    this.id = id;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
