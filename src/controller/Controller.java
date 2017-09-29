package controller;

import java.util.concurrent.ThreadLocalRandom;

import systemmgmt.health.HeartbeatClient;

public class Controller {

	private static final int PROCESS_NAME = 1;
	private HeartbeatClient _heartbeat;

	public Controller(String heartbeatFilename, int pid) {
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
		System.out.println("Updating the motors to " + newMotorSetpoint);
		int noise = ThreadLocalRandom.current().nextInt(0, 11);
		System.out.println("Ratio to noise: " + newMotorSetpoint / noise);
	}
}
