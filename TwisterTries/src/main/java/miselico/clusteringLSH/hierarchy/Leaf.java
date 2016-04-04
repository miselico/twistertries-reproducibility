package miselico.clusteringLSH.hierarchy;


public interface Leaf<E> {

	String getID();

	/**
	 * Get the content stored in the leaf. Most implementations will throw an
	 * {@link UnsupportedOperationException}.
	 *
	 * @throws UnsupportedOperationException
	 *             Thrown when the leaf does not store any content.
	 * @return
	 */
	E getContent();
}
