package miselico.clusteringLSH.distanceFunction;


public interface DistanceFunction<E> {
	double apply(E l, E r);
}
