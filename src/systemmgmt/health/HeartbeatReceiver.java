package systemmgmt.health;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;
import systemmgmt.health.message.Fault;
import systemmgmt.health.message.Heartbeat;

public class HeartbeatReceiver {

	public static final String PROCESS_NAME = "HBReceiver";
	static final int MAX_HEARTBEAT_DELAY = 2000;

	MappedBusReader _reader;
	MappedBusWriter _writer;

	/**
	 * Map processName to last known timestamp
	 */
	HashMap<String, Long> _previousTimestamps;

	public HeartbeatReceiver(final String heartbeatFile, final String monitorFile) {
		_reader = new MappedBusReader(heartbeatFile, 100000L, 32);
		_writer = new MappedBusWriter(monitorFile, 100000L, 32, true);
		_previousTimestamps = new HashMap<String, Long>();
		try {
			_reader.open();
			_writer.open();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		final Heartbeat heartbeat = new Heartbeat();
		while (true) {
			// Delay so we only check once every second
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			// Read any new heartbeats from the shared file
			try {
				while (_reader.next()) {
					final int type = _reader.readType();
					if (type == 0) {
						
					_reader.readMessage(heartbeat);
					if (heartbeat.active) {
						
						System.out.println("[HBReceiver] (:time) Read heartbeat from :pn"
								.replace(":pn", heartbeat.processName).replace(":time", "" + heartbeat.timestamp));
						_previousTimestamps.put(heartbeat.processName, heartbeat.timestamp);
						}
							
					}
				}
			} catch (final EOFException e) {
				e.printStackTrace();
			}

			// Check to see if anything has died
			final long currentTime = System.currentTimeMillis();
			for (final String processName : _previousTimestamps.keySet()) {
				if (currentTime - _previousTimestamps.get(processName) > MAX_HEARTBEAT_DELAY) {
					try {
						_writer.write(new Fault(processName));
					} catch (final EOFException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
