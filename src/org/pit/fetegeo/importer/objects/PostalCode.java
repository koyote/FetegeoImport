package org.pit.fetegeo.importer.objects;

/**
 * Author: Pit Apps
 * Date: 10/31/12
 * Time: 5:09 PM
 */
public class PostalCode extends GenericTag {

  private static Long postCodeId = -1l;
  private String postCode;

  public PostalCode(String postCode) {
    this.postCode = postCode;
    postCodeId++;
  }

  public String getPostCode() {
    return postCode;
  }

  public Long getPostCodeId() {
    return postCodeId;
  }

  public void write(org.pit.fetegeo.importer.processors.CleverWriter postCodeWriter) {
    postCodeWriter.writeField(postCodeId);

    super.write(postCodeWriter);

    postCodeWriter.writeField(org.pit.fetegeo.importer.processors.LocationProcessor.findLocation(this));
    postCodeWriter.writeField(postCode);
    postCodeWriter.endRecord();
  }
}
