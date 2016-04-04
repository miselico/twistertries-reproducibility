package miselico.clusteringLSH.hierarchy.printing;

import miselico.clusteringLSH.hierarchy.AbstractActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.TrieLeafRecord;

import com.google.common.collect.Lists;

public class PrintingActiveElement<E> extends AbstractActiveElement<E> implements ActiveElement<E> {

	private final int count;
	private final String id;

	public PrintingActiveElement(int valueCount, String id) {
		super(Lists.<TrieLeafRecord<E>> newLinkedList());
		this.count = valueCount;
		this.id = id;
	}

	@Override
	public int getValueCount() {
		return this.count;
	}

	public String getID() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.getID();
	};
}
