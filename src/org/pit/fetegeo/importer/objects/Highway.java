package org.pit.fetegeo.importer.objects;

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

  public void write(org.pit.fetegeo.importer.processors.CleverWriter addressWriter, org.pit.fetegeo.importer.processors.CleverWriter nameWriter) {
    addressWriter.writeField(Address.addressId);

    super.write(addressWriter, nameWriter);

    addressWriter.writeField(org.pit.fetegeo.importer.processors.LocationProcessor.findLocation(this));
    addressWriter.writeField(this.getPostCodeId());

    for (Name name : this.getNameList()) {
      nameWriter.writeField(Address.addressNameId++);
      nameWriter.writeField(Address.addressId);
      nameWriter.writeField(org.pit.fetegeo.importer.processors.LanguageProcessor.findLanguageId(name.getLanguage()));
      nameWriter.writeField(name.getNameType());
      nameWriter.writeField(name.getName());
      nameWriter.endRecord();
    }

    Address.addressId++;
    addressWriter.endRecord();
  }
}
