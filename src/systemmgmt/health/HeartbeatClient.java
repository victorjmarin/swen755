package systemmgmt.health;

import io.mappedbus.MappedBusWriter;

import java.io.EOFException;
import java.io.IOException;

public class HeartbeatClient implements IHeartbeatClient {

	MappedBusWriter _writer;
	int _pid;
	int _processName;

	public HeartbeatClient(String filename, int pid, int processName) {
		_writer = new MappedBusWriter(filename, 100000L, 32, true);
		_pid = pid;
		_processName = processName;
		try {
			_writer.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendHeartbeat() {
		try {
			_writer.write(new Heartbeat(_pid, System.currentTimeMillis(),
					_processName));
		} catch (EOFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
