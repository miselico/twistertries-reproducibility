package miselico.clusteringLSH.management;

import com.google.common.hash.HashCode;

public interface HashCodeProvider {

	boolean isEmpty();

	HashCode pop();

	int getAmountLeft();

	// boolean lastHashCode();

}
