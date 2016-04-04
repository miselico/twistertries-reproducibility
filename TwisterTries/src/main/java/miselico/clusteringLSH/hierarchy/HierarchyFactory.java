package miselico.clusteringLSH.hierarchy;

public interface HierarchyFactory<E> {
	/**
	 * Create a leaf node with this factory. Many implementations do not care
	 * about the content and will ignore it.
	 *
	 * @param ID
	 * @param content
	 * @return
	 */
	ActiveLeaf<E> createLeaf(String ID, E content);

	ActiveElement<E> merge(ActiveElement<E> left, ActiveElement<E> right);
}
