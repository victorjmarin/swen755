package systemmgmt.health.message;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class Fault implements MappedBusMessage {

    public int processName;

    public Fault() {
    }

    public Fault(final int processName) {
	this.processName = processName;
    }

    @Override
    public void write(final MemoryMappedFile mem, final long pos) {
	mem.putInt(pos, processName);
    }

    @Override
    public void read(final MemoryMappedFile mem, final long pos) {
	processName = mem.getInt(pos);
    }

    @Override
    public int type() {
	return 1;
    }

}
