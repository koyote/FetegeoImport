package org.pit.fetegeo.importer;

import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.pgsimple.common.PointBuilder;
import org.openstreetmap.osmosis.pgsimple.common.PolygonBuilder;
import org.postgis.Geometry;
import org.postgis.MultiPolygon;
import org.postgis.Point;
import org.postgis.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 3:43 PM
 */
public class LocationProcessor {

  private static Map<Long, Point> nodeMap;
  private static Map<Long, Polygon> wayMap;
  private static Map<Long, MultiPolygon> relationMap;

  private PointBuilder pointBuilder;
  private PolygonBuilder polygonBuilder;

  public LocationProcessor() {
    pointBuilder = new PointBuilder();
    polygonBuilder = new PolygonBuilder();
    nodeMap = new HashMap<Long, Point>();
    wayMap = new HashMap<Long, Polygon>();
    relationMap = new HashMap<Long, MultiPolygon>();
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
        process((Relation) entity);
        break;
      default:
        break;
    }
  }

  public static Geometry findLocation(GenericTag tag) {
    switch (tag.getOriginEntity()) {
      case Node:
        return nodeMap.get(tag.getId());
      case Way:
        return wayMap.get(tag.getId());
      case Relation:
        return relationMap.get(tag.getId());
      default:
        return null;
    }
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
    List<Polygon> coordinateList = new ArrayList<Polygon>();
    fetchRelationCoors(relation, coordinateList);
    Polygon[] polyArray = coordinateList.toArray(new Polygon[coordinateList.size()]);
    MultiPolygon relationPolygons = new MultiPolygon(polyArray);

    relationMap.put(relation.getId(), relationPolygons);
  }

  private void fetchRelationCoors(Relation relation, List<Polygon> coordinateList) {
    Polygon poly;
    for (RelationMember relationMember : relation.getMembers()) {
      EntityType memberType = relationMember.getMemberType();
      switch (memberType) {
        case Node:
          // we don't care about Nodes as these are not used for roads or bounds (a part from designating the capital and other useless stuff)
          break;
        case Way:
          if ((poly = wayMap.get(relationMember.getMemberId())) != null) {
            coordinateList.add(poly);
          }
          break;
        case Relation:
          MultiPolygon multiPoly = relationMap.get(relationMember.getMemberId());
          if (multiPoly != null) {
            for (int i = 0; i < multiPoly.numPolygons(); i++) {
              if ((poly = multiPoly.getPolygon(i)) != null) {
                coordinateList.add(poly);
              }
            }
          }
        default:
          break;
      }
    }
  }

  public void printSize() {
    System.out.println("Nodes: " + nodeMap.size() + ", Ways: " + wayMap.size() + ", Relations: " + relationMap.size());
  }
}
