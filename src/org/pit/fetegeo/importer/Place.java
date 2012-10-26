package org.pit.fetegeo.importer;

import java.util.List;

/**
 * Author: Pit Apps
 * Date: 10/25/12
 * Time: 5:37 PM
 */
public class Place extends GenericTag {

  private Long population;

  public Long getPopulation() {
    return population == null ? -1l : population;
  }

  public void setPopulation(Long population) {
    this.population = population;
  }

}
