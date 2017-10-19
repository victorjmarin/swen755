package systemmgmt;

import static systemmgmt.health.ProcessName.HB_RECEIVER;
import static systemmgmt.health.ProcessName.OBJECT_RECOGNITION_1;
import static systemmgmt.health.ProcessName.OBJECT_RECOGNITION_2;
import static systemmgmt.health.ProcessName.OBSTACLE_DETECTION_1;
import static systemmgmt.health.ProcessName.OBSTACLE_DETECTION_2;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import perception.ObjectRecognition;
import perception.ObstacleDetection;
import systemmgmt.health.HBReceiver;
import systemmgmt.health.ProcessName;

public class Boot {

  // Pointing to /dev/shm/heartbeat will use a RAM-based bus (Only GNU/Linux)
  public static final String HEARTBEAT_BUS = "bus/heartbeat";

  private static final String[] MODULES = {HB_RECEIVER, OBSTACLE_DETECTION_1, OBSTACLE_DETECTION_2,
      OBJECT_RECOGNITION_1, OBJECT_RECOGNITION_2};

  private final static ArrayList<Process> processes = new ArrayList<Process>();

  public static void main(final String[] args) throws IOException, InterruptedException {

    if (args.length == 1) {
      final String name = args[0];
      boot(name);
      return;
    }

    new File(HEARTBEAT_BUS).delete();

    for (final String m : MODULES) {
      spawnFor(m);
    }

    final Thread destroyProcesses = new Thread(() -> processes.forEach(Process::destroy));


    Runtime.getRuntime().addShutdownHook(destroyProcesses);

    // Keep boot process alive so that the other processes don't die because of the shutdown hook.
    while (true);
  }

  public static void spawnFor(final String name) {
    try {
      final ProcessBuilder pb = new ProcessBuilder("java", "-Dname=" + name, "-cp", classpath(),
          Boot.class.getName(), name);
      pb.inheritIO();
      Process p;
      p = pb.start();
      processes.add(p);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static void boot(final String name) {
    switch (name) {
      case OBSTACLE_DETECTION_1:
        new ObstacleDetection(HEARTBEAT_BUS, ProcessName.OBSTACLE_DETECTION_1).run();
        break;
      case OBSTACLE_DETECTION_2:
        new ObstacleDetection(HEARTBEAT_BUS, ProcessName.OBSTACLE_DETECTION_2).run();
        break;
      case OBJECT_RECOGNITION_1:
        new ObjectRecognition(HEARTBEAT_BUS, ProcessName.OBJECT_RECOGNITION_1).run();
        break;
      case OBJECT_RECOGNITION_2:
        new ObjectRecognition(HEARTBEAT_BUS, ProcessName.OBJECT_RECOGNITION_2).run();
        break;
      case HB_RECEIVER:
        final HBReceiver hbReceiver = new HBReceiver(HEARTBEAT_BUS);
        hbReceiver.register(OBSTACLE_DETECTION_1, ObstacleDetection.GROUP);
        hbReceiver.register(OBSTACLE_DETECTION_2, ObstacleDetection.GROUP);
        hbReceiver.register(OBJECT_RECOGNITION_1, ObjectRecognition.GROUP);
        hbReceiver.register(OBJECT_RECOGNITION_2, ObjectRecognition.GROUP);
        hbReceiver.enable(Executors.newSingleThreadScheduledExecutor());
    }
  }

  private static String classpath() {
    final String bootUri = Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    final String mappedBusUri =
        bootUri.substring(0, bootUri.length() - 5) + "/lib/mappedbus-0.5.jar";
    final String result = bootUri + ":" + mappedBusUri;
    return result;
  }

  private static String windowsClasspath() {
    final String bootUri = Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    final String mappedBusUri =
        bootUri.substring(1, bootUri.length() - 5) + "/lib/mappedbus-0.5.jar";
    String result = bootUri.substring(1, bootUri.length()) + ";" + mappedBusUri;
    result = result.replace('/', '\\');
    return result;
  }
}
