package miselico.clusteringLSH.hierarchy.printing;

import miselico.clusteringLSH.hierarchy.ActiveLeaf;

public class PrintingLeaf<E> extends PrintingActiveElement<E> implements ActiveLeaf<E> {

	public PrintingLeaf(String id) {
		super(1, id);
	}

	@Override
	public E getContent() {
		throw new UnsupportedOperationException();
	}

}
