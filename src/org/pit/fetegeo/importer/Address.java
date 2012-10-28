package org.pit.fetegeo.importer;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 3:37 PM
 */
public class Address extends GenericTag {

  private String street;
  private String housenumber;

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
}