package miselico.clusteringLSH.management;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;
import miselico.clusteringLSH.hierarchy.TrieLeafRecord;
import miselico.clusteringLSH.list.ListLength;
import miselico.clusteringLSH.splitmap.SplitMap;
import miselico.clusteringLSH.trie.TrieInternalNode;
import miselico.clusteringLSH.trie.TrieLeafNode;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class RecursiveMerger<E> {

	private final Random rng;
	private final HierarchyFactory<E> hierarchyFactory;

	public RecursiveMerger(Random rng, HierarchyFactory<E> factory) {
		this.rng = rng;
		this.hierarchyFactory = factory;
	}

	private static class MergeAction<F> {
		private final ActiveElement<F> joinedElement;
		private final Combiner combiner;
		private TrieInternalNode<F> currentLeftNode;
		private TrieInternalNode<F> currentRightNode;
		private final SplitMap<F> map;

		private MergeAction(ActiveElement<F> joinedElement, Combiner combiner, TrieInternalNode<F> currentLeftNode, TrieInternalNode<F> currentRightNode, SplitMap<F> map) {
			super();
			this.joinedElement = joinedElement;
			this.combiner = combiner;
			this.currentLeftNode = currentLeftNode;
			this.currentRightNode = currentRightNode;
			this.map = map;
		}

		// the children are laready taken out of the current nodes
		private void mergeLeftRightRemoving() {
			if (this.currentLeftNode == this.currentRightNode) {
				int childcount = this.currentLeftNode.getChildCount();
				if ((childcount == 0) || (childcount == 1)) {
					// this node used to be a split point but is not any longer.
					this.currentLeftNode.removeSplitPoint();
				}
				RecursiveAdder.add(this.currentLeftNode, this.combiner, this.joinedElement, this.map);
				return;
			} else {
				// collect the
				// hashcodes along the way into the combiner
				this.combiner.push(this.currentLeftNode.edgeFromParent(), this.currentRightNode.edgeFromParent());

				if (this.currentLeftNode.getChildCount() == 1) {
					this.currentLeftNode.removeSplitPoint();
				}
				if (this.currentRightNode.getChildCount() == 1) {
					this.currentRightNode.removeSplitPoint();
				}

				boolean leftRemoving = !this.currentLeftNode.hasChildren();
				boolean rightRemoving = !this.currentRightNode.hasChildren();

				if (leftRemoving) {
					this.currentLeftNode.getParent().removeChild(this.currentLeftNode.edgeFromParent());
				}
				if (rightRemoving) {
					this.currentRightNode.getParent().removeChild(this.currentRightNode.edgeFromParent());
				}
				//FIXME it seems like the identity of currentLEFT/RIGHTNode should be changed to their respective patrents here. Likely also in othe methods.

				this.currentLeftNode = this.currentLeftNode.getParent();
				this.currentRightNode = this.currentRightNode.getParent();

				if (leftRemoving && rightRemoving) {
					this.mergeLeftRightRemoving();
				} else if (leftRemoving && !rightRemoving) {
					this.mergeLeftRemoving();
				} else if (!leftRemoving && rightRemoving) {
					this.mergeRightRemoving();
				} else if (!leftRemoving && !rightRemoving) {
					this.mergeNotRemoving();
				} else {
					throw new Error("Logical mistake");
				}

			}

		}

		private void mergeLeftRemoving() {
			if (this.currentLeftNode == this.currentRightNode) {
				int childcount = this.currentLeftNode.getChildCount();
				if ((childcount == 0) || (childcount == 1)) {
					// this node used to be a split point but is not any longer.
					this.currentLeftNode.removeSplitPoint();
				}
				RecursiveAdder.add(this.currentLeftNode, this.combiner, this.joinedElement, this.map);
				return;
			} else {
				// collect the
				// hashcodes along the way into the combiner
				this.combiner.push(this.currentLeftNode.edgeFromParent(), this.currentRightNode.edgeFromParent());

				if (this.currentLeftNode.getChildCount() == 1) {
					this.currentLeftNode.removeSplitPoint();
				}

				boolean leftRemoving = !this.currentLeftNode.hasChildren();

				if (leftRemoving) {
					this.currentLeftNode.getParent().removeChild(this.currentLeftNode.edgeFromParent());
				}

				this.currentLeftNode = this.currentLeftNode.getParent();
				this.currentRightNode = this.currentRightNode.getParent();
				if (leftRemoving) {
					this.mergeLeftRemoving();
				} else if (!leftRemoving) {
					this.mergeNotRemoving();
				} else {
					throw new Error("Logical mistake");
				}

			}

		}

		private void mergeRightRemoving() {
			if (this.currentLeftNode == this.currentRightNode) {
				int childcount = this.currentLeftNode.getChildCount();
				if ((childcount == 0) || (childcount == 1)) {
					// this node used to be a split point but is not any longer.
					this.currentLeftNode.removeSplitPoint();
				}
				RecursiveAdder.add(this.currentLeftNode, this.combiner, this.joinedElement, this.map);
				return;
			} else {
				// collect the
				// hashcodes along the way into the combiner
				this.combiner.push(this.currentLeftNode.edgeFromParent(), this.currentRightNode.edgeFromParent());

				if (this.currentRightNode.getChildCount() == 1) {
					this.currentRightNode.removeSplitPoint();
				}

				boolean rightRemoving = !this.currentRightNode.hasChildren();

				if (rightRemoving) {
					this.currentRightNode.getParent().removeChild(this.currentRightNode.edgeFromParent());
				}

				this.currentLeftNode = this.currentLeftNode.getParent();
				this.currentRightNode = this.currentRightNode.getParent();
				if (rightRemoving) {
					this.mergeRightRemoving();
				} else if (!rightRemoving) {
					this.mergeNotRemoving();
				} else {
					throw new Error("Logical mistake");
				}

			}

		}

		private void mergeNotRemoving() {
			while (this.currentLeftNode != this.currentRightNode) {
				// collect the hashcodes along the way into the combiner
				this.combiner.push(this.currentLeftNode.edgeFromParent(), this.currentRightNode.edgeFromParent());
				this.currentLeftNode = this.currentLeftNode.getParent();
				this.currentRightNode = this.currentRightNode.getParent();
			}
			RecursiveAdder.add(this.currentLeftNode, this.combiner, this.joinedElement, this.map);
		}

	}

	public void merge(ActiveElement<E> leftActive, ActiveElement<E> rightActive, Iterable<SplitMap<E>> splitMaps) {

		ActiveElement<E> joinedElement = this.hierarchyFactory.merge(leftActive, rightActive);
		double leftCount = leftActive.getValueCount();
		double rightCount = rightActive.getValueCount();
		final double leftRatio = leftCount / (leftCount + rightCount);
		ImmutableList<TrieLeafRecord<E>> leftRecords = leftActive.getRecords();
		ImmutableList<TrieLeafRecord<E>> rightRecords = rightActive.getRecords();
		if (leftRecords.size() != rightRecords.size()) {
			throw new AssertionError("The reference lists have to have the same size. This should be also equal to the number of trees.");
		}
		// for each trie
		// TODO if needed, this can be parallelized. The tries function
		// independently during merge

		Iterator<SplitMap<E>> splitMapsIter = splitMaps.iterator();
		for (int i = 0; i < leftRecords.size(); i++) {
			SplitMap<E> map = splitMapsIter.next();

			Combiner combiner = new Combiner(leftRatio, this.rng);
			// get the records for this trie
			TrieLeafRecord<E> leftRecord = leftRecords.get(i);
			TrieLeafRecord<E> rightRecord = rightRecords.get(i);
			// remove both references from the bottom of the trie
			leftRecord.backreference.removeSelf();
			rightRecord.backreference.removeSelf();

			RecursiveMerger.start(joinedElement, combiner, leftRecord.leaf, rightRecord.leaf, map);

		}
	}

	/**
	 * When start is called, it is assumed that the old elements are removed
	 * from the leafs.
	 *
	 * @param joinedElement
	 * @param combiner
	 * @param leftLeafNode
	 * @param rightLeafNode
	 * @param map
	 */
	private static <E> void start(ActiveElement<E> joinedElement, Combiner combiner, TrieLeafNode<E> leftLeafNode, TrieLeafNode<E> rightLeafNode, SplitMap<E> map) {
		// total shortcut: no trie nodes have to be removed.
		if (leftLeafNode == rightLeafNode) {
			ListLength count = leftLeafNode.getElementCount();
			if ((count == ListLength.ZERO) || (count == ListLength.ONE)) {
				// This node used to have two or three and was hence a splitpoint, this
				// node is not a splitpoint any longer.
				leftLeafNode.removeSplitPoint();
			}
			RecursiveAdder.addToLeaf(leftLeafNode, joinedElement, map);
			return;
		}

		RecursiveMerger.cleanLeafSplitPoint(leftLeafNode, map);
		RecursiveMerger.cleanLeafSplitPoint(rightLeafNode, map);

		// TODO check is this all?

		MergeAction<E> action = new MergeAction<>(joinedElement, combiner, leftLeafNode.getParent(), rightLeafNode.getParent(), map);

		combiner.push(leftLeafNode.edgeFromParent(), rightLeafNode.edgeFromParent());

		boolean leftRemoving = leftLeafNode.getElementCount() == ListLength.ZERO;
		boolean rightRemoving = rightLeafNode.getElementCount() == ListLength.ZERO;
		if (leftRemoving) {
			leftLeafNode.getParent().removeChild(leftLeafNode.edgeFromParent());
		}
		if (rightRemoving) {
			rightLeafNode.getParent().removeChild(rightLeafNode.edgeFromParent());
		}

		if (leftRemoving && rightRemoving) {
			action.mergeLeftRightRemoving();
		} else if (leftRemoving && !rightRemoving) {
			action.mergeLeftRemoving();
		} else if (!leftRemoving && rightRemoving) {
			action.mergeRightRemoving();
		} else if (!leftRemoving && !rightRemoving) {
			action.mergeNotRemoving();
		} else {
			throw new Error("Logical mistake");
		}

	}

	private static <E> void cleanLeafSplitPoint(TrieLeafNode<E> leaf, SplitMap<E> map) {
		if (leaf.getElementCount() == ListLength.ONE) {
			// this leaf used to have two and was hence a splitpoint, but is not
			// any longer
			leaf.removeSplitPoint();
		}
	}

	/**
	 * A storage for the hashcodes when climbing up, giving back based on the
	 * bias
	 *
	 * @author michael
	 *
	 */
	private static class Combiner implements HashCodeProvider {

		private final double biasToLeft;
		private final Deque<HashCode> stack = new ArrayDeque<>();
		private final Random rng;

		public Combiner(double biasToLeft, Random rng) {
			this.biasToLeft = biasToLeft;
			this.rng = rng;
		}

		private boolean leftShouldWin() {
			return this.rng.nextFloat() < this.biasToLeft;
		}

		public void push(HashCode left, HashCode right) {
			if (this.leftShouldWin()) {
				this.stack.push(left);
			} else {
				this.stack.push(right);
			}
		}

		@Override
		public HashCode pop() {
			HashCode val = this.stack.pop();
			if (val == null) {
				throw new Error("Element popped from stack is null. Can this even happen?");
			}
			return val;
		}

		// looks like this was used wrongly.
		// public boolean lastHashCode() {
		// return this.stack.size() == 1;
		// }

		@Override
		public boolean isEmpty() {
			return this.stack.isEmpty();
		}

		@Override
		public int getAmountLeft() {
			return this.stack.size();
		}

	}

}
