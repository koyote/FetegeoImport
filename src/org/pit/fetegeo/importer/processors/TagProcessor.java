package org.pit.fetegeo.importer.processors;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.pit.fetegeo.importer.objects.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Pit Apps
 * Date: 10/26/12
 * Time: 3:48 PM
 */
public class TagProcessor {

  private List<GenericTag> tags;

  public List<GenericTag> process(Entity entity) {
    String key;
    tags = new ArrayList<GenericTag>();

    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      if (key.equalsIgnoreCase("place") || (key.equalsIgnoreCase("boundary") && tag.getValue().equalsIgnoreCase("administrative"))) {
        processPlace(entity);
        break;
      } else if (key.equalsIgnoreCase("highway") || (key.equalsIgnoreCase("route") && tag.getValue().equalsIgnoreCase("road"))) {
        processHighway(entity);
        break;
      } else if (key.startsWith("addr")) {
        processAddress(entity);
        break;
      }
    }
    return tags;
  }

  private void processPlace(Entity entity) {
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
      } else if (key.equalsIgnoreCase("postal_code")) {
        processPostalCode(entity, tag, place);
      }
    }
    place.setNameList(nameList);

    tags.add(place);
  }

  private void processHighway(Entity entity) {
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
      } else if (key.equalsIgnoreCase("postal_code")) {
        processPostalCode(entity, tag, highway);
      }
    }

    // We don't want highways without names
    if (nameList.isEmpty() && highway.getRef() == null) {
      return;
    }

    highway.setNameList(nameList);
    tags.add(highway);
  }

  private void processAddress(Entity entity) {
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
      if (key.equalsIgnoreCase("postal_code") || key.equalsIgnoreCase("addr:postcode")) {
        processPostalCode(entity, tag, address);
      }
    }

    address.setNameList(nameList);

    tags.add(address);
  }

  // We allow for duplicate postcodes so that we can then store all locations for a given post_code (and combine the area using PostGIS)
  private void processPostalCode(Entity entity, Tag tag, GenericTag genericTag) {
    String value = tag.getValue();
    String splitter = ";";
    if (value.contains(",")) splitter = ",";
    String[] values = value.split(splitter); // sometimes we have more than one postcode separated by a comma or semi-colon

    for (String code : values) {
      PostalCode postalCode = new PostalCode(code);
      postalCode.setId(entity.getId());
      postalCode.setType(entity.getType().name());
      postalCode.setOriginEntity(entity.getType());
      tags.add(postalCode);
      genericTag.setPostCodeId(postalCode.getPostCodeId());
    }
  }

}
