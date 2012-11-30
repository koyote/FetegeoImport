package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.FetegeoImportTask;
import org.pit.fetegeo.importer.processors.CleverWriter;
import org.pit.fetegeo.importer.processors.HashMaker;
import org.pit.fetegeo.importer.processors.LocationProcessor;

import java.io.File;

/**
 * Author: Pit Apps
 * Date: 30/11/12
 * Time: 20:48
 */
public abstract class Road extends GenericTag {

  private static Long roadId = 0l;
  private static Long roadNameId = 0l;

  private static final CleverWriter roadWriter;
  private static final CleverWriter roadNameWriter;

  protected abstract String getRef();

  static {
    roadNameWriter = FetegeoImportTask.container.add(new CleverWriter(new File(Constants.OUT_PATH, "road_name.txt")));
    roadWriter = FetegeoImportTask.container.add(new CleverWriter(new File(Constants.OUT_PATH, "road.txt")));
  }

  public void write() {
    roadWriter.writeField(roadId);                                       // address_id
    super.write(roadWriter);                                             // OSM_ID, TYPE_ID
    roadWriter.writeField(LocationProcessor.findLocation(this));         // location
    roadWriter.writeField(this.getPostCodeId());                         // postcode_id

    for (Name name : this.getNameList()) {
      roadNameWriter.writeField(roadNameId++);                                      // address_name_id
      roadNameWriter.writeField(roadId);                                            // address_id
      roadNameWriter.writeField(name.getLanguageId());                              // lang_id
      roadNameWriter.writeField(GenericTag.getTypeMap().get(name.getNameType()));   // type_id
      roadNameWriter.writeField(name.getName());                                    // name
      roadNameWriter.writeField(HashMaker.getMD5Hash(name.getName()));              // name_hash
      roadNameWriter.writeField(getRef());                                          // ref
      roadNameWriter.writeField(HashMaker.getMD5Hash(getRef()));                    // ref_hash
      roadNameWriter.endRecord();
    }

    roadId++;
    roadWriter.endRecord();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{ ");
    sb.append("roadId : ").append(roadId);
    sb.append("roadNameId : ").append(roadNameId);
    sb.append("PostCodeId : ").append(this.getPostCodeId());
    sb.append("[ names: ");
    sb.append(this.getNameList().toString());
    sb.append("]}");
    return sb.toString();
  }

}
