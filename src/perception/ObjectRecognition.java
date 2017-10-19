package perception;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import perception.object.EnvObject;
import systemmgmt.health.HBSender;

public class ObjectRecognition implements IObjectRecognition {

	 public static final String GROUP = "ObjectRecognition";
	  private final HBSender hbSender;
	

	  public ObjectRecognition(final String heartbeatBus, final String sender) {
	    hbSender = new HBSender(heartbeatBus, sender, GROUP);
	    hbSender.enable(Executors.newSingleThreadScheduledExecutor());
	  }

	  public void run() {
	    try {
	      for (;;) {
	    	  recognizeObject(new HashSet<>());
	      }
	    } catch (final Exception e) {
	      e.printStackTrace();
	      hbSender.cancel();
	    }
	  }

  @Override
  public Set<EnvObject> recognizeObject(final Set<EnvObject> objects) {
	    final int rnd = ThreadLocalRandom.current().nextInt(0, 21);
	    try {
	      Thread.sleep(500);
	    } catch (final InterruptedException e) {
	      e.printStackTrace();
	    }
	    final int i = 9 / rnd;
	    return null;
	  }

}
