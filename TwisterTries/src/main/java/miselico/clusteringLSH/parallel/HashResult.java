package miselico.clusteringLSH.parallel;

import miselico.clusteringLSH.management.HashCodeProvider;

public interface HashResult<E> extends Iterable<HashCodeProvider> {

	public String getID();

	public E getContent();

}
