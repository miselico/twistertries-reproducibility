package miselico.clusteringLSH.hashing.RHH;

import java.util.Random;

import miselico.clusteringLSH.hashing.DownHasher;
import miselico.clusteringLSH.hashing.LSHFunctionFactory;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.math.LongMath;

public class RHHFactory implements LSHFunctionFactory<int[]> {

	private final int dim;

	public RHHFactory(int dimension) {
		this.dim = dimension;
	}

	static final HashCode positive = HashCode.fromLong(1);
	static final HashCode negative = HashCode.fromLong(-1);

	@Override
	public DownHasher<int[]> getLSHFunction(final long seed) {

		return new DownHasher<int[]>() {
			// AtomicLong positives = new AtomicLong(1);
			// AtomicLong negatives = new AtomicLong(1);

			final int[] v = new int[RHHFactory.this.dim];

			{
				Random r = new Random(seed);
				for (int i = 0; i < this.v.length; i++) {
					this.v[i] = r.nextInt();
				}
			}

			@Override
			public HashCode hash(int[] set) {
				Preconditions.checkArgument(set.length == RHHFactory.this.dim);
				long dotproduct = 0;
				for (int i = 0; i < this.v.length; i++) {
					// long x_i = Ints.fromByteArray(set.get(i));
					long x_i = set[i];
					dotproduct += x_i * this.v[i];
					assert dotproduct == LongMath.checkedAdd(dotproduct, LongMath.checkedMultiply(x_i, this.v[i]));

				}
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
