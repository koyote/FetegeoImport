package org.pit.fetegeo.importer.processors;

import org.openstreetmap.osmosis.core.store.StoreClassRegister;
import org.openstreetmap.osmosis.core.store.StoreReader;
import org.openstreetmap.osmosis.core.store.StoreWriter;
import org.openstreetmap.osmosis.core.store.Storeable;
import org.postgis.Point;

/**
 * Author: Pit Apps
 * Date: 19/01/13
 * Time: 13:06
 * <p/>
 * This class is essentially Point but implements Storeable so that it can be cached in a file.
 * This is used for storing Node objects without having to store all other data associated with a Node.
 */
public class CleverPoint extends Point implements Storeable {

  public CleverPoint(double x, double y) {
    super(x, y);
    this.srid = 4326; // Coordinate SRID
  }

  public CleverPoint(StoreReader sr, StoreClassRegister scr) {
    this(sr.readDouble(), sr.readDouble());
  }

  @Override
  public void store(StoreWriter sw, StoreClassRegister scr) {
    sw.writeDouble(super.getX());
    sw.writeDouble(super.getY());
  }
}
