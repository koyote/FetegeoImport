package org.pit.fetegeo.importer;

import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.pgsimple.common.PointBuilder;
import org.openstreetmap.osmosis.pgsimple.common.PolygonBuilder;
import org.postgis.Geometry;
import org.postgis.Point;
import org.postgis.Polygon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 3:43 PM
 */
public class LocationProcessor {


  private Map<Long, Point> nodeMap;
  private Map<Long, Polygon> wayMap;


  private PointBuilder pointBuilder;
  private PolygonBuilder polygonBuilder;

  public LocationProcessor() {

    pointBuilder = new PointBuilder();
    polygonBuilder = new PolygonBuilder();
    nodeMap = new HashMap<Long, Point>();
    wayMap = new HashMap<Long, Polygon>();
  }

  public void process(Entity entity) {

    // Find out what we're dealing with
    switch (entity.getType()) {
      case Node:
        process((Node) entity);
        break;
      case Way:
        process((Way) entity);
        break;
      case Relation:
        //Relation relation = (Relation) entity;
        //process(relation);
        break;
      default:
        break;
    }
  }

  public Geometry findLocation(Long id) {
    Geometry location = nodeMap.get(id);
    if (location == null) {
      location = wayMap.get(id);
    }

    return location;
  }

  private void process(Node node) {
    nodeMap.put(node.getId(), pointBuilder.createPoint(node.getLatitude(), node.getLongitude()));
  }

  private void process(Way way) {
    List<WayNode> wayNodes = way.getWayNodes();
    Point[] points = new Point[wayNodes.size()];

    for (int i = 0; i < points.length; i++) {
      points[i] = nodeMap.get(wayNodes.get(i).getNodeId());
    }
    wayMap.put(way.getId(), polygonBuilder.createPolygon(points));
  }

  private void process(Relation relation) {
    // TODO: This
    for (RelationMember relationMember : relation.getMembers()) {
    }
  }
}
