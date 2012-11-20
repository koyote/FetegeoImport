package org.pit.fetegeo.importer.objects;

import org.pit.fetegeo.importer.processors.CleverWriter;
import org.pit.fetegeo.importer.processors.HashMaker;
import org.pit.fetegeo.importer.processors.LocationProcessor;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 3:37 PM
 */
public class Address extends GenericTag {

  private String street;
  private String housenumber;
  public static Long addressId = 0l;
  public static Long addressNameId = 0l;

  public void setStreet(String street) {
    this.street = street;
  }

  public void setHousenumber(String housenumber) {
    this.housenumber = housenumber;
  }

  public String print() {
    return housenumber + " " + street;
  }

  public void write(CleverWriter addressWriter, CleverWriter nameWriter) {
    addressWriter.writeField(addressId);

    super.write(addressWriter, nameWriter);

    addressWriter.writeField(LocationProcessor.findLocation(this));
    addressWriter.writeField(this.getPostCodeId());

    nameWriter.writeField(addressNameId++);
    nameWriter.writeField(addressId);
    nameWriter.writeField(Constants.NULL_STRING); // language_id
    nameWriter.writeField(GenericTag.getTypeMap().get("name"));     // name_type
    nameWriter.writeField(this.print());
    nameWriter.writeField(HashMaker.getMD5Hash(this.print()));
    nameWriter.endRecord();

    addressId++;
    addressWriter.endRecord();
  }
}
