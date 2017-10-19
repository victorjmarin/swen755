package perception;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import perception.object.EnvObject;
import systemmgmt.health.HeartbeatSender;

public class ObstacleDetection implements IObstacleDetection {

    public static final String PROCESS_NAME_ACTIVE = "ObstacleDetectionActive";
    public static final String PROCESS_NAME_PASSIVE = "ObstacleDetectionPassive";
    private final HeartbeatSender _heartbeat;

    public ObstacleDetection(final String heartbeatFilename, String processName, boolean active) {
	_heartbeat = new HeartbeatSender(heartbeatFilename, processName, active);
    }

    public void run() {
	while (true) {
	    // Send heartbeat - we are alive!
	    _heartbeat.sendHeartbeat();

	    // Send new values to the actuators
	    detectObstacles(new HashSet<EnvObject>());

	    try {
		Thread.sleep(1000); // Pretend we are doing a long computation
	    } catch (final InterruptedException e) {
	    }
	}
    }

    @Override
    public Set<EnvObject> detectObstacles(final Set<EnvObject> objects) {
	final long currTime = System.currentTimeMillis();
	System.out.println("Detecting obstacles " + currTime);
	final int noise = ThreadLocalRandom.current().nextInt(0, 20);
	System.out.println("Performing important division: " + currTime / noise);
	return null;
    }

    @Override
    public double getCollisionForce() {
	return 0;
    }

}
