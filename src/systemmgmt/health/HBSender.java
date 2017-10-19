package systemmgmt.health;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import io.mappedbus.MappedBusWriter;
import systemmgmt.health.message.Heartbeat;

public class HBSender {

  private MappedBusWriter busWriter;
  private String sender;
  private String group;

  public static final long INTERVAL = 1000;
  public static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
  private TimeUnit timeUnit;
  private ScheduledExecutorService executor;

  private ScheduledFuture<?> senderFuture;

  public HBSender(final String heartbeatBus, final String sender, final String group) {
    this(heartbeatBus, TIME_UNIT, sender, group);
  }

  public HBSender(final String heartbeatBus, final TimeUnit timeUnit, final String sender,
      final String group) {
    try {
      busWriter = new MappedBusWriter(heartbeatBus, 100000L, 32, true);
      busWriter.open();
      this.timeUnit = timeUnit;
      this.sender = sender;
      this.group = group;
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public synchronized void enable(final ScheduledExecutorService executor) {
    if (executor != null) {
      this.executor = executor;
      senderFuture = executor.scheduleAtFixedRate(() -> sendHeartbeat(), (INTERVAL + 9L) / 10L,
          INTERVAL, timeUnit);
    }
  }

  public synchronized void cancel() {
    if (senderFuture != null)
      senderFuture.cancel(false);
    senderFuture = null;
  }

  private synchronized void sendHeartbeat() {
    try {
      System.out.println("[HBSender] Sending heartbeat from " + sender);
      final Heartbeat heartbeat = new Heartbeat(sender, group);
      busWriter.write(heartbeat);
    } catch (final EOFException e) {
      e.printStackTrace();
    }
  }

}
