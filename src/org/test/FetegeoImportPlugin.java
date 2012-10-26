/**
 * Author: Pit Apps
 * Date: 10/22/12
 * Time: 2:26 PM
 */

package org.test;

import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.plugin.PluginLoader;

import java.util.HashMap;
import java.util.Map;


public class FetegeoImportPlugin implements PluginLoader {

  @Override
  public Map<String, TaskManagerFactory> loadTaskFactories() {

    HashMap<String, TaskManagerFactory> map = new HashMap<String, TaskManagerFactory>();
    map.put("fetegeo-import", new FetegeoImportFactory());
    map.put("fimp", new FetegeoImportFactory());

    return map;
  }
}
