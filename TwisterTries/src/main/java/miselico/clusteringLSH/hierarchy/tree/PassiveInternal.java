package miselico.clusteringLSH.hierarchy.tree;

public class PassiveInternal implements PassiveElement {
	final private PassiveElement left;
	final private PassiveElement right;

	public PassiveInternal(PassiveElement left, PassiveElement right) {
		super();
		this.left = left;
		this.right = right;
	}

	public PassiveElement getRight() {
		return this.right;
	}

	public PassiveElement getLeft() {
		return this.left;
	}

}
