package miselico.clusteringLSH.runs.twistertriearticle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
import miselico.clusteringLSH.hierarchy.standardprinting.StandardPrintingHierarchyFactory;
import miselico.clusteringLSH.input.Cifar10Reader;
import miselico.clusteringLSH.input.InputReader;
import miselico.clusteringLSH.input.InputSet;
import miselico.clusteringLSH.parallel.HashResult;
import miselico.clusteringLSH.parallel.ParallelHashing;
import miselico.clusteringLSH.twistertrie.ForestConfig;
import miselico.clusteringLSH.twistertrie.TwisterTrieForestIOFriendlyAdd;

import com.google.common.collect.ImmutableList;

/**
 *
 * This is the implementation of the first four experiments in the SIGMOD 2015
 * paper.
 *
 * 1000 news stories are read and for treeHeights {1..5..10..125} and counts
 * {1..5..10..125} clustering is performed.
 *
 * 1000 cifar-10 images are read and for treeHeights {1..5..10..125} and counts
 * {1..5..10..125} clustering is performed.
 *
 * The calculation of the B_k and JDR is done based on the output files in
 * Python.
 *
 * @author michael
 *
 */
public class BkAndJDRExperiments {

	public static void performExperiment1till4(File cifarDatasetLocation, File reuterDatasetLocation, File cifaroutput, File reutersOutput) throws IOException {
		cifaroutput.mkdirs();
		BkAndJDRExperiments.runCifar10(cifarDatasetLocation, cifaroutput);
		reutersOutput.mkdirs();
		BkAndJDRExperiments.runTRC2(reuterDatasetLocation, reutersOutput);
	}

	public static void main(String[] args) throws IOException {
		File f = new File("/home/hamou/largeRun2/");
		File cifaroutput = new File(f, "cifar-10-1000");
		File reutersOutput = new File(f, "reuters-1000");
		File cifarDatasetLocation = new File("datasets/cifar-10-batches-bin/data_batch_1.bin");
		File reuterDatasetLocation = new File("/home/miselico/data/cleaned-stemmedheadlines-docs.csv");
		BkAndJDRExperiments.performExperiment1till4(cifarDatasetLocation, reuterDatasetLocation, cifaroutput, reutersOutput);
	}

	private static void runCifar10(File datasetLocation, File outputFolder) throws IOException {
		int[] treeheights = new int[] { 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120, 125 };
		int[] treeCounts = new int[] { 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120, 125 };
		long[] seeds = new long[10];
		Random r = new Random(4567564L);
		for (int i = 0; i < seeds.length; i++) {
			seeds[i] = r.nextLong();
		}
		String dataset = "cifar-1000";

		int amount = 1000;
		List<InputSet<int[]>> data = Cifar10Reader.read(datasetLocation, amount);
		//TODO this is how things used to be done, it has not yet been checked whether reading part of the dataset works properly
		//List<InputSet> alldata = Cifar10Reader.read(datasetLocation, 10000);
		//read 10000 and cut of first 1000
		//List<InputSet> data = Lists.newArrayList(Iterables.limit(alldata, amount));

		for (int treeHeight : treeheights) {
			for (int numberOfTrees : treeCounts) {
				ForestConfig config = new ForestConfig(numberOfTrees, treeHeight);
				System.out.println("starting " + dataset + " " + treeHeight + "height " + numberOfTrees + "trees");
				for (long seed : seeds) {
					BlockingQueue<InputSet<int[]>> dataQueue = new LinkedBlockingQueue<>();
					for (InputSet<int[]> is : data) {
						try {
							dataQueue.put(is);
						} catch (InterruptedException e) {
							throw new Error(e);
						}
					}
					File outputFile = new File(outputFolder, String.format("%s_%d_tries_%d_height_%d_seed.dat", dataset, numberOfTrees, treeHeight, seed));
					FileOutputStream out = new FileOutputStream(outputFile);
					HierarchyFactory<int[]> factory = new StandardPrintingHierarchyFactory<>(out, amount);

					//StopWatch w = new StopWatch("Clustering of " + amount + " elements from " + dataFile);
					//w.start("Adding ");
					RHHFactory lshFact = new RHHFactory(3072);
					ParallelHashing<int[]> hasher = new ParallelHashing<>(config, lshFact, seed);
					List<Future<HashResult<int[]>>> outcome = hasher.hashParallel(dataQueue, amount);
					TwisterTrieForestIOFriendlyAdd<int[]> forest = new TwisterTrieForestIOFriendlyAdd<>(config, factory);
					forest.add(outcome);
					//w.stop();
					//w.start("Twisting");
					ActiveElement<int[]> result = forest.twist(new Random(seed));
					//w.stop();

					Assert.assertEquals(amount, result.getValueCount());
					//System.out.println(w.prettyPrint());
					out.flush();
					out.close();
				}
			}
		}
	}

	private static void runTRC2(File reuterDatasetLocation, File reutersOutput) throws IOException {
		int[] treeheights = new int[] { 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120, 125 };
		int[] treeCounts = new int[] { 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120, 125 };
		long[] seeds = new long[10];
		Random r = new Random(4567564L);
		for (int i = 0; i < seeds.length; i++) {
			seeds[i] = r.nextLong();
		}

		String dataset = "full_data";
		int amount = 1000;

		for (int treeHeight : treeheights) {
			for (int numberOfTrees : treeCounts) {
				System.out.println("starting " + dataset + " " + treeHeight + "height " + numberOfTrees + " trees");
				ForestConfig config = new ForestConfig(numberOfTrees, treeHeight);
				for (long seed : seeds) {
					BlockingQueue<InputSet<ImmutableList<String>>> data = InputReader.readAsync(new FileReader(reuterDatasetLocation), amount);

					File outputFile = new File(reutersOutput, String.format("%s_%d_tries_%d_height_%d_seed.dat", dataset, numberOfTrees, treeHeight, seed));
					FileOutputStream out = new FileOutputStream(outputFile);
					HierarchyFactory<ImmutableList<String>> factory = new StandardPrintingHierarchyFactory<>(out, amount);

					//StopWatch w = new StopWatch(String.format("height %s, amount %s", treeHeight, numberOfTrees));
					//w.start("Adding ");
					MinHashFactory lshFact = new MinHashFactory();
					ParallelHashing<ImmutableList<String>> hasher = new ParallelHashing<>(config, lshFact, seed);
					List<Future<HashResult<ImmutableList<String>>>> outcome = hasher.hashParallel(data, amount);

					TwisterTrieForestIOFriendlyAdd<ImmutableList<String>> forest = new TwisterTrieForestIOFriendlyAdd<>(config, factory);
					forest.add(outcome);
					//w.stop();
					//w.start("Twisting");
					ActiveElement<ImmutableList<String>> result = forest.twist(new Random(seed));
					//w.stop();

					Assert.assertEquals(amount, result.getValueCount());
					//System.out.println(w.prettyPrint());
					out.flush();
					out.close();
				}
			}
		}
	}
}
