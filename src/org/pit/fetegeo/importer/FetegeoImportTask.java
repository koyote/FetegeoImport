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
import org.pit.fetegeo.importer.objects.*;
import org.pit.fetegeo.importer.processors.*;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FetegeoImportTask implements Sink {

  private final LocationProcessor locationProcessor;
  private final TagProcessor tagProcessor;

  private final CompletableContainer container;
  private final CleverWriter addressWriter;
  private final CleverWriter addressNameWriter;
  private final CleverWriter placeWriter;
  private final CleverWriter placeNameWriter;
  private final CleverWriter postCodeWriter;
  private final CleverWriter typeWriter;

  public FetegeoImportTask(final File outdir) {

    String outPath = outdir.getAbsolutePath();
    System.out.println("The Output directory is: " + outPath);

    container = new CompletableContainer();

    new LanguageProcessor(container.add(new CleverWriter(new File(outPath, "lang.txt"))));
    locationProcessor = new LocationProcessor();
    tagProcessor = new TagProcessor();

    addressWriter = container.add(new CleverWriter(new File(outPath, "address.txt")));
    addressNameWriter = container.add(new CleverWriter(new File(outPath, "address_name.txt")));
    placeWriter = container.add(new CleverWriter(new File(outPath, "place.txt")));
    placeNameWriter = container.add(new CleverWriter(new File(outPath, "place_name.txt")));
    postCodeWriter = container.add(new CleverWriter(new File(outPath, "postcode.txt")));
    typeWriter = container.add(new CleverWriter(new File(outPath, "type.txt")));
  }


  @Override
  public void process(EntityContainer entityContainer) {
    Entity entity = entityContainer.getEntity();

    // Process locations
    locationProcessor.process(entity);

    // Process tags
    List<GenericTag> tagList = tagProcessor.process(entity);

    // Write to file
    for (GenericTag tag : tagList) {
      if (tag instanceof Place) {
        tag.write(placeWriter, placeNameWriter);
      } else if (tag instanceof Address || tag instanceof Highway) {
        tag.write(addressWriter, addressNameWriter);
      } else if (tag instanceof PostalCode) {
        tag.write(postCodeWriter);
      }
    }
  }

  @Override
  public void complete() {
    locationProcessor.printSize();
    writeTypes();
    container.complete();
  }

  @Override
  public void release() {
    container.release();
  }

  private void writeTypes() {
    Map<String, Long> typeMap = GenericTag.getTypeMap();
    for (String type : typeMap.keySet()) {
      typeWriter.writeField(typeMap.get(type));
      typeWriter.writeField(type);
      typeWriter.endRecord();
    }
  }

}
