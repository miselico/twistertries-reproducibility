package miselico.clusteringLSH.hashing.minHash;

import miselico.clusteringLSH.hashing.DownHasher;
import miselico.clusteringLSH.hashing.LSHFunctionFactory;
import miselico.clusteringLSH.hashing.RabinHasherFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashFunction;

public class MinHashFactory implements LSHFunctionFactory<ImmutableList<String>> {

	private final HashFunction f;

	public MinHashFactory() {
		//HashFunction f = Hashing.murmur3_128(46846547);
		long polynomial = (1 << 21) | (1 << 14) | (1 << 13) | (1 << 12) | (1 << 11) | (1 << 10) | (1 << 9) | (1 << 8) | (1 << 7) | (1 << 6) | (1 << 4) | (1 << 3) | (1 << 2) | (1 << 1) | (1 << 0);
		this.f = new RabinHasherFunction(polynomial);
	}

	@Override
	public DownHasher<ImmutableList<String>> getLSHFunction(long seed) {
		return new MinHasher(this.f, seed);
	}
}
