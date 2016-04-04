package miselico.clusteringLSH.distanceFunction;

import java.util.HashSet;

import com.google.common.collect.ImmutableList;

public class JaccardDistanceFunc<E> implements DistanceFunction<ImmutableList<E>> {

	public static final JaccardDistanceFunc<String> f = new JaccardDistanceFunc<String>();

	@Override
	public double apply(ImmutableList<E> left, ImmutableList<E> right) {
		HashSet<E> leftSh = new HashSet<E>(left);
		HashSet<E> rightSh = new HashSet<E>(right);

		int leftSize = leftSh.size();
		int rightSize = rightSh.size();

		if (leftSize < rightSize) {
			// swap, this saves leftSize - rightSize contains() checks
			HashSet<E> temp = leftSh;
			leftSh = rightSh;
			rightSh = temp;
		}
		// these are the start values, the union might be overestimated since it
		// counts elements in the intersection twice
		int unionSize = leftSize + rightSize;
		int intersectionSize = 0;
		for (E t : leftSh) {
			if (rightSh.contains(t)) {
				intersectionSize++;
				// we counted one too much in the union (we counted the element
				// twice)
				unionSize--;
			}
		}
		double distJaccard = 1 - ((double) intersectionSize / (double) unionSize);
		return distJaccard;

	}

}
