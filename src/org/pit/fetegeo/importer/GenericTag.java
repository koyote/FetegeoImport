package org.pit.fetegeo.importer;

import java.util.List;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 4:03 PM
 */
public abstract class GenericTag {

  private Long id;
  private String type;
  private List<Name> nameList;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Name> getNameList() {
    return nameList;
  }

  public void setNameList(List<Name> nameList) {
    this.nameList = nameList;
  }

}
