package systemmgmt.health.message;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class Fault implements MappedBusMessage {

	public String processName;

	public Fault() {
	}

	public Fault(final String processName) {
		this.processName = processName;
	}

	@Override
	public void write(final MemoryMappedFile mem, final long pos) {
		int len = processName.getBytes().length;
		mem.putInt(pos, len);
		mem.setBytes(pos + 4, processName.getBytes(), 0, len);
	}

	@Override
	public void read(final MemoryMappedFile mem, final long pos) {
		int len = mem.getInt(pos);
		byte[] data = new byte[len];
		mem.getBytes(pos + 4, data, 0, len);
		processName = new String(data);
	}

	@Override
	public int type() {
		return 1;
	}

}
