package miselico.clusteringLSH.trie.childMap;

import java.util.Comparator;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;

public enum HashCodeComparator implements Comparator<HashCode> {
	comp;

	@Override
	public int compare(HashCode o1, HashCode o2) {
		if (o1 == o2) {
			return 0;
		}
		int bits = o1.bits();
		Preconditions.checkArgument(bits == o2.bits());
		if (bits <= 64) {
			return Long.compare(o1.asLong(), o2.asLong());
		}
		byte[] b1 = o1.asBytes();
		byte[] b2 = o2.asBytes();
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] < b2[i]) {
				return -1;
			} else if (b1[i] > b2[i]) {
				return 1;
			}
		}
		return 0;
	}

}
