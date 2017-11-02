package pooling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

  public static PriorityThreadPoolExecutor getPriorityThreadPoolExecutor(final int corePoolSize) {
    final PriorityBlockingQueue<Runnable> jobs = new PriorityBlockingQueue<>();
    return new PriorityThreadPoolExecutor(corePoolSize, corePoolSize, 20, TimeUnit.SECONDS, jobs);
  }

}
