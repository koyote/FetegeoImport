package org.pit.fetegeo.importer;

import java.util.List;

/**
 * Author: Pit Apps
 * Date: 10/25/12
 * Time: 5:37 PM
 */
public class Place {

  private Long id;
  private String placeType;
  private Long population;
  private List<Name> nameList;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPlaceType() {
    return placeType;
  }

  public void setPlaceType(String placeType) {
    this.placeType = placeType;
  }

  public Long getPopulation() {
    return population == null ? -1l : population;
  }

  public void setPopulation(Long population) {
    this.population = population;
  }

  public List<Name> getNameList() {
    return nameList;
  }

  public void setNameList(List<Name> nameList) {
    this.nameList = nameList;
  }

}
