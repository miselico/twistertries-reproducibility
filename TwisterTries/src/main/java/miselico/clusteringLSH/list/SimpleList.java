package miselico.clusteringLSH.list;

import java.util.ArrayList;
import java.util.List;

public class SimpleList<T> {

	private final ListNode<T> sentinel = new Sentinel<T>();

	private static class Sentinel<U> extends ListNode<U> {

		@Override
		public U getValue() {
			throw new Error("No methods shoudl be called on the sentinel");
		}

		@Override
		public boolean isLonelyNode() {
			throw new Error("No methods shoudl be called on the sentinel");
		}

		@Override
		public void removeSelf() {
			throw new Error("No methods shoudl be called on the sentinel");
		}
	}

	public static <U> SimpleList<U> create() {
		return new SimpleList<>();
	}

	private SimpleList() {
		this.sentinel.next = this.sentinel;
		this.sentinel.previous = this.sentinel;
	}

	public void pushBack(ListNode<T> node) {
		if ((node.next != null) || (node.previous != null)) {
			throw new Error("Trying to append a node which is already part of a list.");
		}
		ListNode<T> currentLast = this.sentinel.previous;
		currentLast.next = node;
		node.previous = currentLast;
		node.next = this.sentinel;
		this.sentinel.previous = node;
	}

	public ListNode<T> getFirst() {
		if (this.sentinel.next == this.sentinel) {
			throw new Error("Requested first from empty list.");
		}
		return this.sentinel.next;
	}

	public ListNode<T> getSecond() {
		if (this.sentinel.next.next == this.sentinel) {
			throw new Error("Requested second element, but list has less.");
		}
		return this.sentinel.next.next;
	}

	public boolean isEmpty() {
		return this.sentinel.next == this.sentinel;
	}

	public String showAll() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		ListNode<T> current = this.sentinel.next;
		while (current != this.sentinel) {
			b.append(current.getValue());
			b.append(",");
			current = current.next;
		}
		b.append("]");
		return b.toString();
	}

	public ListLength length() {
		ListNode<T> first = this.sentinel.next;
		if (first == this.sentinel) {
			return ListLength.ZERO;
		}
		ListNode<T> second = first.next;
		if (second == this.sentinel) {
			return ListLength.ONE;
		}
		//ListNode<T> third = second.next;
		//if (third == this.sentinel){
		return ListLength.MORE_AS_ONE;
		//}

	}

	/**
	 * This method is for debugging. It is a linear in the length of the list.
	 *
	 * @return
	 */
	public Object[] asArray() {

		List<T> arrayList = new ArrayList<>();
		ListNode<T> current = this.sentinel.next;
		while (current != this.sentinel) {
			arrayList.add(current.getValue());
			current = current.next;
		}
		return arrayList.toArray();
	}

}
