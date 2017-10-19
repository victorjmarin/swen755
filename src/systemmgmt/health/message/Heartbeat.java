package systemmgmt.health.message;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class Heartbeat implements MappedBusMessage {

	public long timestamp;
	public String processName;
	public boolean active;

	public Heartbeat(long timestamp, String processName, boolean active) {
		this.timestamp = timestamp;
		this.processName = processName;
		this.active = active;
	}

	public Heartbeat() {
	}

	@Override
	public void write(MemoryMappedFile mem, long pos) {
		int processNameLen = processName.getBytes().length;
		mem.putInt(pos, processNameLen);
		mem.setBytes(pos + 4, processName.getBytes(), 0, processNameLen);
		mem.putLong(pos + 4 + processNameLen, timestamp);
		mem.putByte(pos + 12 + processNameLen, (byte) (active ? 1 : 0));
	}

	@Override
	public void read(MemoryMappedFile mem, long pos) {
		int len = mem.getInt(pos);
		byte[] data = new byte[len];
		mem.getBytes(pos + 4, data, 0, len);
		processName = new String(data);
		timestamp = mem.getLong(pos + 4 + len);
		active = mem.getByte(pos + 12 + len) == 1 ? true : false;
	}

	@Override
	public int type() {
		return 0;
	}
}
