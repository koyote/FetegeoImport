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


  public List<GenericTag> processTags(Entity entity) {
    String key;

    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      if (key.equalsIgnoreCase("place")) {
        return processPlace(entity);
      }
      if (key.equalsIgnoreCase("highway")) {
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
    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      value = tag.getValue();
      if (key.equalsIgnoreCase("place")) {
        place.setType(tag.getValue());
        place.setId(entity.getId());
      } else if (key.endsWith("name") || key.equalsIgnoreCase("place_name")) {
        nameList.add(new Name(key, value));
      } else if (key.equalsIgnoreCase("population")) {
        place.setPopulation(Long.valueOf(value));
      }
      // TODO: place_numbers; postal_code, is_in
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
    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      value = tag.getValue();
      if (key.equalsIgnoreCase("highway")) {
        highway.setType(value);
        highway.setId(entity.getId());
      } else if (key.endsWith("name")) {
        nameList.add(new Name(key, value));
      } else if (key.equalsIgnoreCase("ref")) {
        highway.setRef(value);
      }
    }
    // TODO: postal_code, is_in

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
    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      value = tag.getValue();
      if (key.startsWith("addr")) {
        String addressType = key.split(":")[1];
        address.setId(entity.getId());
        if (addressType.equalsIgnoreCase("housenumber")) {
          address.setHousenumber(value);
        } else if (addressType.equalsIgnoreCase("postcode")) {
          address.setPostcode(value);
        } else if (addressType.equalsIgnoreCase("street")) {
          address.setStreet(value);
        }
        // TODO: city, country (lookup??)
      } else if (key.endsWith("name")) {
        nameList.add(new Name(key, value));
      }
    }
    address.setNameList(nameList);

    tags.add(address);

    return tags;
  }

}
