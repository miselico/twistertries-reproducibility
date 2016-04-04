package miselico.clusteringLSH.trie.childMap;

import miselico.clusteringLSH.trie.TrieInternalNode.PairOfChildren;
import miselico.clusteringLSH.trie.TrieNode;

import com.google.common.hash.HashCode;

/**
 * Most of the time, there are only one or two children, so overkill to make a
 * map for each node. This creates the map lazily First trying with one child
 *
 * @author michael
 *
 */
public class SingleElementChildMap<E> implements ChildMapInterface<E> {

	private HashCode firstH;
	private TrieNode<E> firstChild;

	private OverFlownChildMap<E> overflow() {
		//create map
		OverFlownChildMap<E> overflowMap = new OverFlownChildMap<>();
		// add stored element
		overflowMap.put(this.firstH, this.firstChild);
		//setting these to null prevents memory leaks AND makes the working of put correct.
		this.firstChild = null;
		this.firstH = null;
		return overflowMap;
	}

	//	private boolean hasOverFlown() {
	//		return this.overflowMap != null;
	//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * miselico.clusteringLSH.trie.ChildMapInterface#put(com.google.common.hash
	 * .HashCode, miselico.clusteringLSH.trie.TrieNode)
	 */
	@Override
	public ChildMapInterface<E> put(HashCode val, TrieNode<E> newChild) {
		if (this.firstChild != null) {
			OverFlownChildMap<E> newMap = this.overflow();
			newMap.put(val, newChild);
			return newMap;
		}
		this.firstH = val;
		this.firstChild = newChild;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * miselico.clusteringLSH.trie.ChildMapInterface#get(com.google.common.hash
	 * .HashCode)
	 */
	@Override
	public TrieNode<E> get(HashCode val) {
		if (HashCodeComparator.comp.compare(val, this.firstH) == 0) {
			return this.firstChild;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see miselico.clusteringLSH.trie.ChildMapInterface#getOnlyChild()
	 */
	@Override
	public TrieNode<E> getOnlyChild() {
		return this.firstChild;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see miselico.clusteringLSH.trie.ChildMapInterface#getTwoChildren()
	 */
	@Override
	public PairOfChildren<E> getTwoChildren() {
		throw new Error("A singleElementMap cannot have two children");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see miselico.clusteringLSH.trie.ChildMapInterface#size()
	 */
	@Override
	public int size() {
		if (this.firstChild != null) {
			return 1;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see miselico.clusteringLSH.trie.ChildMapInterface#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.firstChild == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * miselico.clusteringLSH.trie.ChildMapInterface#remove(com.google.common
	 * .hash.HashCode)
	 */
	@Override
	public Object remove(HashCode val) {
		if (HashCodeComparator.comp.compare(val, this.firstH) == 0) {
			TrieNode<E> oldVal = this.firstChild;
			this.firstChild = null;
			this.firstH = null;
			return oldVal;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * miselico.clusteringLSH.trie.ChildMapInterface#containsKey(com.google.
	 * common.hash.HashCode)
	 */
	@Override
	public boolean containsKey(HashCode val) {
		return HashCodeComparator.comp.compare(this.firstH, val) == 0;
	}

}
