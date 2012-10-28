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

  private LanguageProcessor languageProcessor;
  private LocationProcessor locationProcessor;
  private TagProcessor tagProcessor;

  private CompletableContainer container;
  private CopyFileWriter addressWriter;            // having separate address table makes searching faster
  private CopyFileWriter addressNameWriter;
  private CopyFileWriter placeWriter;
  private CopyFileWriter placeNameWriter;
  private CopyFileWriter postcodeWriter;

  private Long placeId = 0l;
  private Long placeNameId = 0l;
  private Long addressId = 0l;
  private Long addressNameId = 0l;


  public FetegeoImportTask(final File outdir) {

    String outPath = outdir.getAbsolutePath();
    System.out.println("The Output directory is: " + outPath);

    container = new CompletableContainer();

    languageProcessor = new LanguageProcessor(container.add(new CopyFileWriter(new File(outPath, "lang.txt"))));
    locationProcessor = new LocationProcessor();
    tagProcessor = new TagProcessor();

    addressWriter = container.add(new CopyFileWriter(new File(outPath, "address.txt")));
    addressNameWriter = container.add(new CopyFileWriter(new File(outPath, "address_name.txt")));
    placeWriter = container.add(new CopyFileWriter(new File(outPath, "place.txt")));
    placeNameWriter = container.add(new CopyFileWriter(new File(outPath, "place_name.txt")));
    postcodeWriter = container.add(new CopyFileWriter(new File(outPath, "postcode.txt")));

  }


  @Override
  public void process(EntityContainer entityContainer) {
    Entity entity = entityContainer.getEntity();

    // Process locations
    locationProcessor.process(entity);

    // Process tags
    List<GenericTag> tagList = tagProcessor.processTags(entity);

    // Write to place file
    if (tagList != null) {
      for (GenericTag tag : tagList) {
        write(tag);
      }
    }
  }

  private void write(GenericTag tag) {
    if (tag instanceof Place) {
      writePlace((Place) tag);
    } else if (tag instanceof Address || tag instanceof Highway) {
      writeRoad(tag);
    }

  }

  // TODO: get rid of duplicate code (implement write() inside GenericTag?)
  private void writePlace(Place place) {
    if (place == null) return;

    placeWriter.writeField(placeId);
    placeWriter.writeField(place.getId());
    placeWriter.writeField(place.getType());
    placeWriter.writeField(place.getPopulation());
    placeWriter.writeField(locationProcessor.findLocation(place));

    for (Name name : place.getNameList()) {
      placeNameWriter.writeField(placeNameId++);
      placeNameWriter.writeField(placeId);
      placeNameWriter.writeField(name.getNameType());
      placeNameWriter.writeField(languageProcessor.findLanguageId(name.getLanguage()));
      placeNameWriter.writeField(name.getName());
      placeNameWriter.endRecord();
    }
    placeId++;
    placeWriter.endRecord();
  }

  private void writeRoad(GenericTag tag) {
    if (tag == null) return;

    addressWriter.writeField(addressId);
    addressWriter.writeField(tag.getId());
    addressWriter.writeField(tag.getType());
    addressWriter.writeField(locationProcessor.findLocation(tag));

    if (tag.getNameList() != null) {  // "addr" will be null here
      for (Name name : tag.getNameList()) {
        addressNameWriter.writeField(addressNameId++);
        addressNameWriter.writeField(addressId);
        addressNameWriter.writeField(name.getNameType());
        addressNameWriter.writeField(languageProcessor.findLanguageId(name.getLanguage()));
        addressNameWriter.writeField(name.getName());
        addressNameWriter.endRecord();
      }
    } else { // for "addr" tags
      addressNameWriter.writeField(addressNameId++);
      addressNameWriter.writeField(addressId);
      addressNameWriter.writeField("name");
      addressNameWriter.writeField(((Address) tag).print());
      addressNameWriter.endRecord();
    }
    addressId++;
    addressWriter.endRecord();

  }

  @Override
  public void initialize(Map<String, Object> stringObjectMap) {
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
