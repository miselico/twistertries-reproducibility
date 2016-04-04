package miselico.clusteringLSH.hierarchy;

import java.util.List;

import com.google.common.collect.ImmutableList;

public abstract class AbstractActiveElement<E> implements ActiveElement<E> {

	private final List<TrieLeafRecord<E>> records;

	protected AbstractActiveElement(List<TrieLeafRecord<E>> records) {
		this.records = records;
	}

	@Override
	public ImmutableList<TrieLeafRecord<E>> getRecords() {
		return ImmutableList.copyOf(this.records);
	}

	@Override
	public void pushRecord(TrieLeafRecord<E> record) {
		this.records.add(record);
	}
}
