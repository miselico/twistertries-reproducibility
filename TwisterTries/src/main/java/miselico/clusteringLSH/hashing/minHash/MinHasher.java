package miselico.clusteringLSH.hashing.minHash;

import java.nio.charset.StandardCharsets;

import miselico.clusteringLSH.hashing.DownHasher;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;

public class MinHasher implements DownHasher<ImmutableList<String>> {

	private final HashFunction permuter;
	private final long salt;

	public MinHasher(HashFunction permuter, long salt) {
		super();
		this.permuter = permuter;
		this.salt = salt;
	}

	// Multiset<HashCode> seenCodes = HashMultiset.create();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * miselico.clusteringLSH.hashing.DownHasher#hash(com.google.common.collect
	 * .ImmutableList)
	 */
	@Override
	public HashCode hash(ImmutableList<String> set) {
		Preconditions.checkArgument(!set.isEmpty(), "Cannot minhash emtpy sets");
		HashCode smallestHashCode = null;
		long smallest = Long.MAX_VALUE;
		for (String word : set) {
			//byte[] bs = word.getBytes();
			Hasher hasher = this.permuter.newHasher(8 + word.length());
			hasher.putString(word, StandardCharsets.UTF_8);
			hasher.putLong(this.salt);
			// TODO the salt is added only at the start, it could be safer to
			// add it multiple times
			HashCode hc = hasher.hash();
			// this.seenCodes.add(hc);
			long asLong = hc.asLong();
			// smaller than would also work. Except in the extremely rare case
			// where hc.asLong == Long.MAX_VALUE
			if (asLong <= smallest) {
				smallestHashCode = hc;
				smallest = asLong;
			}
			// System.out.print(this.salt + "#");
			// for (byte b : bs) {
			// System.out.print(b);
			// System.out.print(" ");
			// }
			// System.out.println(" ! " + hc);
		}
		assert smallestHashCode != null : "The hashcode can never be null";
		return this.optimize(smallestHashCode);

	}

	// @Override
	// public String toString() {
	// Multiset<HashCode> copy = HashMultiset.create();
	// for (Entry<HashCode> entry : this.seenCodes.entrySet()) {
	// if (entry.getCount() > 50) {
	// copy.add(entry.getElement(), entry.getCount());
	// }
	// }
	//
	// return this.salt + " -> " + copy;
	// }

	private HashCode optimize(HashCode c) {
		return c;
	}

	// /**
	// * Trying whether we can speed up things by a) interning and b) keeping
	// the a copy of the byte array This is NOT static since it will be rare
	// that different minhashers have the same hashcodes.
	// *
	// * @param c
	// * @return
	// */
	// private HashCode optimize(HashCode c) {
	// byte[] asBytes = c.asBytes();
	// HashCode intern = this.interned.get(asBytes);
	// if (intern == null) {
	// intern = new BufferedHashCode(c, asBytes);
	// this.interned.put(asBytes, intern);
	// }
	// return intern;
	// }
	//
	// private final TreeMap<byte[], HashCode> interned = Maps.newTreeMap(new
	// Comparator<byte[]>() {
	//
	// @Override
	// public int compare(byte[] b1, byte[] b2) {
	// for (int i = 0; i < b1.length; i++) {
	// if (b1[i] < b2[i]) {
	// return -1;
	// } else if (b1[i] > b2[i]) {
	// return 1;
	// }
	// }
	// return 0;
	// }
	// });

}
