package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.FetegeoImportTask;
import org.pit.fetegeo.importer.processors.CleverWriter;
import org.pit.fetegeo.importer.processors.LocationProcessor;

import java.io.File;

/**
 * Author: Pit Apps
 * Date: 10/31/12
 * Time: 5:09 PM
 */
public class PostalCode extends GenericTag {

  private static Long postCodeId = -1l;
  private final String postCode;

  private static final CleverWriter postCodeWriter;

  static {
    postCodeWriter = FetegeoImportTask.container.add(new CleverWriter(new File(Constants.OUT_PATH, "postcode.txt")));
  }


  public PostalCode(String postCode) {
    this.postCode = postCode;
    postCodeId++;
  }

  public Long getPostCodeId() {
    return postCodeId;
  }

  public void write() {
    postCodeWriter.writeField(postCodeId);                           // postcode_id
    super.write(postCodeWriter);                                     // OSM_ID, TYPE_ID
    postCodeWriter.writeField(LocationProcessor.findLocation()); // location
    postCodeWriter.writeField(postCode);                             // main
    postCodeWriter.writeField(Constants.NULL_STRING);                // sup; TODO: IMPLEMENT THIS
    postCodeWriter.endRecord();
  }
}
