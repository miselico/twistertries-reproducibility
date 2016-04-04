package miselico.clusteringLSH.hierarchy;

import miselico.clusteringLSH.list.ListNode;
import miselico.clusteringLSH.trie.TrieLeafNode;

/**
 * Class bridging between the element and the trie 'world' It contains the
 * references which the element has to a leaf of one trie.
 *
 * @author michael
 *
 */
public class TrieLeafRecord<E> {
	public final TrieLeafNode<E> leaf;
	public final ListNode<ActiveElement<E>> backreference;

	public TrieLeafRecord(TrieLeafNode<E> leaf, ListNode<ActiveElement<E>> backreference) {
		super();
		this.leaf = leaf;
		this.backreference = backreference;
	}

}
