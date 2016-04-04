package miselico.clusteringLSH.hashing;

import com.google.common.hash.HashCode;

public interface DownHasher<E> {

	public abstract HashCode hash(E set);

}