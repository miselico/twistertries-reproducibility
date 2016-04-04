package miselico.clusteringLSH.management;

import java.util.Random;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;
import miselico.clusteringLSH.splitmap.SplitMap;
import miselico.clusteringLSH.trie.TrieInternalNode;
import miselico.clusteringLSH.trie.TrieInternalNode.PairOfChildren;
import miselico.clusteringLSH.trie.TrieLeafNode;
import miselico.clusteringLSH.trie.TrieNode;

public class Driver {
	public static <E> void cluster(Iterable<SplitMap<E>> splitMaps, int rootHeight, HierarchyFactory<E> factory, Random rng) {
		RecursiveMerger<E> merger = new RecursiveMerger<>(rng, factory);

		while (true) {
			int lowest = Integer.MAX_VALUE;
			SplitMap<E> mapWithLowest = null;
			for (SplitMap<E> splitMap : splitMaps) {
				int lowestInThisMap = splitMap.lowestSplitPointHeight();
				if (lowestInThisMap < lowest) {
					lowest = lowestInThisMap;
					mapWithLowest = splitMap;
					if (lowest == 0) {
						// We cannot go lower in any case
						break;
					}
				}
			}
			if (lowest > rootHeight) {
				//System.out.println("Merging done");
				return;
			}
			// FIXME this has changed : getNodeOfLowe... does not remove the TrieNode from the splitMap!
			TrieNode<E> lowestSplitPoint = mapWithLowest.getNodeOfLowerSplitPoint();
			if (lowest == 0) {
				// lowest split point is a leaf
				ActiveElement<E> leftActive = ((TrieLeafNode<E>) lowestSplitPoint).getFirstElement();
				ActiveElement<E> rightActive = ((TrieLeafNode<E>) lowestSplitPoint).getSecondElement();
				merger.merge(leftActive, rightActive, splitMaps);
			} else {
				// get two children and go down in the tree
				TrieInternalNode<E> startNode = (TrieInternalNode<E>) lowestSplitPoint;
				PairOfChildren<E> pair = startNode.getTwoChildren();
				ActiveElement<E> leftActive = Driver.traverseToBottom(pair.child1, lowest - 1);
				ActiveElement<E> rightActive = Driver.traverseToBottom(pair.child2, lowest - 1);
				merger.merge(leftActive, rightActive, splitMaps);
			}
		}
	}

	private static <E> ActiveElement<E> traverseToBottom(TrieNode<E> node, int height) {
		if (height == 0) {
			return ((TrieLeafNode<E>) node).getOnlyElement();
		} else if (height >= 1) {
			return Driver.traverseToBottom(((TrieInternalNode<E>) node).getOnlyChild(), height - 1);
		} else {
			throw new Error("Height must be positive.");
		}
	}

}
