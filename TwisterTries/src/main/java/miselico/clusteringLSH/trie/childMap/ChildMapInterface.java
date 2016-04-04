package miselico.clusteringLSH.trie.childMap;

import miselico.clusteringLSH.trie.TrieInternalNode.PairOfChildren;
import miselico.clusteringLSH.trie.TrieNode;

import com.google.common.hash.HashCode;

public interface ChildMapInterface<E> {

	public abstract ChildMapInterface<E> put(HashCode val, TrieNode<E> newChild);

	public abstract TrieNode<E> get(HashCode val);

	public abstract TrieNode<E> getOnlyChild();

	public abstract PairOfChildren<E> getTwoChildren();

	public abstract int size();

	public abstract boolean isEmpty();

	public abstract Object remove(HashCode val);

	public abstract boolean containsKey(HashCode val);

}