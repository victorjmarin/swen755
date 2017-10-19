package systemmgmt;

import static systemmgmt.health.ProcessName.HB_RECEIVER;
import static systemmgmt.health.ProcessName.OBSTACLE_DETECTION_1;
import static systemmgmt.health.ProcessName.OBSTACLE_DETECTION_2;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import perception.ObstacleDetection;
import systemmgmt.health.HBReceiver;
import systemmgmt.health.ProcessName;

public class Boot {

  private static final String[] MODULES = {HB_RECEIVER, OBSTACLE_DETECTION_1, OBSTACLE_DETECTION_2};

  private final static ArrayList<Process> processes = new ArrayList<Process>();

  public static void main(final String[] args) throws IOException, InterruptedException {

    if (args.length == 1) {
      final String name = args[0];
      boot(name);
      return;
    }

    new File("bus/heartbeat").delete();

    for (final String m : MODULES) {
      spawnFor(m);
    }

    final Thread destroyProcesses = new Thread(() -> processes.forEach(Process::destroy));
    Runtime.getRuntime().addShutdownHook(destroyProcesses);
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
        new ObstacleDetection(ProcessName.OBSTACLE_DETECTION_1).run();
        break;
      case OBSTACLE_DETECTION_2:
        new ObstacleDetection(ProcessName.OBSTACLE_DETECTION_2).run();
        break;
      case HB_RECEIVER:
        final HBReceiver hbReceiver = new HBReceiver("bus/heartbeat");
        hbReceiver.register(OBSTACLE_DETECTION_1, "ObstacleDetection");
        hbReceiver.register(OBSTACLE_DETECTION_2, "ObstacleDetection");
        hbReceiver.enable(Executors.newSingleThreadScheduledExecutor());
    }
  }

  private static String classpath() {
    final String bootUri =
        Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    final String mappedBusUri =
        bootUri.substring(0, bootUri.length() - 5) + "/lib/mappedbus-0.5.jar";
    final String result = bootUri + ":" + mappedBusUri;
    return result;
  }

}
