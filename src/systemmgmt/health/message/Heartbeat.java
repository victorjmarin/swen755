package systemmgmt.health.message;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class Heartbeat implements MappedBusMessage {

  public String sender;
  public String group;

  public Heartbeat() {}

  public Heartbeat(final String sender, final String group) {
    this.sender = sender;
    this.group = group;
  }

  @Override
  public void write(final MemoryMappedFile mem, final long pos) {
    final byte[] senderData = sender.getBytes();
    final int senderLen = senderData.length;
    final byte[] groupData = group.getBytes();
    final int groupLen = groupData.length;
    mem.putInt(pos, senderLen);
    mem.putInt(pos + 4, groupLen);
    mem.setBytes(pos + 8, senderData, 0, senderLen);
    mem.setBytes(pos + 8 + senderLen, groupData, 0, groupLen);
  }

  @Override
  public void read(final MemoryMappedFile mem, final long pos) {
    final int senderLen = mem.getInt(pos);
    final byte[] senderData = new byte[senderLen];
    final int groupLen = mem.getInt(pos + 4);
    final byte[] groupData = new byte[groupLen];
    mem.getBytes(pos + 8, senderData, 0, senderLen);
    mem.getBytes(pos + 8 + senderLen, groupData, 0, groupLen);
    sender = new String(senderData);
    group = new String(groupData);
  }

  @Override
  public int type() {
    return MessageType.HEARTBEAT.ordinal();
  }

}
