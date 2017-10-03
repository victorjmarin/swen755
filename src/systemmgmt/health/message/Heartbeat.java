package systemmgmt.health.message;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class Heartbeat implements MappedBusMessage {

	public int processId;
	public long timestamp;
	public int processName;

	public Heartbeat(int processId, long timestamp, int processName) {
		this.processId = processId;
		this.timestamp = timestamp;
		this.processName = processName;
	}

	public Heartbeat() {
	}

	@Override
	public void write(MemoryMappedFile mem, long pos) {
		mem.putInt(pos, processId);
		mem.putInt(pos + 4, processName);
		mem.putLong(pos + 8, timestamp);
	}

	@Override
	public void read(MemoryMappedFile mem, long pos) {
		processId = mem.getInt(pos);
		processName = mem.getInt(pos + 4);
		timestamp = mem.getLong(pos + 8);
	}

	@Override
	public int type() {
		return 0;
	}
}
