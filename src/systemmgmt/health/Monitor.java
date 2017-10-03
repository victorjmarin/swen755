package systemmgmt.health;

import java.io.EOFException;
import java.io.IOException;

import io.mappedbus.MappedBusReader;
import systemmgmt.health.message.Fault;

public class Monitor {

    MappedBusReader _reader;

    public Monitor(final String filename) {
	_reader = new MappedBusReader(filename, 100000L, 32);
	try {
	    _reader.open();
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    public void run() {
	final Fault fault = new Fault();
	while (true) {
	    try {
		while (_reader.next()) {
		    final int type = _reader.readType();
		    if (type == 1) {
			_reader.readMessage(fault);
			System.err.println("WARNING: Process with name " + fault.processName + " is not responding");
		    }
		}
	    } catch (final EOFException e) {
		e.printStackTrace();
	    }
	}
    }

}
