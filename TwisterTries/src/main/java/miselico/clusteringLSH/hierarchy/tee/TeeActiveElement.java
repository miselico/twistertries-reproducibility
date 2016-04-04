package miselico.clusteringLSH.hierarchy.tee;

import miselico.clusteringLSH.hierarchy.AbstractActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveLeaf;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;
import miselico.clusteringLSH.hierarchy.Leaf;
import miselico.clusteringLSH.hierarchy.TrieLeafRecord;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class TeeActiveElement<E> extends AbstractActiveElement<E> implements ActiveElement<E>, ActiveLeaf<E> {

	protected ActiveElement<E> l;
	protected ActiveElement<E> r;

	public TeeActiveElement(TeeActiveElement<E> la, TeeActiveElement<E> ra, HierarchyFactory<E> l, HierarchyFactory<E> r) {
		super(Lists.<TrieLeafRecord<E>> newLinkedList());
		this.l = l.merge(la.l, ra.l);
		this.r = r.merge(la.r, ra.r);
	}

	@Override
	public int getValueCount() {
		return this.l.getValueCount();
	}

	@Override
	public String getID() {
		return String.format("TEE(%s, %s)", this.l.toString(), this.r.toString());
	}

	public TeeActiveElement(String iD, E content, HierarchyFactory<E> l, HierarchyFactory<E> r) {
		super(Lists.<TrieLeafRecord<E>> newLinkedList());
		this.l = l.createLeaf(iD, content);
		this.r = r.createLeaf(iD, content);
	}

	@Override
	public E getContent() {
		Preconditions.checkState((this.l instanceof Leaf) && (this.r instanceof Leaf));
		try {
			@SuppressWarnings("unchecked")
			Leaf<E> asLeaf = (Leaf<E>) this.l;
			return asLeaf.getContent();
		} catch (UnsupportedOperationException e) {
			@SuppressWarnings("unchecked")
			Leaf<E> asLeaf = (Leaf<E>) this.r;
			return asLeaf.getContent();
		}
	}
}
