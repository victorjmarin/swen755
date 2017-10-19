package systemmgmt.health;

import java.io.EOFException;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import io.mappedbus.MappedBusReader;
import systemmgmt.Boot;
import systemmgmt.health.message.Heartbeat;
import systemmgmt.health.message.MessageType;

public class HBReceiver {

  private MappedBusReader busReader;

  private long lossInterval;
  private TimeUnit timeUnit;

  private HashMap<String, ScheduledFuture<?>> lossDetectors;
  private ScheduledExecutorService executor;

  private HashMap<String, NodeGroup> nodeGroups;

  public HBReceiver(final String heartbeatBus) {
    this(HBSender.INTERVAL, HBSender.TIME_UNIT, heartbeatBus);
  }

  public HBReceiver(final long interval, final TimeUnit timeUnit, final String heartbeatBus) {
    try {
      busReader = new MappedBusReader(heartbeatBus, 100000L, 32);
      busReader.open();
      lossDetectors = new HashMap<>();
      lossInterval = interval * 2L;
      this.timeUnit = timeUnit;
      nodeGroups = new HashMap<>();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public void register(final String sender, final String group) {
    System.out.println("[HBReceiver] Registering sender " + sender);
    final Node node = new Node(sender);
    NodeGroup ng = null;
    ng = nodeGroups.get(group);
    if (ng == null) {
      ng = new NodeGroup(group);
      nodeGroups.put(group, ng);
    }
    ng.addNode(node);
  }

  public void enable(final ScheduledExecutorService executor) {
    if (executor != null) {
      System.out.println("[HBReceiver] Listening to heartbeats...");
      this.executor = executor;
      for (final NodeGroup ng : nodeGroups.values()) {
        for (final Node n : ng.nodes) {
          final ScheduledFuture<?> lossDetector =
              executor.schedule(() -> died(n.sender, n.group.name), lossInterval, timeUnit);
          lossDetectors.put(n.sender, lossDetector);
        }
      }
      listenForHeartbeats();
    }
  }

  private void listenForHeartbeats() {
    try {
      final Heartbeat heartbeat = new Heartbeat();
      for (;;) {
        while (busReader.next()) {
          final int type = busReader.readType();
          if (type == MessageType.HEARTBEAT.ordinal()) {
            busReader.readMessage(heartbeat);
            receiveHeartbeat(heartbeat.sender, heartbeat.group);
          }
        }
      }
    } catch (final EOFException e) {
      e.printStackTrace();
    }
  }

  private synchronized void receiveHeartbeat(final String sender, final String group) {
    ScheduledFuture<?> lossDetector = lossDetectors.remove(sender);
    System.out.println("[HBReceiver] Heartbeat received from " + sender);
    if (lossDetector != null)
      lossDetector.cancel(false);
    if (executor != null) {
      lossDetector = executor.schedule(() -> died(sender, group), lossInterval, timeUnit);
      lossDetectors.put(sender, lossDetector);
    }
  }

  private synchronized void died(final String sender, final String group) {
    final NodeGroup ng = nodeGroups.get(group);
    System.out.println("[HBReceiver] " + sender + " has died. Rebooting it...");
    Boot.spawnFor(sender);
    if (ng.active.sender.equals(sender)) {
      final Node active = ng.swapActive();
      if (active == null)
        System.out.println("[HBReceiver] No swapping alternative");
      else
        System.out.println("[HBReceiver] Swapped to " + active.sender);
    }
  }

  private class Node {
    int priority = -1;
    String sender;
    NodeGroup group;

    public Node(final String sender) {
      super();
      this.sender = sender;
    }

    public int getPriority() {
      return priority;
    }

    public void setPriority(final int priority) {
      this.priority = priority;
    }

  }

  private class NodeGroup {

    private int lastId;
    private final String name;
    private Node active;
    private final TreeSet<Node> nodes;

    public NodeGroup(final String name) {
      super();
      this.name = name;
      nodes = new TreeSet<>(Comparator.comparing(Node::getPriority));
    }

    public Node swapActive() {
      Node next = nodes.higher(active);
      if (next == null)
        next = nodes.lower(active);
      active = next;
      return active;
    }

    public void addNode(final Node n) {
      if (n.getPriority() == -1)
        n.setPriority(lastId++);
      n.group = this;
      nodes.add(n);
      if (active == null)
        active = n;
    }

  }

}
