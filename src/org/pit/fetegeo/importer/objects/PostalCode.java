package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.FetegeoImportTask;
import org.pit.fetegeo.importer.processors.CleverWriter;
import org.pit.fetegeo.importer.processors.LocationProcessor;

import java.io.File;

/**
 * Author: Pit Apps
 * Date: 10/31/12
 * Time: 5:09 PM
 * <p/>
 * Stores all data relevant to a PostalCode
 */
public class PostalCode extends GenericTag {

  private static Long postCodeId = -1l;
  private final String main;
  private final String sup;

  private static final CleverWriter postCodeWriter;

  static {
    postCodeWriter = FetegeoImportTask.container.add(new CleverWriter(new File(Constants.OUT_PATH, "postcode.txt")));
  }

  /*
    Constructor for single postcode
    Automatically increases the ID for each instance.
   */
  public PostalCode(String postCode) {
    this.main = postCode;
    this.sup = null;
    postCodeId++;
  }

  /*
    Constructor for postcode with main and sup
   */
  public PostalCode(String main, String sup) {
    this.main = main;
    this.sup = sup;
    postCodeId++;
  }

  public void write() {
    postCodeWriter.writeField(postCodeId);                           // postcode_id
    super.write(postCodeWriter);                                     // OSM_ID, TYPE_ID
    postCodeWriter.writeField(LocationProcessor.findLocation());     // location
    postCodeWriter.writeField(main);                                 // main
    postCodeWriter.writeField(sup);                                  // sup;
    postCodeWriter.writeField(Constants.NULL_STRING);                // country_id
    postCodeWriter.writeField(Constants.NULL_STRING);                // parent_id
    postCodeWriter.writeField(Constants.NULL_STRING);                // area
    postCodeWriter.endRecord();
  }
}
