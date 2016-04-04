package miselico.clusteringLSH.list;

public abstract class ListNode<T> {
	ListNode<T> next, previous;

	public abstract T getValue();

	public void removeSelf() {
		if ((this.next == null) || (this.previous == null)) {
			throw new Error("removal of disconnected node.");
		}

		this.previous.next = this.next;
		this.next.previous = this.previous;
		this.next = null;
		this.previous = null;
	}

	public boolean isLonelyNode() {
		//this implementation is a bit weird, but it works.
		//if both next and previous are of the same thing,
		//then that thing is the sentinel and this is the only node in the list
		if ((this.next == null) || (this.previous == null)) {
			throw new Error("isLonelyNode check on disconnected node");
		}
		return this.previous == this.next;
	}

}
