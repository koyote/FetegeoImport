package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.processors.CleverWriter;
import org.pit.fetegeo.importer.processors.LanguageProcessor;
import org.pit.fetegeo.importer.processors.LocationProcessor;

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

  public void write(CleverWriter addressWriter, CleverWriter nameWriter) {
    addressWriter.writeField(Address.addressId);

    super.write(addressWriter, nameWriter);

    addressWriter.writeField(LocationProcessor.findLocation(this));
    addressWriter.writeField(this.getPostCodeId());

    for (Name name : this.getNameList()) {
      nameWriter.writeField(Address.addressNameId++);
      nameWriter.writeField(Address.addressId);
      nameWriter.writeField(LanguageProcessor.findLanguageId(name.getLanguage()));
      nameWriter.writeField(name.getNameType());
      nameWriter.writeField(name.getName());
      nameWriter.endRecord();
    }

    Address.addressId++;
    addressWriter.endRecord();
  }
}
