package org.pit.fetegeo.importer.processors;

import org.openstreetmap.osmosis.pgsimple.common.CopyFileWriter;
import org.pit.fetegeo.importer.objects.Constants;

import java.io.File;

/**
 * Author: Pit Apps
 * Date: 10/31/12
 * Time: 6:04 PM
 */
public class CleverWriter extends CopyFileWriter {

  public CleverWriter(File file) {
    super(file);
  }

  // The normal CopyFileWriter does not handle Longs. We need them so that we can store nulls though.
  public void writeField(Long l) {
    if (l == null) {
      super.writeField(Constants.NULL_STRING);
    } else {
      super.writeField(l);
    }
  }

  public void writeField(Integer i) {
    if (i == null) {
      super.writeField(Constants.NULL_STRING);
    } else {
      super.writeField(i);
    }
  }
}
