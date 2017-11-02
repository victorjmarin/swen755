package pooling;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {

  public PriorityThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize,
      final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  public <T> Future<T> submit(final PriorityCallable<T> task) {
    if (task == null)
      throw new NullPointerException();
    final RunnableFuture<T> ftask = newTaskFor(task);
    execute(ftask);
    return ftask;
  }

  protected <T> RunnableFuture<T> newTaskFor(final PriorityCallable<T> callable) {
    return new PriorityFutureTask<T>(callable);
  }

}
