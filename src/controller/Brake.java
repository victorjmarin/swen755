package controller;

import java.util.concurrent.ThreadLocalRandom;

import systemmgmt.health.HeartbeatSender;

public class Brake implements IBrake {

	private static final int PROCESS_NAME = 1;
	private final HeartbeatSender _heartbeat;

	public Brake(final String heartbeatFilename, final int pid) {
		_heartbeat = new HeartbeatSender(heartbeatFilename, pid, PROCESS_NAME);
	}

	public void run() {
		while (true) {
			// Send heartbeat - we are alive!
			_heartbeat.sendHeartbeat();

			// Send new values to the actuators
			applyBrake(System.currentTimeMillis());

			try {
				Thread.sleep(500); // Pretend we are doing a long computation
			} catch (final InterruptedException e) {
			}
		}
	}

	public void applyBrake(long Time) {
		System.out.println("Brakes were applied at " + Time);
		final int noise = ThreadLocalRandom.current().nextInt(0, 11);
		System.out.println("Ratio to noise: " + Time / noise);
	}
	
	public void releaseBrake()
	{
		
	}
	
	public void jamBrake()
	{
	
	}
}
