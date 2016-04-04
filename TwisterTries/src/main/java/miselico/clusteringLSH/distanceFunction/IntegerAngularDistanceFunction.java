package miselico.clusteringLSH.distanceFunction;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public enum IntegerAngularDistanceFunction implements DistanceFunction<int[]> {

	f {
		@Override
		public double apply(int[] a, int[] b) {

			long sumACube = 0;
			long sumBCube = 0;
			long sumAB = 0;

			for (int i = 0; i < a.length; i++) {
				long a_i = a[i];
				long b_i = b[i];
				// sumACube = LongMath.checkedAdd(sumACube,
				// LongMath.checkedMultiply(a_i, a_i));
				// sumBCube = LongMath.checkedAdd(sumBCube,
				// LongMath.checkedMultiply(b_i, b_i));
				// sumAB = LongMath.checkedAdd(sumAB,
				// LongMath.checkedMultiply(a_i, b_i));

				sumACube += a_i * a_i;
				sumBCube += b_i * b_i;
				sumAB += a_i * b_i;
			}

			double sqSumACube = Math.sqrt(sumACube);
			double sqSumBCube = Math.sqrt(sumBCube);

			double distance = Math.acos(sumAB / (sqSumACube * sqSumBCube));
			return distance;
		}
	},

	fcached {
		@Override
		public double apply(int[] a, int[] b) {

			long sumAB = 0;

			for (int i = 0; i < a.length; i++) {
				long a_i = a[i];
				long b_i = b[i];
				sumAB += a_i * b_i;
			}

			double sqSumACube = IntegerAngularDistanceFunction.normCache.getUnchecked(new ContentWrapper(a));
			double sqSumBCube = IntegerAngularDistanceFunction.normCache.getUnchecked(new ContentWrapper(b));

			double distance = Math.acos(sumAB / (sqSumACube * sqSumBCube));
			return distance;
		}

	},

	// fcachedApproxCos{
	// @Override
	// public double apply(ImmutableList<byte[]> l, ImmutableList<byte[]> r) {
	//
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// private double approxACos(double angle){
	// float acos(float x) {
	// float negate = float(x < 0);
	// x = abs(x);
	// float ret = -0.0187293;
	// ret = ret * x;
	// ret = ret + 0.0742610;
	// ret = ret * x;
	// ret = ret - 0.2121144;
	// ret = ret * x;
	// ret = ret + 1.5707288;
	// ret = ret * sqrt(1.0-x);
	// ret = ret - 2 * negate * ret;
	// return negate * 3.14159265358979 + ret;
	// }
	//
	// }
	// }
	;

	public static void warmUpNormCache(List<int[]> vectors) {
		for (int[] is : vectors) {
			IntegerAngularDistanceFunction.normCache.getUnchecked(new ContentWrapper(is));
		}
	}

	/** Wraps the content for usage in the cache. We only want the cache to
	 * re-use the value if it is ==, not when it is equals(). The reason for
	 * this is that the computation of .equals is more expensive as a head on
	 * computation.
	 *
	 * @author michael */
	private static class ContentWrapper {
		int[] content;

		public ContentWrapper(int[] content) {
			Preconditions.checkNotNull(content);
			this.content = content;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this.content);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			ContentWrapper other = (ContentWrapper) obj;
			return this.content == other.content;
		}

	}

	private static LoadingCache<ContentWrapper, Double> normCache = CacheBuilder.newBuilder().concurrencyLevel(1).build(new CacheLoader<ContentWrapper, Double>() {

		@Override
		public Double load(ContentWrapper key) throws Exception {
			return IntegerAngularDistanceFunction.norm(key.content);
		}

	});

	//static int norms = 0;

	private static double norm(int[] a) {
		long sumACube = 0;
		for (int i = 0; i < a.length; i++) {
			long a_i = a[i];
			sumACube += a_i * a_i;
		}
		//IntegerAngularDistanceFunction.norms++;
		//System.out.println(IntegerAngularDistanceFunction.norms);
		return Math.sqrt(sumACube);
	}

}
