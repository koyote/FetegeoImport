/**
 * Author: Pit Apps
 * Date: 10/22/12
 * Time: 2:26 PM
 */
package org.pit.fetegeo.importer;

import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;

import java.io.File;

public class FetegeoImportFactory extends TaskManagerFactory {

  private static final String ARG_NAME = "outdir";
  private static final String DEFAULT_DIR = System.getProperty("user.dir"); // assume current directory to be the default

  @Override
  protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {

    // Get args
    String outDirName = getStringArgument(taskConfig, ARG_NAME, getDefaultStringArgument(taskConfig, DEFAULT_DIR));
    File outDir = new File(outDirName);

    if (!outDir.isDirectory()) {
      throw new IllegalArgumentException(outDirName + " is not a directory or does not exist.");
    }

    FetegeoImportTask task = new FetegeoImportTask(outDir);

    return new SinkManager(taskConfig.getId(), task, taskConfig.getPipeArgs());
  }
}
