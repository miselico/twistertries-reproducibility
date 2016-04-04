package miselico.clusteringLSH.runs.twistertriearticle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.Assert;
import miselico.clusteringLSH.hashing.RHH.RHHFactory;
import miselico.clusteringLSH.hashing.minHash.MinHashFactory;
import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;
import miselico.clusteringLSH.hierarchy.printing.PrintingHierarchyFactory;
import miselico.clusteringLSH.input.Cifar10Reader;
import miselico.clusteringLSH.input.InputReader;
import miselico.clusteringLSH.input.InputSet;
import miselico.clusteringLSH.parallel.HashResult;
import miselico.clusteringLSH.parallel.ParallelHashing;
import miselico.clusteringLSH.twistertrie.ForestConfig;
import miselico.clusteringLSH.twistertrie.TwisterTrieForestIOFriendlyAdd;

import org.springframework.util.StopWatch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The first part of the experiments for timing.
 *
 * Timing is measured for
 *
 * cifar-10 : {10,000...10,000...60,000} images
 *
 * TRC2 : {50,000...50,000...500,000 } U {1,000,000 1,500,000 1675575} new
 * stories
 *
 * Timing for larger amounts of news stories are in TimingExtremelyLargeRun
 *
 * @author michael
 *
 */

public class TimingLargeRun {
	public static void main(String[] args) {
		File f = new File("largeRunTiming/");

		try {
			File cifaroutput = new File(f, "cifar-10-1000");
			cifaroutput.mkdirs();
			TimingLargeRun.runCifar10(new File("datasets/cifar-10-batches-bin/allData.bin"), cifaroutput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			File reutersOutput = new File(f, "reuters-1000");
			reutersOutput.mkdirs();
			int[] amounts = new int[] { 50000, 100000, 150000, 200000, 250000, 300000, 350000, 400000, 450000, 500000, 1000000, 1500000, 1675575 };			
			TimingLargeRun.runReuters(new File("/home/miselico/data/cleaned-stemmedheadlines-docs.csv"), reutersOutput, amounts);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void runCifar10(File cifarInput, File outputFolder) throws IOException {
		int[] treeheights = new int[] { 60 };
		int[] treeCounts = new int[] { 20 };
		long[] seeds = new long[3];
		Random r = new Random(4567564L);
		for (int i = 0; i < seeds.length; i++) {
			seeds[i] = r.nextLong();
		}
		String dataset = "cifar-1000";

		int[] amounts = new int[] {/* warmup */1000, 10000, 20000, 30000, 40000, 50000, 60000 };

		try (OutputStreamWriter out2 = new OutputStreamWriter(new FileOutputStream(new File(outputFolder, "timing.txt")))) {
			List<InputSet<int[]>> alldata = Cifar10Reader.read(cifarInput, 60000);
			out2.write("#item_count adding_time twisting_time");
			
			for (int amount : amounts) {
				List<InputSet<int[]>> data = Lists.newArrayList(Iterables.limit(alldata, amount));

				for (int treeHeight : treeheights) {
					for (int numberOfTrees : treeCounts) {
						ForestConfig config = new ForestConfig(numberOfTrees, treeHeight);
						System.out.println("starting " + dataset + ", " + amount + "items, " + treeHeight + "height, " + numberOfTrees + "trees");
						for (long seed : seeds) {
							StopWatch timer = new StopWatch(String.format("%s_%d_items_%d_tries_%d_height_%d_seed.dat", dataset, amount, numberOfTrees, treeHeight, seed));

							BlockingQueue<InputSet<int[]>> dataQueue = new LinkedBlockingQueue<>();
							for (InputSet<int[]> is : data) {
								try {
									dataQueue.put(is);
								} catch (InterruptedException e) {
									throw new Error(e);
								}
							}
							HierarchyFactory<int[]> factory = new PrintingHierarchyFactory<>(new OutputStream() {

								@Override
								public void write(int b) throws IOException {
									//ignore
								}
							});

							RHHFactory lshFact = new RHHFactory(3072);
							ParallelHashing<int[]> hasher = new ParallelHashing<>(config, lshFact, seed);
							System.gc();
							timer.start("Adding");
							List<Future<HashResult<int[]>>> outcome = hasher.hashParallel(dataQueue, amount);
							TwisterTrieForestIOFriendlyAdd<int[]> forest = new TwisterTrieForestIOFriendlyAdd<>(config, factory);
							forest.add(outcome);
							timer.stop();
							long addTime = timer.getLastTaskTimeMillis();
							System.gc();
							timer.start("Twisting");
							ActiveElement<int[]> result = forest.twist(new Random(seed));
							timer.stop();
							long twistTime = timer.getLastTaskTimeMillis();
							System.gc();
							Assert.assertEquals(amount, result.getValueCount());
							//out.write(timer.prettyPrint());
							out2.write(amount + " " + addTime + " " + twistTime + '\n');
							out2.flush();
						}
					}
				}
			}
		}
	}

	public static void runReuters(File TRC2Input, File outputFolder, int[] amounts) throws IOException {
		int[] treeheights = new int[] { 20 };
		int[] treeCounts = new int[] { 20 };
		long[] seeds = new long[3];
		Random r = new Random(4567564L);
		for (int i = 0; i < seeds.length; i++) {
			seeds[i] = r.nextLong();
		}

		String dataset = "full_data";

		try (OutputStreamWriter out2 = new OutputStreamWriter(new FileOutputStream(new File(outputFolder, "timing.txt"), true))) {
			for (int amount : amounts) {
				for (int treeHeight : treeheights) {
					for (int numberOfTrees : treeCounts) {
						System.out.println("starting " + dataset + ", " + amount + " items, " + treeHeight + "height, " + numberOfTrees + " trees");
						ForestConfig config = new ForestConfig(numberOfTrees, treeHeight);
						for (long seed : seeds) {
							StopWatch timer = new StopWatch(String.format("%s_%d_items_%d_tries_%d_height_%d_seed.dat", dataset, amount, numberOfTrees, treeHeight, seed));
							BlockingQueue<InputSet<ImmutableList<String>>> data = InputReader.readAsync(new FileReader(TRC2Input), amount);

							HierarchyFactory<ImmutableList<String>> factory = new PrintingHierarchyFactory<>(new OutputStream() {

								@Override
								public void write(int b) throws IOException {
									//ignore
								}
							});

							MinHashFactory lshFact = new MinHashFactory();
							ParallelHashing<ImmutableList<String>> hasher = new ParallelHashing<>(config, lshFact, seed);
							System.gc();
							timer.start("Adding");
							List<Future<HashResult<ImmutableList<String>>>> outcome = hasher.hashParallel(data, amount);
							TwisterTrieForestIOFriendlyAdd<ImmutableList<String>> forest = new TwisterTrieForestIOFriendlyAdd<>(config, factory);
							forest.add(outcome);
							timer.stop();
							long addTime = timer.getLastTaskTimeMillis();
							System.gc();
							timer.start("Twisting");
							ActiveElement<ImmutableList<String>> result = forest.twist(new Random(seed));
							timer.stop();
							long twistTime = timer.getLastTaskTimeMillis();
							System.gc();
							Assert.assertEquals(amount, result.getValueCount());
							out2.write(amount + " " + addTime + " " + twistTime + '\n');
							out2.flush();
						}
					}
				}
			}

		}
	}
}
