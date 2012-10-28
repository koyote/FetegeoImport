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
import org.openstreetmap.osmosis.pgsimple.common.CopyFileWriter;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FetegeoImportTask implements Sink {

  private LocationProcessor locationProcessor;
  private TagProcessor tagProcessor;

  private CompletableContainer container;
  private CopyFileWriter addressWriter;
  private CopyFileWriter addressNameWriter;
  private CopyFileWriter placeWriter;
  private CopyFileWriter placeNameWriter;

  public FetegeoImportTask(final File outdir) {

    String outPath = outdir.getAbsolutePath();
    System.out.println("The Output directory is: " + outPath);

    container = new CompletableContainer();

    new LanguageProcessor(container.add(new CopyFileWriter(new File(outPath, "lang.txt"))));
    locationProcessor = new LocationProcessor();
    tagProcessor = new TagProcessor();

    addressWriter = container.add(new CopyFileWriter(new File(outPath, "address.txt")));
    addressNameWriter = container.add(new CopyFileWriter(new File(outPath, "address_name.txt")));
    placeWriter = container.add(new CopyFileWriter(new File(outPath, "place.txt")));
    placeNameWriter = container.add(new CopyFileWriter(new File(outPath, "place_name.txt")));

  }


  @Override
  public void process(EntityContainer entityContainer) {
    Entity entity = entityContainer.getEntity();

    // Process locations
    locationProcessor.process(entity);

    // Process tags
    List<GenericTag> tagList = tagProcessor.process(entity);

    // Write to file
    if (tagList != null) {
      for (GenericTag tag : tagList) {
        if (tag instanceof Place) {
          tag.write(placeWriter, placeNameWriter);
        } else if (tag instanceof Address || tag instanceof Highway) {
          tag.write(addressWriter, addressNameWriter);
        }
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
