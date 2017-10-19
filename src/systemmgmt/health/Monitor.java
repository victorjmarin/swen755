package systemmgmt.health;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URLClassLoader;

import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;
import systemmgmt.health.message.Fault;
import systemmgmt.health.message.SwapBrake;

public class Monitor {

	public static final String PROCESS_NAME = "Monitor";

	MappedBusReader _reader;
	MappedBusWriter _writer;

	public Monitor(final String filename) {
		_reader = new MappedBusReader(filename, 100000L, 32);
		_writer = new MappedBusWriter(filename, 100000L, 32, true);

		try {
			_reader.open();
			_writer.open();
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
						System.err.println("[Monitor] WARNING: " + fault.processName + " is not responding");
						System.out.println("[Monitor] Switching to " + getSwitchProcess(fault.processName));
						_writer.write(new SwapBrake());
						final ProcessBuilder pb = new ProcessBuilder("java", "-jar", getJarPath(),
								"" + fault.processName);
						pb.redirectOutput(Redirect.INHERIT);
						pb.redirectError(Redirect.INHERIT);
						// pb.start();
						// System.out.println("[Monitor] Rebooting " +
						// fault.processName + " process");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getSwitchProcess(String processName) {
		int activeEndIndex = processName.indexOf("Active");
		if (activeEndIndex != -1)
			return processName.substring(0, activeEndIndex) + "Passive";

		return processName.replace("Passive", "Active");

	}

	private String getJarPath() {
		final URLClassLoader url = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		String jarPath = url.getURLs()[0].toString();
		final int fileGarbage = jarPath.indexOf('/');
		jarPath = jarPath.substring(fileGarbage + 1, jarPath.length());
		return jarPath;
	}

}
