package miselico.clusteringLSH.trie.childMap;

import java.util.Iterator;
import java.util.Map;

import miselico.clusteringLSH.trie.TrieInternalNode.PairOfChildren;
import miselico.clusteringLSH.trie.TrieNode;

import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;

public class OverFlownChildMap<E> implements ChildMapInterface<E> {

	private final Map<HashCode, TrieNode<E>> overflowMap;

	public OverFlownChildMap() {
		this.overflowMap = Maps.newTreeMap(HashCodeComparator.comp);
	}

	@Override
	public ChildMapInterface<E> put(HashCode val, TrieNode<E> newChild) {
		if (this.overflowMap.put(val, newChild) != null) {
			throw new Error("Tried to put a child which already existed.");
		}
		return this;
	}

	@Override
	public TrieNode<E> get(HashCode val) {
		return this.overflowMap.get(val);
	}

	@Override
	public TrieNode<E> getOnlyChild() {
		return this.overflowMap.values().iterator().next();
	}

	@Override
	public PairOfChildren<E> getTwoChildren() {
		Iterator<TrieNode<E>> iter = this.overflowMap.values().iterator();
		return new PairOfChildren<>(iter.next(), iter.next());
	}

	@Override
	public int size() {
		return this.overflowMap.size();
	}

	@Override
	public boolean isEmpty() {
		return this.overflowMap.isEmpty();
	}

	@Override
	public Object remove(HashCode val) {
		return this.overflowMap.remove(val);
	}

	@Override
	public boolean containsKey(HashCode val) {
		return this.overflowMap.containsKey(val);
	}

}
