package miselico.clusteringLSH;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import miselico.clusteringLSH.hashing.minHash.MinHashFactory;
import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveLeaf;
import miselico.clusteringLSH.hierarchy.tree.ActiveInternal;
import miselico.clusteringLSH.hierarchy.tree.PassiveElement;
import miselico.clusteringLSH.hierarchy.tree.PassiveInternal;
import miselico.clusteringLSH.hierarchy.tree.PassiveLeaf;
import miselico.clusteringLSH.hierarchy.tree.TreeHierarchyFactory;
import miselico.clusteringLSH.input.InputReader;
import miselico.clusteringLSH.input.InputSet;
import miselico.clusteringLSH.twistertrie.ForestConfig;
import miselico.clusteringLSH.twistertrie.TwisterTrieForest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class IntegrationTest extends TestCase {
	//	public static void main(String[] args) throws IOException {
	//		Stopwatch w = Stopwatch.createStarted();
	//
	//		String input = "/miselico/clusteringLSH/runs/simpletest1.dat";
	//		InputStreamReader reader = new InputStreamReader(Main.class.getResourceAsStream(input), StandardCharsets.UTF_8);
	//		List<InputSet> sets = InputReader.read(reader);
	//
	//		TwisterTrieForest forest = new TwisterTrieForest(2, 1);
	//		for (InputSet inputSet : sets) {
	//			forest.add(inputSet.id, inputSet.data);
	//		}
	//
	//		ActiveElement result = forest.twist();
	//		System.out.println(result);
	//		w.stop();
	//		System.out.println(w.elapsed(TimeUnit.MILLISECONDS));
	//	}

	private static ActiveElement<ImmutableList<String>> clusterFromResource(String inputResource, ForestConfig config) throws IOException {
		InputStreamReader reader = new InputStreamReader(IntegrationTest.class.getResourceAsStream(inputResource), StandardCharsets.UTF_8);
		return IntegrationTest.cluster(reader, config);
	}

	private static ActiveElement<ImmutableList<String>> cluster(Reader reader, ForestConfig config) throws IOException {
		List<InputSet<ImmutableList<String>>> sets = InputReader.read(reader);
		TwisterTrieForest<ImmutableList<String>> forest = new TwisterTrieForest<>(config, new TreeHierarchyFactory<ImmutableList<String>>(), new MinHashFactory());
		for (InputSet<ImmutableList<String>> inputSet : sets) {
			forest.add(inputSet.id, inputSet.data);
		}
		ActiveElement<ImmutableList<String>> result = forest.twist(new Random(4654654L));
		return result;
	}

	private static ActiveElement<ImmutableList<String>> clusterDirect(String data, ForestConfig config) throws IOException {
		StringReader r = new StringReader(data);
		return IntegrationTest.cluster(r, config);
	}

	private static final ImmutableList<ForestConfig> configs = ImmutableList.of(new ForestConfig(1, 1), new ForestConfig(1, 2), new ForestConfig(2, 1), new ForestConfig(2, 2),
			new ForestConfig(20, 15));

	public void testOnePoint1() throws Exception {
		for (ForestConfig config : IntegrationTest.configs) {
			String input = "set1: a";
			ActiveElement<ImmutableList<String>> result = IntegrationTest.clusterDirect(input, config);
			Assert.assertTrue(result instanceof ActiveLeaf);
			ActiveLeaf<ImmutableList<String>> leaf = (ActiveLeaf<ImmutableList<String>>) result;
			Assert.assertTrue(leaf.getID().equals("set1"));
		}
	}

	public void testOnePoint2() throws Exception {
		for (ForestConfig config : IntegrationTest.configs) {
			String input = "set1: a b c d e f";
			ActiveElement<ImmutableList<String>> result = IntegrationTest.clusterDirect(input, config);
			Assert.assertTrue(result instanceof ActiveLeaf);
			ActiveLeaf<ImmutableList<String>> leaf = (ActiveLeaf<ImmutableList<String>>) result;
			Assert.assertTrue(leaf.getID().equals("set1"));
		}
	}

	public void testTwoSamePoints() throws Exception {
		for (ForestConfig config : IntegrationTest.configs) {
			String input = "set1: a b\nset2: a b";
			ActiveElement<ImmutableList<String>> result = IntegrationTest.clusterDirect(input, config);
			Assert.assertTrue(result instanceof ActiveInternal);
			ActiveInternal<ImmutableList<String>> internal = (ActiveInternal<ImmutableList<String>>) result;
			Assert.assertEquals(2, internal.getValueCount());
			Assert.assertEquals(config.getNumberOfTries(), internal.getRecords().size());
			PassiveElement left = internal.getLeft();
			PassiveElement right = internal.getRight();
			Assert.assertTrue(left instanceof PassiveLeaf);
			Assert.assertTrue(right instanceof PassiveLeaf);
			@SuppressWarnings("unchecked")
			PassiveLeaf<ImmutableList<String>> leftLeaf = (PassiveLeaf<ImmutableList<String>>) left;
			@SuppressWarnings("unchecked")
			PassiveLeaf<ImmutableList<String>> rightLeaf = (PassiveLeaf<ImmutableList<String>>) right;
			Assert.assertTrue((leftLeaf.getID().equals("set1") && rightLeaf.getID().equals("set2")) || (leftLeaf.getID().equals("set2") && rightLeaf.getID().equals("set1")));
		}
	}

	public void testTwoPointsWithSameHash() throws Exception {
		for (ForestConfig config : IntegrationTest.configs) {
			String input = "set1: a b\nset2: b c";
			ActiveElement<ImmutableList<String>> result = IntegrationTest.clusterDirect(input, config);
			Assert.assertTrue(result instanceof ActiveInternal);
			ActiveInternal<ImmutableList<String>> internal = (ActiveInternal<ImmutableList<String>>) result;
			Assert.assertEquals(2, internal.getValueCount());
			Assert.assertEquals(config.getNumberOfTries(), internal.getRecords().size());
			PassiveElement left = internal.getLeft();
			PassiveElement right = internal.getRight();
			Assert.assertTrue(left instanceof PassiveLeaf);
			Assert.assertTrue(right instanceof PassiveLeaf);
			@SuppressWarnings("unchecked")
			PassiveLeaf<ImmutableList<String>> leftLeaf = (PassiveLeaf<ImmutableList<String>>) left;
			@SuppressWarnings("unchecked")
			PassiveLeaf<ImmutableList<String>> rightLeaf = (PassiveLeaf<ImmutableList<String>>) right;
			Assert.assertTrue((leftLeaf.getID().equals("set1") && rightLeaf.getID().equals("set2")) || (leftLeaf.getID().equals("set2") && rightLeaf.getID().equals("set1")));
		}
	}

	public void testTwoNonIntersectingPoints() throws Exception {
		for (ForestConfig config : IntegrationTest.configs) {
			String input = "set1: a b\nset2: c d";
			ActiveElement<ImmutableList<String>> result = IntegrationTest.clusterDirect(input, config);
			Assert.assertTrue(result instanceof ActiveInternal);
			ActiveInternal<ImmutableList<String>> internal = (ActiveInternal<ImmutableList<String>>) result;
			Assert.assertEquals(2, internal.getValueCount());
			Assert.assertEquals(config.getNumberOfTries(), internal.getRecords().size());
			PassiveElement left = internal.getLeft();
			PassiveElement right = internal.getRight();
			Assert.assertTrue(left instanceof PassiveLeaf);
			Assert.assertTrue(right instanceof PassiveLeaf);
			PassiveLeaf<?> leftLeaf = (PassiveLeaf<?>) left;
			PassiveLeaf<?> rightLeaf = (PassiveLeaf<?>) right;
			Assert.assertTrue((leftLeaf.getID().equals("set1") && rightLeaf.getID().equals("set2")) || (leftLeaf.getID().equals("set2") && rightLeaf.getID().equals("set1")));
		}
	}

	public void testThreeSamePoints() throws Exception {
		for (ForestConfig config : IntegrationTest.configs) {
			String input = "set1: a b\nset2: a b\nset3:b a";
			ActiveElement<ImmutableList<String>> result = IntegrationTest.clusterDirect(input, config);
			Assert.assertTrue(result instanceof ActiveInternal);
			ActiveInternal<ImmutableList<String>> internal = (ActiveInternal<ImmutableList<String>>) result;
			Assert.assertEquals(3, internal.getValueCount());
			Assert.assertEquals(config.getNumberOfTries(), internal.getRecords().size());
			PassiveElement left = internal.getLeft();
			PassiveElement right = internal.getRight();
			Assert.assertTrue((left instanceof PassiveLeaf) || (right instanceof PassiveLeaf));
			//FIXME this is already somewhat hard to check.

			//Assert.assertTrue((leftLeaf.getID().equals("set1") && rightLeaf.getID().equals("set2")) || (leftLeaf.getID().equals("set2") && rightLeaf.getID().equals("set1")));
		}
	}

	public void testTwoPairsOfSamePoints() throws Exception {
		for (ForestConfig config : IntegrationTest.configs) {
			String input = "set1: a b\nset2: a b\nset3:d e\nset4:d e";
			ActiveElement<ImmutableList<String>> result = IntegrationTest.clusterDirect(input, config);
			Assert.assertTrue(result instanceof ActiveInternal);
			ActiveInternal<ImmutableList<String>> internal = (ActiveInternal<ImmutableList<String>>) result;
			Assert.assertEquals(4, internal.getValueCount());
			Assert.assertEquals(config.getNumberOfTries(), internal.getRecords().size());
			PassiveElement left = internal.getLeft();
			PassiveElement right = internal.getRight();
			//FIXME this is already somewhat hard to check, right now manual inspection is needed

			//Assert.assertTrue((leftLeaf.getID().equals("set1") && rightLeaf.getID().equals("set2")) || (leftLeaf.getID().equals("set2") && rightLeaf.getID().equals("set1")));
		}
	}

	public void testBig() throws Exception {
		for (int i = 1000; i <= 3000; i += 1000) {
			for (ForestConfig config : IntegrationTest.configs) {
				String data = IntegrationTest.generateData(new Random(44564), i);

				ActiveElement<ImmutableList<String>> result = IntegrationTest.clusterDirect(data, config);
				Assert.assertTrue(result instanceof ActiveInternal);
				ActiveInternal<ImmutableList<String>> internal = (ActiveInternal<ImmutableList<String>>) result;
				Assert.assertEquals(i, internal.getValueCount());
				Assert.assertEquals(config.getNumberOfTries(), internal.getRecords().size());
				PassiveElement left = internal.getLeft();
				PassiveElement right = internal.getRight();
				//FIXME this is already somewhat hard to check, right now manual inspection is needed
				//Assert.assertTrue((leftLeaf.getID().equals("set1") && rightLeaf.getID().equals("set2")) || (leftLeaf.getID().equals("set2") && rightLeaf.getID().equals("set1")));
				Assert.assertTrue("Not all expected elements are in the tree", CheckAllIn.checkAllIn(result, i));
			}
		}
	}

	static String generateData(Random r, int numberOfSet) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < numberOfSet; i++) {
			b.append("set" + i + ":");
			int amount = r.nextInt(5) + 1;
			for (int j = 0; j < amount; j++) {
				b.append(r.nextInt(1000));
				b.append(' ');
			}
			b.append('\n');
		}
		return b.toString();
	}

	private static class CheckAllIn {

		private static boolean checkAllIn(ActiveElement<ImmutableList<String>> result, int i) {
			Set<String> s = Sets.newHashSet();
			for (int j = 0; j < i; j++) {
				s.add("set" + j);
			}
			if (result instanceof ActiveLeaf) {
				ActiveLeaf<ImmutableList<String>> leaf = (ActiveLeaf<ImmutableList<String>>) result;
				s.remove(leaf.getID());
			} else if (result instanceof ActiveInternal) {
				ActiveInternal<ImmutableList<String>> internal = (ActiveInternal<ImmutableList<String>>) result;
				CheckAllIn.removeAllRecursive(s, internal.getLeft());
				CheckAllIn.removeAllRecursive(s, internal.getRight());
			} else {
				throw new Error();
			}
			return s.isEmpty();
		}

		private static void removeAllRecursive(Set<String> s, PassiveInternal node) {
			PassiveElement left = node.getLeft();
			PassiveElement right = node.getRight();
			CheckAllIn.removeAllRecursive(s, left);
			CheckAllIn.removeAllRecursive(s, right);
		}

		private static void removeAllRecursive(Set<String> s, PassiveElement node) {
			if (node instanceof PassiveInternal) {
				CheckAllIn.removeAllRecursive(s, (PassiveInternal) node);
			} else if (node instanceof PassiveLeaf) {
				Assert.assertTrue(s.remove(((PassiveLeaf) node).getID()));
			}
		}

	}
}
