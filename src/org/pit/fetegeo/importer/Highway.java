package org.pit.fetegeo.importer;

import org.openstreetmap.osmosis.pgsimple.common.CopyFileWriter;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 4:05 PM
 */
public class Highway extends GenericTag {

  private String ref;

  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  // TODO: merge this with address or something.

  public void write(CopyFileWriter addressWriter, CopyFileWriter nameWriter) {
    addressWriter.writeField(Address.addressId);

    super.write(addressWriter, nameWriter);

    for (Name name : this.getNameList()) {
      nameWriter.writeField(Address.addressNameId++);
      nameWriter.writeField(Address.addressId);
      nameWriter.writeField(name.getNameType());
      nameWriter.writeField(LanguageProcessor.findLanguageId(name.getLanguage()));
      nameWriter.writeField(name.getName());
      nameWriter.endRecord();
    }

    Address.addressId++;
    addressWriter.endRecord();
  }
}
