package org.pit.fetegeo.importer;

import org.openstreetmap.osmosis.pgsimple.common.CopyFileWriter;

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

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getHousenumber() {
    return housenumber;
  }

  public void setHousenumber(String housenumber) {
    this.housenumber = housenumber;
  }

  public String print() {
    return housenumber + " " + street;
  }

  public void write(CopyFileWriter addressWriter, CopyFileWriter nameWriter) {
    String nullString = null;
    addressWriter.writeField(addressId);

    super.write(addressWriter, nameWriter);

    nameWriter.writeField(addressNameId++);
    nameWriter.writeField(addressId);
    nameWriter.writeField("name");
    nameWriter.writeField(nullString);
    nameWriter.writeField(this.print());
    nameWriter.endRecord();

    addressId++;
    addressWriter.endRecord();
  }
}
