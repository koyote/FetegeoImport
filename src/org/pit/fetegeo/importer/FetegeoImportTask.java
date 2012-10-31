/**
 * Author: Pit Apps
 * Date: 10/22/12
 * Time: 2:26 PM
 */
package org.pit.fetegeo.importer;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.lifecycle.CompletableContainer;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.pit.fetegeo.importer.objects.GenericTag;
import org.pit.fetegeo.importer.objects.Highway;
import org.pit.fetegeo.importer.objects.Place;
import org.pit.fetegeo.importer.objects.PostalCode;
import org.pit.fetegeo.importer.processors.CleverWriter;
import org.pit.fetegeo.importer.processors.LocationProcessor;
import org.pit.fetegeo.importer.processors.TagProcessor;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FetegeoImportTask implements Sink {

  private org.pit.fetegeo.importer.processors.LocationProcessor locationProcessor;
  private org.pit.fetegeo.importer.processors.TagProcessor tagProcessor;

  private CompletableContainer container;
  private org.pit.fetegeo.importer.processors.CleverWriter addressWriter;
  private org.pit.fetegeo.importer.processors.CleverWriter addressNameWriter;
  private org.pit.fetegeo.importer.processors.CleverWriter placeWriter;
  private org.pit.fetegeo.importer.processors.CleverWriter placeNameWriter;
  private org.pit.fetegeo.importer.processors.CleverWriter postCodeWriter;

  public FetegeoImportTask(final File outdir) {

    String outPath = outdir.getAbsolutePath();
    System.out.println("The Output directory is: " + outPath);

    container = new CompletableContainer();

    new org.pit.fetegeo.importer.processors.LanguageProcessor(container.add(new org.pit.fetegeo.importer.processors.CleverWriter(new File(outPath, "lang.txt"))));
    locationProcessor = new LocationProcessor();
    tagProcessor = new TagProcessor();

    addressWriter = container.add(new org.pit.fetegeo.importer.processors.CleverWriter(new File(outPath, "address.txt")));
    addressNameWriter = container.add(new org.pit.fetegeo.importer.processors.CleverWriter(new File(outPath, "address_name.txt")));
    placeWriter = container.add(new org.pit.fetegeo.importer.processors.CleverWriter(new File(outPath, "place.txt")));
    placeNameWriter = container.add(new org.pit.fetegeo.importer.processors.CleverWriter(new File(outPath, "place_name.txt")));
    postCodeWriter = container.add(new CleverWriter(new File(outPath, "postcode.txt")));
  }


  @Override
  public void process(EntityContainer entityContainer) {
    Entity entity = entityContainer.getEntity();

    // Process locations
    locationProcessor.process(entity);

    // Process tags
    List<org.pit.fetegeo.importer.objects.GenericTag> tagList = tagProcessor.process(entity);

    // Write to file
    for (GenericTag tag : tagList) {
      if (tag instanceof Place) {
        tag.write(placeWriter, placeNameWriter);
      } else if (tag instanceof org.pit.fetegeo.importer.objects.Address || tag instanceof Highway) {
        tag.write(addressWriter, addressNameWriter);
      } else if (tag instanceof PostalCode) {
        tag.write(postCodeWriter);
      }
    }
  }

  @Override
  public void initialize(Map<String, Object> stringObjectMap) {
    // What is this?
  }

  @Override
  public void complete() {
    locationProcessor.printSize();
    container.complete();
  }

  @Override
  public void release() {
    container.release();
  }
}
