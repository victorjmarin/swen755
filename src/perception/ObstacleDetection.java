package perception;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import perception.object.EnvObject;
import systemmgmt.health.HBSender;

public class ObstacleDetection implements IObstacleDetection {

  private final HBSender hbSender;

  public ObstacleDetection(final String sender) {
    hbSender = new HBSender("bus/heartbeat", sender, "ObstacleDetection");
    hbSender.enable(Executors.newSingleThreadScheduledExecutor());
  }

  public void run() {
    try {
      for (;;) {
        detectObstacles(new HashSet<>());
      }
    } catch (final Exception e) {
      e.printStackTrace();
      hbSender.cancel();
    }
  }

  @Override
  public Set<EnvObject> detectObstacles(final Set<EnvObject> objects) {
    final int rnd = ThreadLocalRandom.current().nextInt(0, 11);
    try {
      Thread.sleep(400);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
    final int i = 9 / rnd;
    return null;
  }

}
