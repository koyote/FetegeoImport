package org.pit.fetegeo.importer;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 3:48 PM
 */
public class TagProcessor {


  public List<GenericTag> process(Entity entity) {
    String key;

    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      if (key.equalsIgnoreCase("place") || (tag.getKey().equalsIgnoreCase("boundary") && tag.getValue().equalsIgnoreCase("administrative"))) {
        return processPlace(entity);
      }
      if (key.equalsIgnoreCase("highway") || (tag.getKey().equalsIgnoreCase("route") && tag.getValue().equalsIgnoreCase("road"))) {
        return processHighway(entity);
      }
      if (key.startsWith("addr")) {
        return processAddress(entity);
      }
    }
    return null;
  }

  private List<GenericTag> processPlace(Entity entity) {
    List<GenericTag> tags = new ArrayList<GenericTag>();
    Place place = new Place();
    List<Name> nameList = new ArrayList<Name>();
    String key, value;

    place.setId(entity.getId());
    place.setOriginEntity(entity.getType());

    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      value = tag.getValue();
      if (key.equalsIgnoreCase("place")) {
        place.setType(tag.getValue());
      } else if (tag.getKey().equalsIgnoreCase("boundary") && tag.getValue().equalsIgnoreCase("administrative")) {
        place.setType("boundary");
      } else if (key.startsWith("name:") || key.endsWith("name") || key.equalsIgnoreCase("place_name")) {
        nameList.add(new Name(value, key));
      } else if (key.equalsIgnoreCase("population")) {
        place.setPopulation(Long.valueOf(value));
      }
    }
    place.setNameList(nameList);

    tags.add(place);

    return tags;
  }

  private List<GenericTag> processHighway(Entity entity) {
    List<GenericTag> tags = new ArrayList<GenericTag>();
    Highway highway = new Highway();
    List<Name> nameList = new ArrayList<Name>();
    String key, value;

    highway.setId(entity.getId());
    highway.setOriginEntity(entity.getType());

    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      value = tag.getValue();
      if (key.equalsIgnoreCase("highway")) {
        highway.setType(value);
      } else if (key.startsWith("name:") || key.endsWith("name")) {
        nameList.add(new Name(value, key));
      } else if (key.equalsIgnoreCase("ref")) {
        highway.setRef(value);
      }
    }

    // We don't want highways without names
    if (nameList.isEmpty() && highway.getRef() == null) {
      return null;
    }

    highway.setNameList(nameList);
    tags.add(highway);

    return tags;
  }

  private List<GenericTag> processAddress(Entity entity) {
    List<GenericTag> tags = new ArrayList<GenericTag>();
    Address address = new Address();
    List<Name> nameList = new ArrayList<Name>();
    String key, value;

    address.setId(entity.getId());
    address.setOriginEntity(entity.getType());

    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      value = tag.getValue();
      address.setType("addr");

      if (key.startsWith("addr")) {
        String addressType = key.split(":")[1];
        if (addressType.equalsIgnoreCase("housenumber")) {
          address.setHousenumber(value);
        } else if (addressType.equalsIgnoreCase("street")) {
          address.setStreet(value);
        }
      } else if (key.startsWith("name:") || key.endsWith("name")) {
        nameList.add(new Name(value, key));
      }
    }

    // TODO: city, country (lookup??), post_code

    address.setNameList(nameList);

    tags.add(address);

    return tags;
  }

}
