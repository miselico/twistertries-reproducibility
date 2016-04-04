package miselico.clusteringLSH.hierarchy.tree;

import miselico.clusteringLSH.hierarchy.Leaf;

public class PassiveLeaf<E> implements PassiveElement, Leaf<E> {

	private final String ID;

	public PassiveLeaf(String iD) {
		super();
		this.ID = iD;
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
