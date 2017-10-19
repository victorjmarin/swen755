package controller;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import io.mappedbus.MappedBusReader;
import systemmgmt.health.HeartbeatSender;
import systemmgmt.health.message.SwapBrake;

public class Brake implements IBrake {

	public static final String PROCESS_NAME_ACTIVE = "BrakeActive";
	public static final String PROCESS_NAME_PASSIVE = "BrakePassive";
	private final HeartbeatSender _heartbeat;
	private String _processName;
	private MappedBusReader _reader;

	public Brake(final String heartbeatFilename, final String monitorFileName, final String processName,
			boolean status) {
		_heartbeat = new HeartbeatSender(heartbeatFilename, processName, status);
		_processName = processName;
		_reader = new MappedBusReader(monitorFileName, 100000L, 32);
		try {
			_reader.open();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		final SwapBrake swap = new SwapBrake();
		while (true) {
			// Send heartbeat - we are alive!
			_heartbeat.sendHeartbeat();
			System.out.println("[:processName] Sending heartbeat".replace(":processName", _processName));

			try {
				while (_reader.next()) {
					final int type = _reader.readType();
					if (type == 3) {
						_reader.readMessage(swap);
						_heartbeat.swapStatus();
					}
				}
			} catch (EOFException e1) {
				e1.printStackTrace();
			}

			// Send new values to the actuators
			applyBrake(System.currentTimeMillis());

			try {
				Thread.sleep(1000); // Pretend we are doing a long computation
			} catch (final InterruptedException e) {
			}
		}
	}

	public void applyBrake(long time) {
		final int noise = ThreadLocalRandom.current().nextInt(0, 11);
		double noiseRatio = time / noise;
		// System.out.println("Ratio to noise: " + Time / noise);
	}

	public void releaseBrake() {

	}

	public void jamBrake() {

	}
}
