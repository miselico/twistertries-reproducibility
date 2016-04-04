package miselico.clusteringLSH.input;


public class InputSet<E> {
	public final E data;
	public final String id;

	/**
	 *
	 * @param id
	 * @param data
	 */
	//FIXME this has been diabled because an automatic copu is not made, checkm actual usage.
	public InputSet(String id, E data) {
		super();
		//Preconditions.checkArgument(!data.isEmpty(), "Empty sets are not allowed. the distance to the empty set is maximum for all non-empty sets and the first element of its permutation undefined.");
		this.data = data;
		this.id = id;
	}

	//	@Override
	//	public String toString() {
	//		StringBuilder b = new StringBuilder();
	//		b.append(this.id);
	//		b.append('[');
	//		for (byte[] val : this.data) {
	//			b.append(new String(val, StandardCharsets.UTF_8));
	//			b.append(',');
	//		}
	//		b.append(']');
	//
	//		return b.toString();
	//	}
}
