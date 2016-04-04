package miselico.clusteringLSH.hashing.RHH;

import java.util.Random;

import miselico.clusteringLSH.hashing.DownHasher;
import miselico.clusteringLSH.hashing.LSHFunctionFactory;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;

public class RHHFloatFactory implements LSHFunctionFactory<double[]> {

	private final int dim;

	public RHHFloatFactory(int dimension) {
		this.dim = dimension;
	}

	static final HashCode positive = HashCode.fromLong(1);
	static final HashCode negative = HashCode.fromLong(-1);

	@Override
	public DownHasher<double[]> getLSHFunction(final long seed) {

		return new DownHasher<double[]>() {

			final double[] v = new double[RHHFloatFactory.this.dim];

			{
				Random r = new Random(seed);
				for (int i = 0; i < this.v.length; i++) {
					this.v[i] = (r.nextDouble() - 0.5);
				}
			}

			@Override
			public HashCode hash(double[] set) {
				Preconditions.checkArgument(set.length == RHHFloatFactory.this.dim);
				double dotproduct = 0;
				for (int i = 0; i < this.v.length; i++) {
					double x_i = set[i];
					dotproduct += x_i * this.v[i];
				}
				if (dotproduct > 0) {
					return RHHFloatFactory.positive;
				} else {
					return RHHFloatFactory.negative;
				}
			}
		};
	}
}
