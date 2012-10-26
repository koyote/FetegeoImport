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


  public Place processTags(Entity entity) {
    String key;
    for (Tag tag : entity.getTags()) {
      key = tag.getKey();
      if (key.equalsIgnoreCase("place")) {
        return processPlace(entity);
      }
      if (key.equalsIgnoreCase("highway")) {
        processHighway(entity);
        break;
      }
      if (key.equalsIgnoreCase("addr")) {
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

}
