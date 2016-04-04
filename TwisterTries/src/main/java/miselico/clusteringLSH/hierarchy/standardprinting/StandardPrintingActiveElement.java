package miselico.clusteringLSH.hierarchy.standardprinting;

import miselico.clusteringLSH.hierarchy.AbstractActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.TrieLeafRecord;

import com.google.common.collect.Lists;

public class StandardPrintingActiveElement<E> extends AbstractActiveElement<E> implements ActiveElement<E> {

	private final int count;
	private final int id;

	public StandardPrintingActiveElement(int valueCount, int id) {
		super(Lists.<TrieLeafRecord<E>> newLinkedList());
		this.count = valueCount;
		this.id = id;
	}

	@Override
	public int getValueCount() {
		return this.count;
	}

	public String getID() {
		return Integer.toString(this.id);
	}

	public int getIntID() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.getID();
	}

}
