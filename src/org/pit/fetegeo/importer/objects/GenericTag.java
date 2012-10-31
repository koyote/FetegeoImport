package org.pit.fetegeo.importer.objects;

import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;

import java.util.List;

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

  public void write(org.pit.fetegeo.importer.processors.CleverWriter copyFileWriter) {
    copyFileWriter.writeField(this.getId());
    copyFileWriter.writeField(this.getType());
  }

  public void write(org.pit.fetegeo.importer.processors.CleverWriter copyFileWriter, org.pit.fetegeo.importer.processors.CleverWriter nameWriter) {
    write(copyFileWriter);
  }
}
