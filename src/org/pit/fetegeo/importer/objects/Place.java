package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.processors.CleverWriter;
import org.pit.fetegeo.importer.processors.LanguageProcessor;
import org.pit.fetegeo.importer.processors.LocationProcessor;

/**
 * Author: Pit Apps
 * Date: 10/25/12
 * Time: 5:37 PM
 */
public class Place extends GenericTag {

  private Long population;
  private static Long placeId = 0l;
  private static Long placeNameId = 0l;

  public Long getPopulation() {
    return population == null ? -1l : population;
  }

  public void setPopulation(Long population) {
    this.population = population;
  }

  public void write(CleverWriter placeWriter, CleverWriter nameWriter) {
    placeWriter.writeField(placeId);

    super.write(placeWriter, nameWriter);

    placeWriter.writeField(LocationProcessor.findLocation(this));
    placeWriter.writeField(this.getPostCodeId());
    placeWriter.writeField(this.getPopulation());
    for (Name name : this.getNameList()) {
      nameWriter.writeField(placeNameId++);
      nameWriter.writeField(placeId);
      nameWriter.writeField(LanguageProcessor.findLanguageId(name.getLanguage()));
      nameWriter.writeField(name.getNameType());
      nameWriter.writeField(name.getName());
      nameWriter.endRecord();
    }
    placeId++;
    placeWriter.endRecord();
  }

}
