/**
 * Author: Pit Apps
 * Date: 10/22/12
 * Time: 2:26 PM
 */
package org.test;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.lifecycle.CompletableContainer;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.pgsimple.common.CopyFileWriter;
import org.openstreetmap.osmosis.pgsimple.common.PointBuilder;
import org.openstreetmap.osmosis.pgsimple.common.PolygonBuilder;
import org.postgis.Geometry;
import org.postgis.Point;
import org.postgis.Polygon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetegeoImportTask implements Sink {

  private PointBuilder pointBuilder;
  private PolygonBuilder polygonBuilder;

  private CompletableContainer container;
  private CopyFileWriter addressWriter;            // having separate address table makes searching faster
  private CopyFileWriter addressNameWriter;
  //private CopyFileWriter countryWriter;
  //private CopyFileWriter countryNameWriter;
  private CopyFileWriter langWriter;
  private CopyFileWriter placeWriter;
  private CopyFileWriter placeNameWriter;
  private CopyFileWriter postcodeWriter;

  private Map<Long, Point> nodeMap;
  private Map<Long, Polygon> wayMap;
  private Map<Long, String> nameMap;

  private Long placeId = 0l;
  private Long placeNameId = 0l;
  private Long addressId = 0l;
  private Long addressNameId = 0l;
  private Long postcodeId = 0l;


  public FetegeoImportTask(final File outdir) {
    System.out.println("IMPORTING A task");

    String outPath = outdir.getAbsolutePath();

    System.out.println("The Output dir is: " + outPath);

    pointBuilder = new PointBuilder();
    polygonBuilder = new PolygonBuilder();

    container = new CompletableContainer();

    addressWriter = container.add(new CopyFileWriter(new File(outPath, "address.txt")));
    addressNameWriter = container.add(new CopyFileWriter(new File(outPath, "address_name.txt")));
    //countryWriter = container.add(new CopyFileWriter(new File(outPath, "country.txt")));
    //countryNameWriter = container.add(new CopyFileWriter(new File(outPath, "country_name.txt")));
    langWriter = container.add(new CopyFileWriter(new File(outPath, "lang.txt")));
    placeWriter = container.add(new CopyFileWriter(new File(outPath, "place.txt")));
    placeNameWriter = container.add(new CopyFileWriter(new File(outPath, "place_name.txt")));
    postcodeWriter = container.add(new CopyFileWriter(new File(outPath, "postcode.txt")));

    nodeMap = new HashMap<Long, Point>();
    wayMap = new HashMap<Long, Polygon>();
    nameMap = new HashMap<Long, String>();

  }


  @Override
  public void process(EntityContainer entityContainer) {
    Entity entity = entityContainer.getEntity();

    // Aquire locations
    switch (entity.getType()) {
      case Node:
        Node node = (Node) entity;
        processNode(node);
        break;
      case Way:
        Way way = (Way) entity;
        processWay(way);
        break;
      case Relation:
        //Relation relation = (Relation) entity;
        //processRelation(relation);
        break;
      default:
        break;
    }

    // Process tags
    Place place = processTags(entity);

    // Write to place file
    writePlace(place);

  }

  private void writePlace(Place place) {
    if (place == null) return;
    placeWriter.writeField(placeId);
    placeWriter.writeField(place.getId());
    placeWriter.writeField(place.getPlaceType());
    placeWriter.writeField(place.getPopulation());
    placeWriter.writeField(findLocation(place.getId()));

    for (Name name : place.getNameList()) {
      placeNameWriter.writeField(placeNameId++);
      placeNameWriter.writeField(placeId);
      placeNameWriter.writeField(name.getNameType());
      placeNameWriter.writeField(name.getName());
      placeNameWriter.endRecord();
    }
    placeId++;
    placeWriter.endRecord();
  }

  private Geometry findLocation(Long id) {
    Geometry location = nodeMap.get(id);
    if (location == null) {
      location = wayMap.get(id);
    }

    return location;
  }

  private void processNode(Node node) {
    nodeMap.put(node.getId(), pointBuilder.createPoint(node.getLatitude(), node.getLongitude()));
  }

  private void processWay(Way way) {
    List<WayNode> wayNodes = way.getWayNodes();
    Point[] points = new Point[wayNodes.size()];

    for (int i = 0; i < points.length; i++) {
      points[i] = nodeMap.get(wayNodes.get(i).getNodeId());
    }
    wayMap.put(way.getId(), polygonBuilder.createPolygon(points));

  }

  // TODO: This
  private void processRelation(Relation relation) {
    for (RelationMember relationMember : relation.getMembers()) {
    }


  }

  private Place processTags(Entity entity) {
    String key;
    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      if (key.equalsIgnoreCase("place")) {
        return processPlace(entity);
      } else if (key.equalsIgnoreCase("highway")) {
        processHighway(entity);
        break;
      } else if (key.equalsIgnoreCase("addr")) {
        processAddress(entity);
        break;
      }
    }
    return null;
  }

  private Place processPlace(Entity entity) {
    Place place = new Place();
    List<Name> nameList = new ArrayList<Name>();
    String key, value;
    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      value = tag.getValue();
      if (key.equalsIgnoreCase("place")) {
        place.setPlaceType(tag.getValue());
        place.setId(entity.getId());
      } else if (key.endsWith("name") || key.equalsIgnoreCase("place_name")) {
        nameList.add(new Name(key, value));
      } else if (key.equalsIgnoreCase("population")) {
        place.setPopulation(Long.valueOf(value));
      }
      // TODO: place_numbers; postal_code
    }
    place.setNameList(nameList);
    return place;
  }

  private void processHighway(Entity entity) {

  }

  private void processAddress(Entity entity) {

  }

  private void printRelationInfo(Relation relation) {
    System.out.println(relation.toString());

    // Print RelationMembers. What exactly is this?
    for (RelationMember relationMember : relation.getMembers()) {
      //System.out.println(relationMember.toString());
    }

    // Print Tags
    for (Tag tag : relation.getTags()) {
      System.out.println(tag.toString());
    }
  }

  private void printWayInfo(Way way) {

    // Print ID & Tag
    System.out.println(way.toString());
    for (Tag tag : way.getTags()) {
      System.out.println(tag.toString());
    }

    /*
    Tags that seem useful:
      name
      name:* (for translation)
      old_name
      postal_code
      is_in
      is_in:city
      ref (N 31)
      int_ref (E 44)
      country
      place
      population
     */

    // Don't know what this is; always empty
    Map<String, Object> m = way.getMetaTags();
    if (!m.isEmpty()) { /* Always empty for Luxembourg */
      for (String key : m.keySet()) {
        System.out.println(key);
      }
    }

    // Nodes related to this way?
    for (WayNode wayNode : way.getWayNodes()) {
      //System.out.println(wayNode.toString());
    }
  }

  @Override
  public void initialize(Map<String, Object> stringObjectMap) {
  }

  @Override
  public void complete() {
    container.complete();
  }

  @Override
  public void release() {
    container.release();
  }
}
