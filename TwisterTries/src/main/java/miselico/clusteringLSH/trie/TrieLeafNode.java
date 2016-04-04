package miselico.clusteringLSH.trie;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.TrieLeafRecord;
import miselico.clusteringLSH.list.ListLength;
import miselico.clusteringLSH.list.ListNode;
import miselico.clusteringLSH.list.SimpleList;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;

public class TrieLeafNode<E> extends TrieNode<E> {

	private final TrieInternalNode<E> parent;
	private final HashCode edgeFromParent;

	public TrieLeafNode(TrieInternalNode<E> parent, HashCode edgeFromParent) {
		super();
		Preconditions.checkArgument(parent != null, "Each leaf must have a valid parent. Got null");
		Preconditions.checkArgument(edgeFromParent != null, "Each leaf must have a valid edge from parent. Got null");

		this.parent = parent;
		this.edgeFromParent = edgeFromParent;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	// @Override
	// public TrieNode getChild(HashCode val) {
	// throw new Error("Child of leaf requested.");
	// }

	@Override
	public TrieInternalNode<E> getParent() {
		return this.parent;
	}

	@Override
	public HashCode edgeFromParent() {
		return this.edgeFromParent;
	}

	private final SimpleList<ActiveElement<E>> connectedElements = SimpleList.create();

	public void attachElement(ActiveElement<E> e) {
		// if there is now only one element, there will soon be two
		Node<E> node = new Node<>(e);
		this.connectedElements.pushBack(node);
		TrieLeafRecord<E> record = new TrieLeafRecord<>(this, node);
		e.pushRecord(record);
	}

	public ActiveElement<E> getFirstElement() {
		return this.connectedElements.getFirst().getValue();
	}

	public ActiveElement<E> getSecondElement() {
		return this.connectedElements.getSecond().getValue();
	}

	public ActiveElement<E> getOnlyElement() {
		Preconditions.checkArgument(this.getElementCount() == ListLength.ONE, "only element requested from leave with not exactly one element.");
		return this.getFirstElement();
	}

	private static class Node<E> extends ListNode<ActiveElement<E>> {

		private final ActiveElement<E> e;

		public Node(ActiveElement<E> e) {
			this.e = e;
		}

		@Override
		public ActiveElement<E> getValue() {
			return this.e;
		}

		@Override
		public void removeSelf() {
			// FIXME remove this TrieLeafNode from the splitmap
			// TODO check conditions
			super.removeSelf();
		}

	}

	//	private int elementCount = 0;
	//
	//	public void increaseElementCount() {
	//		elementCount++;
	//	}
	//
	//	public void decreaseElementCount() {
	//		elementCount--;
	//	}
	//
	//	public int getElementCount() {
	//		return this.elementCount;
	//	}

	public ListLength getElementCount() {
		return this.connectedElements.length();
	}

}
