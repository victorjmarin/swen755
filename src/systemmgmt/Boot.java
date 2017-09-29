package systemmgmt;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URLClassLoader;
import java.util.ArrayList;

import systemmgmt.health.HeartbeatReceiver;
import controller.Controller;
import decision.Decision;

public class Boot {

	public static final ArrayList<Process> processes = new ArrayList<Process>();

	/**
	 * 
	 * @param args
	 *            the processName to start or nothing to run main initialization
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		try {
			URLClassLoader url = (URLClassLoader) Thread.currentThread()
					.getContextClassLoader();
			String jarPath = url.getURLs()[0].toString();
			int fileGarbage = jarPath.indexOf('/');
			jarPath = jarPath.substring(fileGarbage + 1, jarPath.length());

			// First, check if this was the normal boot
			if (args.length == 0) {
				// Start all the processes
				for (int i = 1; i < 4; ++i) {
					ProcessBuilder pb = new ProcessBuilder("java", "-jar",
							jarPath, "" + i);
					pb.redirectOutput(Redirect.INHERIT);
					pb.redirectError(Redirect.INHERIT);
					processes.add(pb.start());
				}
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						for (Process p : processes) {
							p.destroy();
						}
					}
				});
				while (true)
					;
			}

			// Otherwise, start the specific process
			String heartbeatFilename = jarPath.substring(0,
					jarPath.length() - 7) + "heartbeat_communication";
			{
				File file = new File(heartbeatFilename);
				if (file.exists()) {
					file.delete();
				}
			}

			int processName = Integer.parseInt(args[0]);

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

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
