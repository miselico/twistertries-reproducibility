package miselico.clusteringLSH.twistertrie;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveLeaf;
import miselico.clusteringLSH.management.HashCodeProvider;
import miselico.clusteringLSH.management.RecursiveAdder;
import miselico.clusteringLSH.splitmap.SplitMap;
import miselico.clusteringLSH.trie.TrieInternalNode;
import miselico.clusteringLSH.trie.TrieLeafNode;
import miselico.clusteringLSH.trie.TrieNode;
import miselico.clusteringLSH.trie.TrieRootNode;

public class TwisterTrie<E> {

	private final TrieRootNode<E> root;
	private final SplitMap<E> splitMap;

	public TwisterTrie(int height) {
		this.root = new TrieRootNode<>();
		this.splitMap = new SplitMap<>(height);
	}

	// new ActiveLeaf(is.id), the activeleaf must be the same for all tries
	public void add(ActiveLeaf<E> leaf, HashCodeProvider provider) {
		RecursiveAdder.add(this.root, provider, leaf, this.splitMap);
	}

	SplitMap<E> getSplitMap() {
		return this.splitMap;
	}

	/**
	 * Get the result of the clustering. This includes correctness checks for
	 * the first trie
	 *
	 * @return the root of the dendogram
	 */
	ActiveElement<E> getResult() {
		TrieNode<E> node = this.root;
		while (!node.isLeaf()) {
			node = ((TrieInternalNode<E>) node).getOnlyChild();
		}
		TrieLeafNode<E> trieLeaf = (TrieLeafNode<E>) node;

		ActiveElement<E> result = trieLeaf.getOnlyElement();
		return result;
	}

}
