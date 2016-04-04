package miselico.clusteringLSH.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.util.concurrent.MoreExecutors;

public class ThreadManager {
	private static final int numberOfCPUs = Runtime.getRuntime().availableProcessors();
	// private static final ExecutorService pool =
	// Executors.newFixedThreadPool(ParallelHashing.numberOfCPUs + 5);
	public static final ExecutorService pool;

	static {
		ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(ThreadManager.numberOfCPUs + 5);
		pool = MoreExecutors.getExitingExecutorService(tpe);
	}
}
