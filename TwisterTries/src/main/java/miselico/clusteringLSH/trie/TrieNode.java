package miselico.clusteringLSH.trie;

import miselico.clusteringLSH.list.ListNode;

import com.google.common.hash.HashCode;

public abstract class TrieNode<E> {

	public abstract boolean isRoot();

	public abstract boolean isLeaf();

	public abstract HashCode edgeFromParent();

	public abstract TrieInternalNode<E> getParent();

	private ListNode<TrieNode<E>> splitPoint;

	public boolean isSplitPoint() {
		return this.splitPoint != null;
	}

	public void setSplitPoint(ListNode<TrieNode<E>> node) {
		this.splitPoint = node;
	}

	public void removeSplitPoint() {
		this.splitPoint.removeSelf();
		this.splitPoint = null;
	}

	// public ListNode<TrieNode> getSplitPoint(){
	// return this.splitPoint;
	// }

}
