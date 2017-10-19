package systemmgmt.health;

import java.io.EOFException;
import java.io.IOException;
import io.mappedbus.MappedBusWriter;
import systemmgmt.health.message.Heartbeat;

public class HeartbeatSender implements IHeartbeatSender {

	MappedBusWriter _writer;
	String _processName;
	boolean _active;

	public HeartbeatSender(final String filename, final String processName, boolean active) {
		_writer = new MappedBusWriter(filename, 100000L, 32, true);
		_processName = processName;
		_active = active;
		try {
			_writer.open();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendHeartbeat() {
		try {
			_writer.write(new Heartbeat(System.currentTimeMillis(), _processName, _active));
		} catch (final EOFException e) {
			e.printStackTrace();
		}
	}

	public void swapStatus() {
		_active = !_active;
	}

}
