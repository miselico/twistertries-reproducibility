package miselico.clusteringLSH.hierarchy.tree;

import miselico.clusteringLSH.hierarchy.ActiveLeaf;

public class TreeActiveLeaf<E> extends TreeAbstractActiveElement<E> implements ActiveLeaf<E> {
	private final String ID;

	public TreeActiveLeaf(String ID) {
		this.ID = ID;
	}

	@Override
	public PassiveElement asPassive() {
		return new PassiveLeaf<E>(this.ID);
	}

	@Override
	public int getValueCount() {
		return 1;
	}

	@Override
	public String getID() {
		return this.ID;
	}

	@Override
	public E getContent() {
		throw new UnsupportedOperationException();
	}

}
