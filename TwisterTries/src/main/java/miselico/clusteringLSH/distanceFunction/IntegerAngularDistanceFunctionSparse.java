package miselico.clusteringLSH.distanceFunction;

import miselico.clusteringLSH.input.SparseVector;

public enum IntegerAngularDistanceFunctionSparse implements DistanceFunction<SparseVector> {

	f {
		@Override
		public double apply(SparseVector a, SparseVector b) {
			return SparseVector.angle(a, b);
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

}
