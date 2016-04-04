package miselico.clusteringLSH.hierarchy.tree;

public class ActiveInternal<E> extends TreeAbstractActiveElement<E> {
	private final PassiveElement left;
	private final PassiveElement right;
	private final int valueCount;

	public ActiveInternal(TreeAbstractActiveElement<E> left, TreeAbstractActiveElement<E> right) {
		super();
		this.left = left.asPassive();
		this.right = right.asPassive();
		this.valueCount = left.getValueCount() + right.getValueCount();
	}

	@Override
	public PassiveElement asPassive() {
		return new PassiveInternal(this.left, this.right);
	}

	@Override
	public int getValueCount() {
		return this.valueCount;
	}

	public PassiveElement getLeft() {
		return this.left;
	}

	public PassiveElement getRight() {
		return this.right;
	}

	//public abstract PassiveElement asPassive();

}
