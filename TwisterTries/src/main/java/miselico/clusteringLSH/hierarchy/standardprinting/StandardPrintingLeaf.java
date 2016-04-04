package miselico.clusteringLSH.hierarchy.standardprinting;

import miselico.clusteringLSH.hierarchy.ActiveLeaf;

public class StandardPrintingLeaf<E> extends StandardPrintingActiveElement<E> implements ActiveLeaf<E> {

	public StandardPrintingLeaf(int leafCounter) {
		super(1, leafCounter);
	}

	@Override
	public E getContent() {
		throw new UnsupportedOperationException();
	}
}
