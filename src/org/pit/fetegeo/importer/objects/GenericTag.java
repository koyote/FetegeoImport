package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.processors.CleverWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 4:03 PM
 * <p/>
 * The GenericTag class acts as parent to all other Tags and stores common
 * information such as osmID, type and a list of names.
 */
public abstract class GenericTag {

  private Long osmId;
  private String type;
  private static final Map<String, Long> typeMap;
  private List<Name> nameList;

  static {
    typeMap = new HashMap<String, Long>();
  }

  public void setOsmId(Long osmId) {
    this.osmId = osmId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  List<Name> getNameList() {
    return nameList;
  }

  public void setNameList(List<Name> nameList) {
    this.nameList = nameList;
  }

  public static Map<String, Long> getTypeMap() {
    return typeMap;
  }

  /*
    Writes fields common to all Tags such as osmID and typeID
   */
  void write(CleverWriter copyFileWriter) {
    copyFileWriter.writeField(osmId);                         // write OSM_ID
    copyFileWriter.writeField(typeMap.get(this.getType()));   // write TYPE_ID
  }

  /*
    To be implemented by all children in order to write Tag-specific fields
   */
  public abstract void write();

}
