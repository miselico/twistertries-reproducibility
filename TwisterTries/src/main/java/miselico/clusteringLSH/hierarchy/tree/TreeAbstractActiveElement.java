package miselico.clusteringLSH.hierarchy.tree;

import miselico.clusteringLSH.hierarchy.AbstractActiveElement;
import miselico.clusteringLSH.hierarchy.TrieLeafRecord;

import com.google.common.collect.Lists;

public abstract class TreeAbstractActiveElement<E> extends AbstractActiveElement<E> {

	protected TreeAbstractActiveElement() {
		super(Lists.<TrieLeafRecord<E>> newLinkedList());
	}

	public abstract PassiveElement asPassive();

}
