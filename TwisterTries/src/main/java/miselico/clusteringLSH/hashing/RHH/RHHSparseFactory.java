package miselico.clusteringLSH.hashing.RHH;

import java.util.Random;

import miselico.clusteringLSH.hashing.DownHasher;
import miselico.clusteringLSH.hashing.LSHFunctionFactory;
import miselico.clusteringLSH.input.SparseVector;

import com.google.common.hash.HashCode;

public class RHHSparseFactory implements LSHFunctionFactory<SparseVector> {

	private final int dim;

	public RHHSparseFactory(int dimension) {
		this.dim = dimension;
	}

	static final HashCode positive = HashCode.fromLong(1);
	static final HashCode negative = HashCode.fromLong(-1);

	@Override
	public DownHasher<SparseVector> getLSHFunction(final long seed) {

		return new DownHasher<SparseVector>() {
			// AtomicLong positives = new AtomicLong(1);
			// AtomicLong negatives = new AtomicLong(1);

			final int[] v = new int[RHHSparseFactory.this.dim];

			{
				Random r = new Random(seed);
				for (int i = 0; i < this.v.length; i++) {
					this.v[i] = r.nextInt();
				}
			}

			@Override
			public HashCode hash(SparseVector b) {
				//Preconditions.checkArgument(set.length == RHHFactory.this.dim);

				long dotproduct = b.dotProduct(this.v);
				if (dotproduct > 0) {
					// System.out.println(this.v.toString() + " " + (((double)
					// this.positives.incrementAndGet()) /
					// this.negatives.get()));

					return RHHFactory.positive;
				} else {
					// System.out.println(this.v.toString() + " " + (((double)
					// this.positives.get()) /
					// this.negatives.incrementAndGet()));

					return RHHFactory.negative;
				}
			}
		};
	}
}
