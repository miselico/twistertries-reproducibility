package miselico.clusteringLSH.hierarchy;

import com.google.common.collect.ImmutableList;

public interface ActiveElement<E> extends Element {

	public abstract int getValueCount();

	public ImmutableList<TrieLeafRecord<E>> getRecords();

	public void pushRecord(TrieLeafRecord<E> record);
}
