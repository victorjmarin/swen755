package systemmgmt;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URLClassLoader;
import java.util.ArrayList;

import controller.Brake;
import perception.ObstacleDetection;
import systemmgmt.health.HeartbeatReceiver;
import systemmgmt.health.Monitor;

public class Boot {

	public static final ArrayList<Process> processes = new ArrayList<Process>();
	private static final String[] BOOT = { Monitor.PROCESS_NAME, HeartbeatReceiver.PROCESS_NAME,
			Brake.PROCESS_NAME_ACTIVE, Brake.PROCESS_NAME_PASSIVE, ObstacleDetection.PROCESS_NAME_ACTIVE,
			ObstacleDetection.PROCESS_NAME_PASSIVE };

	/**
	 * @param args
	 *            the processName to start or nothing to run main initialization
	 * @throws InterruptedException
	 */
	public static void main(final String[] args) throws InterruptedException {
		try {
			final URLClassLoader url = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			String jarPath = url.getURLs()[0].toString();
			final int fileGarbage = jarPath.indexOf('/');
			jarPath = jarPath.substring(fileGarbage + 1, jarPath.length());

			// Otherwise, start the specific process
			final String heartbeatFilename = jarPath.substring(0, jarPath.length() - 7) + "heartbeat_communication";
			final String monitorFilename = jarPath.substring(0, jarPath.length() - 7) + "monitor_communication";

			// First, check if this was the normal boot
			if (args.length == 0) {

				// Remove old files
				{
					final File file = new File(heartbeatFilename);
					if (file.exists()) {
						file.delete();
					}
					final File file2 = new File(monitorFilename);
					if (file2.exists()) {
						file2.delete();
					}
				}

				// Start all the processes
				for (String s : BOOT) {
					final ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath, "" + s);
					pb.redirectOutput(Redirect.INHERIT);
					pb.redirectError(Redirect.INHERIT);
					processes.add(pb.start());
				}
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						for (final Process p : processes) {
							p.destroy();
						}
					}
				});
				while (true)
					;
			}

			final String processName = args[0];

			switch (processName) {
			case Monitor.PROCESS_NAME:
				new Monitor(monitorFilename).run();
				break;
			case HeartbeatReceiver.PROCESS_NAME:
				new HeartbeatReceiver(heartbeatFilename, monitorFilename).run();
				break;
			case Brake.PROCESS_NAME_ACTIVE:
				new Brake(heartbeatFilename, monitorFilename, Brake.PROCESS_NAME_ACTIVE, true).run();
				break;
			case Brake.PROCESS_NAME_PASSIVE:
				new Brake(heartbeatFilename, monitorFilename, Brake.PROCESS_NAME_PASSIVE, false).run();
				break;
			case ObstacleDetection.PROCESS_NAME_ACTIVE:
				// new ObstacleDetection(heartbeatFilename,
				// ObstacleDetection.PROCESS_NAME_ACTIVE, true).run();
				// new ObstacleDetection(heartbeatFilename,
				// ObstacleDetection.PROCESS_NAME_PASSIVE, false).run();
				break;
			default:

			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
