package systemmgmt;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URLClassLoader;
import java.util.ArrayList;

import controller.Controller;
import decision.Decision;
import systemmgmt.health.HeartbeatReceiver;

public class Boot {

	public static final ArrayList<Process> processes = new ArrayList<Process>();

	/**
	 * 
	 * @param args
	 *            the processName to start or nothing to run main initialization
	 * @throws InterruptedException
	 */
	public static void main(final String[] args) throws InterruptedException {
		try {
			final URLClassLoader url = (URLClassLoader) Thread.currentThread()
					.getContextClassLoader();
			String jarPath = url.getURLs()[0].toString();
			final int fileGarbage = jarPath.indexOf('/');
			jarPath = jarPath.substring(fileGarbage, jarPath.length());

			// First, check if this was the normal boot
			if (args.length == 0) {
				// Start all the processes
				for (int i = 1; i < 4; ++i) {
					final ProcessBuilder pb = new ProcessBuilder("java", "-jar",
							jarPath, "" + i);
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

			// Otherwise, start the specific process
			final String heartbeatFilename = jarPath.substring(0,
					jarPath.length() - 7) + "heartbeat_communication";
			{
				final File file = new File(heartbeatFilename);
				if (file.exists()) {
					file.delete();
				}
			}

			final int processName = Integer.parseInt(args[0]);

			switch (processName) {
			case 1:
				new Controller(heartbeatFilename, 1).run();
				break;
			case 2:
				new HeartbeatReceiver(heartbeatFilename).run();
				break;
			case 3:
				new Decision(heartbeatFilename, 3).run();
				break;
			}

		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
