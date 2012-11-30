package org.pit.fetegeo.importer.objects;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 3:37 PM
 */
public class Address extends Road {

  private String streetName;
  private String houseNumber;

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
  }

  /*
    Format houseNumber correctly if it exists
   */
  private String getHouseNumber() {
    if (houseNumber != null && !houseNumber.isEmpty()) {
      return houseNumber + ", ";
    }
    return "";
  }

  public String getRef() {
    if (streetName == null || streetName.isEmpty()) {
      return null;
    }
    return getHouseNumber() + streetName;
  }

}
