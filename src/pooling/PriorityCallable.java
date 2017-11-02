package pooling;

import java.util.concurrent.Callable;

public interface PriorityCallable<T> extends Callable<T>, Comparable<PriorityCallable<T>> {

  int getPriority();

  @Override
  default int compareTo(final PriorityCallable<T> o) {
    return new Integer(getPriority()).compareTo(o.getPriority());
  }

}
