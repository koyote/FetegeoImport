package org.pit.fetegeo.importer.processors;

import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.pgsimple.common.PointBuilder;
import org.openstreetmap.osmosis.pgsimple.common.PolygonBuilder;
import org.pit.fetegeo.importer.objects.GenericTag;
import org.postgis.*;

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
  private static Map<Long, Geometry> wayMap;
  private static Map<Long, Geometry> relationMap;

  private final PointBuilder pointBuilder;
  private final PolygonBuilder polygonBuilder;

  public LocationProcessor() {
    pointBuilder = new PointBuilder();
    polygonBuilder = new PolygonBuilder();
    nodeMap = new HashMap<Long, Point>();
    wayMap = new HashMap<Long, Geometry>();
    relationMap = new HashMap<Long, Geometry>();
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
    Point point = pointBuilder.createPoint(node.getLatitude(), node.getLongitude());
    nodeMap.put(node.getId(), point);
  }

  private void process(Way way) {
    List<WayNode> wayNodes = way.getWayNodes();
    Point[] points = new Point[wayNodes.size()];

    for (int i = 0; i < points.length; i++) {
      points[i] = nodeMap.get(wayNodes.get(i).getNodeId());
    }

    Geometry result;

    // If points make a circle, we have a polygon. otherwise we have a line
    if (points.length > 3 && points[0].equals(points[points.length - 1])) {
      result = polygonBuilder.createPolygon(points);
    } else {
      result = new LineString(points);
      result.setSrid(4326);
    }

    wayMap.put(way.getId(), result);
  }

  private void process(Relation relation) {
    List<Geometry> coordinateList = new ArrayList<Geometry>();
    fetchRelationCoors(relation, coordinateList);
    /*    int polygons = 0, lines = 0;

    for (Geometry g : coordinateList) {
      if (g instanceof Polygon) {
        polygons++;
      } else if (g instanceof LineString) {
        lines++;
      }
    }*/

    //Geometry result;

    GeometryCollection result = new GeometryCollection(coordinateList.toArray(new Geometry[coordinateList.size()]));

    // Find the most used way type and make it multi!
    /*    if (lines > polygons) {
      coordinateList = cleanList(coordinateList, Geometry.LINESTRING);
      LineString[] lineArray = coordinateList.toArray(new LineString[coordinateList.size()]);
      result = new MultiLineString(lineArray);
    } else {
      coordinateList = cleanList(coordinateList, Geometry.POLYGON);
      Polygon[] polyArray = coordinateList.toArray(new Polygon[coordinateList.size()]);
      result = new MultiPolygon(polyArray);
    }*/
    result.setSrid(4326);

    relationMap.put(relation.getId(), result);
  }

  private void fetchRelationCoors(Relation relation, List<Geometry> coordinateList) {
    Geometry geometry;
    for (RelationMember relationMember : relation.getMembers()) {

      // only care about outer roles (maybe inner too?)
      if (!relationMember.getMemberRole().equalsIgnoreCase("outer")) {
        //continue;
      }
      EntityType memberType = relationMember.getMemberType();
      switch (memberType) {
        case Node:
          // we don't care about Nodes as these are not used for roads or bounds (a part from designating the capital and other useless stuff)
          break;
        case Way:
          if ((geometry = wayMap.get(relationMember.getMemberId())) != null) {
            coordinateList.add(geometry);
          }
          break;
        case Relation:
          if (true) break;
          Geometry otherRelation = relationMap.get(relationMember.getMemberId());
          if (otherRelation != null) {
            coordinateList.add(otherRelation);
            // We need to differentiate between the two different types of relations
            /* if (otherRelation instanceof MultiPolygon) {
              MultiPolygon multiPolygon = (MultiPolygon) otherRelation;
              for (int i = 0; i < multiPolygon.numPolygons(); i++) {
                if ((geometry = multiPolygon.getPolygon(i)) != null) {
                  coordinateList.add(geometry);
                }
              }
            } else if (otherRelation instanceof MultiLineString) {
              MultiLineString multiLineString = (MultiLineString) otherRelation;
              for (int i = 0; i < multiLineString.numLines(); i++) {
                if ((geometry = multiLineString.getLine(i)) != null) {
                  coordinateList.add(geometry);
                }
              }
            }*/
          }
          break;
        default:
          break;
      }
    }
  }

  private List<Geometry> cleanList(List<Geometry> list, int type) {
    List<Geometry> cleanedList = new ArrayList<Geometry>();
    for (Geometry g : list) {
      if (g.getType() == type) {
        cleanedList.add(g);
      }
    }
    return cleanedList;
  }

  public void printSize() {
    System.out.println("Nodes: " + nodeMap.size() + ", Ways: " + wayMap.size() + ", Relations: " + relationMap.size());
  }
}
