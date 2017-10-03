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
	    jarPath = jarPath.substring(fileGarbage, jarPath.length());

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
		for (int i = 1; i <= 4; ++i) {
		    final ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath, "" + i);
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

	    final int processName = Integer.parseInt(args[0]);

	    switch (processName) {
	    case 1:
		new Monitor(monitorFilename).run();
		break;
	    case 2:
		new HeartbeatReceiver(heartbeatFilename, monitorFilename).run();
		break;
	    case 3:
		new Brake(heartbeatFilename, 3).run();
		break;
	    case 4:
		new ObstacleDetection(heartbeatFilename, 4).run();
		break;
	    }

	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }
}
