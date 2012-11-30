package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.processors.CleverWriter;
import org.pit.fetegeo.importer.processors.HashMaker;
import org.pit.fetegeo.importer.processors.LocationProcessor;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 4:05 PM
 */
public class Highway extends Road {

  private String ref;

  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

}
