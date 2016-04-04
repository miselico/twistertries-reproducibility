package miselico.clusteringLSH.splitmap;

import java.util.ArrayList;

import miselico.clusteringLSH.list.ListNode;
import miselico.clusteringLSH.list.SimpleList;
import miselico.clusteringLSH.trie.TrieNode;

import com.google.common.collect.Lists;

public class SplitMap<E> {
	private final ArrayList<SimpleList<TrieNode<E>>> splitPoints;

	/**
	 *
	 * @param height
	 *            the height of the trie. Note that the Splitmap has height + 1
	 *            levels (one for each node level in the trie)
	 */
	public SplitMap(int height) {
		this.splitPoints = Lists.newArrayListWithCapacity(height + 1);
		for (int i = 0; i <= height; i++) {
			this.splitPoints.add(SimpleList.<TrieNode<E>> create());
		}
	}

	/**
	 * Note that the {@link SplitMap} counts bottom up! Zero is the bottom of
	 * the trie, i.e. the leafs.
	 *
	 * @param node
	 * @param height
	 */
	public void addSplitPoint(ListNode<TrieNode<E>> node, int height) {
		SimpleList<TrieNode<E>> list = this.splitPoints.get(height);
		list.pushBack(node);
	}

	private ListNode<TrieNode<E>> getLowerSplitPoint() {
		for (int i = 0; i < this.splitPoints.size(); i++) {
			SimpleList<TrieNode<E>> l = this.splitPoints.get(i);
			if (!l.isEmpty()) {
				return l.getFirst();
			}
		}
		return null;
	}

	/**
	 * Returns the node associated with the lowest splitpoint from this
	 * splitmap. The point is not removed.
	 *
	 * @return the TrieNOde associated with the lowest splitpoint in the
	 *         {@link SplitMap}.
	 *
	 * @throws Error
	 *             if there are not points in the {@link SplitMap}.
	 */
	public TrieNode<E> getNodeOfLowerSplitPoint() {
		ListNode<TrieNode<E>> point = this.getLowerSplitPoint();
		if (point == null) {
			throw new Error("there is no splitpoint in this splitmap");
		}
		// point.removeSelf();
		return point.getValue();
	}

	public int lowestSplitPointHeight() {
		for (int i = 0; i < this.splitPoints.size(); i++) {
			SimpleList<TrieNode<E>> l = this.splitPoints.get(i);
			if (!l.isEmpty()) {
				return i;
			}
		}
		return Integer.MAX_VALUE;
	}

}
