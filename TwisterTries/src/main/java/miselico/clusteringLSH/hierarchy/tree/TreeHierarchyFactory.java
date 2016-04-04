package miselico.clusteringLSH.hierarchy.tree;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveLeaf;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;

import com.google.common.base.Preconditions;

public class TreeHierarchyFactory<E> implements HierarchyFactory<E> {

	@Override
	public ActiveLeaf<E> createLeaf(String ID, E content) {
		return new TreeActiveLeaf<E>(ID);
	}

	@Override
	public ActiveElement<E> merge(ActiveElement<E> left, ActiveElement<E> right) {
		Preconditions.checkArgument(left instanceof TreeAbstractActiveElement);
		Preconditions.checkArgument(right instanceof TreeAbstractActiveElement);
		return new ActiveInternal<E>((TreeAbstractActiveElement<E>) left, (TreeAbstractActiveElement<E>) right);
	}
}
