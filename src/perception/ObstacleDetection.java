package perception;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import perception.object.EnvObject;
import pooling.PriorityCallable;
import pooling.PriorityThreadPoolExecutor;
import pooling.ThreadPool;
import systemmgmt.health.HBSender;

public class ObstacleDetection implements IObstacleDetection {

	public static final String GROUP = "ObstacleDetection";
	private final HBSender hbSender;

	private static final int ARRAY_SIZE = Integer.MAX_VALUE / 10;
	private static final int NUMBER_OF_TASKS = 20;

	private static final int POOL_SIZE = 10;
	private static final PriorityThreadPoolExecutor THREAD_POOL = ThreadPool.getPriorityThreadPoolExecutor(POOL_SIZE);

	public ObstacleDetection(final String heartbeatBus, final String sender) {
		hbSender = new HBSender(heartbeatBus, sender, GROUP);
		hbSender.enable(Executors.newSingleThreadScheduledExecutor());
	}

	public void run() {
		try {
			// for (;;) {
			// detectObstacles(new HashSet<>());
			splitGiantArrayLogSum(rndArray(ARRAY_SIZE));
			// }
		} catch (final Exception e) {
			e.printStackTrace();
			hbSender.cancel();
		}
	}

	private void splitGiantArrayLogSum(int[] data) {
		try {
			List<Future<Double>> tasks = new ArrayList<>(POOL_SIZE);
			int len = data.length;
			int load = len / NUMBER_OF_TASKS;
			for (int i = 0; i < NUMBER_OF_TASKS; i++) {
				PriorityCallable<Double> t = logSum(i * load, (i + 1) * load, data, rndPriority());
				tasks.add(THREAD_POOL.submit(t));
			}
			double result = 0;
			for (Future<Double> f : tasks) {
				result += f.get();
			}
			System.out.println("Total sum: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int rndPriority() {
		return ThreadLocalRandom.current().nextInt(NUMBER_OF_TASKS);
	}

	private int[] rndArray(int size) {
		int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			int rndInt = ThreadLocalRandom.current().nextInt(1, 999);
			result[i] = rndInt;
		}
		return result;
	}

	private PriorityCallable<Double> logSum(final int from, final int to, int[] data, final int priority) {
		return new PriorityCallable<Double>() {

			@Override
			public Double call() {
				double partialSum = 0;
				for (int i = from; i < to; i++) {
					partialSum += Math.log(data[i]);
				}
				return partialSum;
			}

			@Override
			public int getPriority() {
				return priority;
			}
		};
	}

	@Override
	public Set<EnvObject> detectObstacles(final Set<EnvObject> objects) {
		return null;
	}

}
