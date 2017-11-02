package pooling;

import java.util.concurrent.FutureTask;

public class PriorityFutureTask<V> extends FutureTask<V>
    implements Comparable<PriorityFutureTask<V>> {

  private final PriorityCallable<V> callable;

  public PriorityFutureTask(final PriorityCallable<V> callable) {
    super(callable);
    this.callable = callable;
  }

  @Override
  public int compareTo(final PriorityFutureTask<V> o) {
    return callable.compareTo(o.getCallable());
  }

  protected PriorityCallable<V> getCallable() {
    return callable;
  }

}
