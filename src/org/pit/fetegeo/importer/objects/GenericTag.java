package org.pit.fetegeo.importer.objects;

import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.pit.fetegeo.importer.processors.CleverWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 4:03 PM
 */
public abstract class GenericTag {

  private Long id;
  private String type;
  private List<Name> nameList;
  private EntityType originEntity;
  private Long postCodeId;
  private static Map<String, Long> typeMap = new HashMap<String, Long>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Name> getNameList() {
    return nameList;
  }

  public void setNameList(List<Name> nameList) {
    this.nameList = nameList;
  }

  public EntityType getOriginEntity() {
    return originEntity;
  }

  public void setOriginEntity(EntityType originEntity) {
    this.originEntity = originEntity;
  }

  public Long getPostCodeId() {
    return postCodeId;
  }

  public void setPostCodeId(Long postCodeId) {
    this.postCodeId = postCodeId;
  }

  public static Map<String, Long> getTypeMap() {
    return typeMap;
  }

  public void write(CleverWriter copyFileWriter) {
    copyFileWriter.writeField(this.getId());                  // write OSM_ID
    copyFileWriter.writeField(typeMap.get(this.getType()));   // write TYPE_ID
  }

  public void write(CleverWriter copyFileWriter, CleverWriter nameWriter) {
    write(copyFileWriter);
  }
}
