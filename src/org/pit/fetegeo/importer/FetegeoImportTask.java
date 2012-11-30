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
import org.pit.fetegeo.importer.objects.Constants;
import org.pit.fetegeo.importer.objects.GenericTag;
import org.pit.fetegeo.importer.processors.*;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FetegeoImportTask implements Sink {

  private final LocationProcessor locationProcessor;
  private final TagProcessor tagProcessor;

  public static final CompletableContainer container = new CompletableContainer();

  private final CleverWriter typeWriter;

  public FetegeoImportTask(final File outDir) {

    Constants.OUT_PATH = outDir.getAbsolutePath();
    System.out.println("The Output directory is: " + Constants.OUT_PATH);

    new LanguageProcessor(container.add(new CleverWriter(new File(Constants.OUT_PATH, "lang.txt"))));
    new CountryCodeProcessor(container.add(new CleverWriter(new File(Constants.OUT_PATH, "country.txt"))));

    locationProcessor = new LocationProcessor();
    tagProcessor = new TagProcessor();

    typeWriter = container.add(new CleverWriter(new File(Constants.OUT_PATH, "type.txt")));
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
      tag.write();
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
      typeWriter.writeField(typeMap.get(type));                   // type_id
      typeWriter.writeField(type);                                // name
      typeWriter.endRecord();
    }
  }

}
