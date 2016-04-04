package miselico.clusteringLSH.hierarchy.tee;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveLeaf;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;

import com.google.common.base.Preconditions;

public class TeeHierarchyFactory<E> implements HierarchyFactory<E> {

	private final HierarchyFactory<E> l;
	private final HierarchyFactory<E> r;

	public TeeHierarchyFactory(HierarchyFactory<E> left, HierarchyFactory<E> right) {
		this.l = left;
		this.r = right;
	}

	@Override
	public ActiveLeaf<E> createLeaf(String ID, E content) {
		return new TeeActiveElement<E>(ID, content, this.l, this.r);
	}

	@Override
	public ActiveElement<E> merge(ActiveElement<E> left, ActiveElement<E> right) {
		Preconditions.checkArgument(left instanceof TeeActiveElement);
		Preconditions.checkArgument(right instanceof TeeActiveElement);
		TeeActiveElement<E> la = (TeeActiveElement<E>) left;
		TeeActiveElement<E> ra = (TeeActiveElement<E>) right;
		return new TeeActiveElement<E>(la, ra, this.l, this.r);
	}

}
