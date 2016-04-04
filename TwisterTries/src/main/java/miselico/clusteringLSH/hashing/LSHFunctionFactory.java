package miselico.clusteringLSH.hashing;

public interface LSHFunctionFactory<E> {
	DownHasher<E> getLSHFunction(long seed);
}
