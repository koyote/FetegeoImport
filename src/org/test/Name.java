package org.test;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 2:19 PM
 */
public class Name {

  private Long id;
  private String nameType;
  private String name;

  public Name(String name, String nameType) {
    this.name = name;
    this.nameType = nameType;
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
}
