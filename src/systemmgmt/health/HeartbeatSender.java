package systemmgmt.health;

import java.io.EOFException;
import java.io.IOException;

import io.mappedbus.MappedBusWriter;

public class HeartbeatSender implements IHeartbeatSender {

    private int sendingInterval;

    MappedBusWriter _writer;
    int _pid;
    int _processName;

    public HeartbeatSender(final String filename, final int pid, final int processName) {
	_writer = new MappedBusWriter(filename, 100000L, 32, true);
	_pid = pid;
	_processName = processName;
	try {
	    _writer.open();
	} catch (final IOException e) {
	    e.printStackTrace();
	}
//	final ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
//	sch.scheduleAtFixedRate(new Runnable() {
//	    @Override
//	    public void run() {
//		sendHeartbeat();
//	    }
//	}, 0, sendingInterval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendHeartbeat() {
	try {
	    _writer.write(new Heartbeat(_pid, System.currentTimeMillis(), _processName));
	} catch (final EOFException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public int getName() {
	return _processName;
    }
}
