package miselico.clusteringLSH.trie;

import miselico.clusteringLSH.trie.childMap.ChildMapInterface;
import miselico.clusteringLSH.trie.childMap.SingleElementChildMap;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;

public class TrieInternalNode<E> extends TrieNode<E> {

	private final TrieInternalNode<E> parent;
	private ChildMapInterface<E> children;
	private final HashCode edgeFromParent;

	public TrieInternalNode(TrieInternalNode<E> parent, HashCode edgeFromParent) {
		this.parent = parent;
		//this.children = Maps.newHashMap();
		this.children = new SingleElementChildMap<>();
		this.edgeFromParent = edgeFromParent;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	public TrieNode<E> getChild(HashCode val) {
		return this.children.get(val);
	}

	public boolean hasChild(HashCode val) {
		return this.children.containsKey(val);
	}

	public void putChild(HashCode val, TrieNode<E> newChild) {
		this.children = this.children.put(val, newChild);
	}

	@Override
	public TrieInternalNode<E> getParent() {
		return this.parent;
	}

	@Override
	public HashCode edgeFromParent() {
		return this.edgeFromParent;
	}

	/**
	 * Removes the child with the given HashCode. Does not remove the
	 * splitMapEntry!!
	 */
	public void removeChild(HashCode val) {
		if (this.children.remove(val) == null) {
			throw new Error("Tried to remove a child which does not exist.");
		}
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public int getChildCount() {
		return this.children.size();
	}

	public PairOfChildren<E> getTwoChildren() {
		Preconditions.checkArgument(this.getChildCount() > 1, "Requested two children, but the node does not have that many.");
		return this.children.getTwoChildren();
	}

	public static class PairOfChildren<E> {
		public final TrieNode<E> child1;
		public final TrieNode<E> child2;

		public PairOfChildren(TrieNode<E> child1, TrieNode<E> child2) {
			this.child1 = child1;
			this.child2 = child2;
		}
	}

	public TrieNode<E> getOnlyChild() {
		Preconditions.checkArgument(this.getChildCount() == 1, "only child requested from a node which does not have exactly one child.");
		return this.children.getOnlyChild();
	}

}
