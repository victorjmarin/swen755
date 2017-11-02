package pooling;

public class SchedulingTest {

  public static void main(final String[] args) {
    // Get PriorityThreadPoolExecutor with only 1 thread for the sake of testing scheduling.
    final PriorityThreadPoolExecutor threadPool = ThreadPool.getPriorityThreadPoolExecutor(1);

    final PriorityCallable<Integer> lowerPriorityTask = new PriorityCallable<Integer>() {

      @Override
      public Integer call() {
        System.out.println("Running lower priority task.");
        return null;
      }

      @Override
      public int getPriority() {
        return 1;
      }
    };

    final PriorityCallable<Integer> higherPriorityTask = new PriorityCallable<Integer>() {

      @Override
      public Integer call() {
        System.out.println("Running higher priority task.");
        return null;
      }

      @Override
      public int getPriority() {
        return 0;
      }
    };


    // Two lowerPriorityTask (t1 and t2) are submitted before a higherPriorityTask (t3).
    // Since we only have 1 thread available, the first submitted task (t1) will be run immediately,
    // and the other two will be executed based on their priority. Even though t2 is submitted
    // before t3, t3 has a higher priority and will be run before t2. Therefore, the output in
    // console should be:
    // Running lower priority task.
    // Running higher priority task.
    // Running lower priority task.

    // t1
    threadPool.submit(lowerPriorityTask);
    // t2
    threadPool.submit(lowerPriorityTask);
    // t3
    threadPool.submit(higherPriorityTask);

    threadPool.shutdown();

  }

}
