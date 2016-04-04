package miselico.clusteringLSH.management;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.list.ListLength;
import miselico.clusteringLSH.list.ListNode;
import miselico.clusteringLSH.splitmap.SplitMap;
import miselico.clusteringLSH.trie.TrieInternalNode;
import miselico.clusteringLSH.trie.TrieLeafNode;
import miselico.clusteringLSH.trie.TrieNode;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;

public class RecursiveAdder {

	private RecursiveAdder() {

	}

	// /**
	// * Avoid use if more specific type is known
	// * @param root
	// * @param provider
	// * @param element
	// * @param map
	// */
	// private static void add(TrieNode root, HashCodeProvider provider,
	// ActiveElement<E>element, SplitMap<E>map) {
	// if (root instanceof TrieInternalNode) {
	// add((TrieInternalNode) root, provider, element, map);
	// } else if (root instanceof TrieLeafNode) {
	// Preconditions.checkArgument(provider.isEmpty(),
	// "Provider must be empty is the root is a leaf");
	// addToLeaf((TrieLeafNode) root, element, map);
	// } else {
	// throw new Error("Impossible");
	// }
	// }

	public static <E> void add(TrieInternalNode<E> root, HashCodeProvider provider, ActiveElement<E> element, SplitMap<E> map) {
		Preconditions.checkArgument(!provider.isEmpty(), "provider cannot be empty");
		if (!root.hasChildren()) {
			// shortcut
			RecursiveAdder.addRecursiveNewChain(root, provider, element);
			return;
		}
		HashCode hashCode = provider.pop();
		TrieNode<E> child = root.getChild(hashCode);
		if (child == null) {
			if (provider.isEmpty()) {
				child = new TrieLeafNode<>(root, hashCode);
			} else {
				child = new TrieInternalNode<>(root, hashCode);
			}
			if (root.getChildCount() == 1) {
				// the root has to be made a split point
				ListNode<TrieNode<E>> node = new TrieNodeListNode<>(root);
				root.setSplitPoint(node);
				map.addSplitPoint(node, provider.getAmountLeft() + 1);
			}
			// now we attach the child to the root
			root.putChild(hashCode, child);
		}
		if (child.isLeaf()) {
			RecursiveAdder.<E> addToLeaf((TrieLeafNode<E>) child, element, map);
		} else {
			RecursiveAdder.add((TrieInternalNode<E>) child, provider, element, map);
		}
	}

	public static <E> void addToLeaf(TrieLeafNode<E> leaf, ActiveElement<E> element, SplitMap<E> map) {
		if (leaf.getElementCount() == ListLength.ONE) {
			// the leaf must be added to the splitmap
			TrieNodeListNode<E> node = new TrieNodeListNode<>(leaf);
			leaf.setSplitPoint(node);
			map.addSplitPoint(node, 0);
		}
		leaf.attachElement(element);
	}

	private static <E> void addRecursiveNewChain(TrieInternalNode<E> root, HashCodeProvider provider, ActiveElement<E> element) {
		Preconditions.checkArgument(!provider.isEmpty(), "Provider cannot be empty");
		HashCode hashCode = provider.pop();
		if (!provider.isEmpty()) {
			TrieInternalNode<E> child = new TrieInternalNode<>(root, hashCode);
			root.putChild(hashCode, child);
			RecursiveAdder.addRecursiveNewChain(child, provider, element);
		} else {
			TrieLeafNode<E> child = new TrieLeafNode<>(root, hashCode);
			root.putChild(hashCode, child);
			RecursiveAdder.addToLeafNewChain(child, element);
		}
	}

	private static <E> void addToLeafNewChain(TrieLeafNode<E> leaf, ActiveElement<E> element) {
		leaf.attachElement(element);
	}

	private static class TrieNodeListNode<E> extends ListNode<TrieNode<E>> {

		private final TrieNode<E> trieNode;

		public TrieNodeListNode(TrieNode<E> trieNode) {
			super();
			this.trieNode = trieNode;
		}

		@Override
		public TrieNode<E> getValue() {
			return this.trieNode;
		}
	}

}
