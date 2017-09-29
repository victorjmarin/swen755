package decision;

import java.util.concurrent.ThreadLocalRandom;

import systemmgmt.health.HeartbeatClient;

public class Decision {

	private static final int PROCESS_NAME = 3;
	private HeartbeatClient _heartbeat;

	public Decision(String heartbeatFilename, int pid) {
		_heartbeat = new HeartbeatClient(heartbeatFilename, pid, PROCESS_NAME);
	}

	public void run() {
		while (true) {
			// Send heartbeat - we are alive!
			_heartbeat.sendHeartbeat();

			// Send new values to the actuators
			updateMotors(System.currentTimeMillis());

			try {
				Thread.sleep(500); // Pretend we are doing a long computation
			} catch (InterruptedException e) {
			}
		}
	}

	private void updateMotors(long newMotorSetpoint) {
		System.out.println("Updating our decision " + newMotorSetpoint);
		int noise = ThreadLocalRandom.current().nextInt(0, 20);
		System.out.println("Performing important division: " + newMotorSetpoint
				/ noise);
	}
}
