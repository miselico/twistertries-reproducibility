package miselico.clusteringLSH.hashing;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.TreeSet;

import com.google.common.hash.AbstractStreamingHashFunctionMadePublic;
import com.google.common.hash.AbstractStreamingHasherMadePublic;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

/**
 * Much of this code is inspired or even copied directly from https://github.com/themadcreator/rabinfingerprint
 * 
 * @author michael
 * 
 */
public class CopyOfRabinHasherFunction extends AbstractStreamingHashFunctionMadePublic {

	private final long[] pushTable;
	private final int degree;
	private final int shift;

	public CopyOfRabinHasherFunction(long polynomial) {
		Polynomial poly = Polynomial.createFromLong(polynomial);
		this.pushTable = CopyOfRabinHasherFunction.precomputePushTable(poly);
		this.degree = poly.degree().intValue();
		this.shift = this.degree - 8;
	}

	private static long[] precomputePushTable(Polynomial p) {
		long[] thePushTable = new long[512];
		for (int i = 0; i < 512; i++) {
			Polynomial f = Polynomial.createFromLong(i);
			f = f.shiftLeft(p.degree());
			f = f.xor(f.mod(p));
			thePushTable[i] = f.toBigInteger().longValue();
		}
		return thePushTable;
	}

	private static int hashBufferSize = 4;

	@Override
	public Hasher newHasher(int expectedInputSize) {
		return new RabinHasher(expectedInputSize);
	}

	@Override
	public Hasher newHasher() {
		return new RabinHasher(CopyOfRabinHasherFunction.hashBufferSize);
	}

	@Override
	public int bits() {
		return this.degree;
	}

	private final class RabinHasher extends AbstractStreamingHasherMadePublic {
		private long fingerprint = 0;

		private RabinHasher(int arg0) {
			super(arg0);
		}

		@Override
		protected void process(ByteBuffer bb) {
			//there will be hashBufferSize bytes in the buffer
			int rem = bb.remaining();
			for (int i = 0; i < rem; i++) {
				this.pushByte(bb.get());
			}
		}

		@Override
		protected void processRemaining(ByteBuffer bb) {
			// this could be overriden if we do things differently for non
			// full bytebuffers
			int rem = bb.remaining();
			for (int i = 0; i < rem; i++) {
				this.pushByte(bb.get());
			}

		}

		@Override
		public HashCode makeHash() {
			return HashCode.fromLong(this.fingerprint);
		}

		private void pushByte(byte b) {
			int j = (int) ((this.fingerprint >> CopyOfRabinHasherFunction.this.shift) & 0x1FF);
			this.fingerprint = ((this.fingerprint << 8) | (b & 0xFF)) ^ CopyOfRabinHasherFunction.this.pushTable[j];
		}
	}

	/**
	 * An immutable polynomial in the finite field GF(2^k)
	 * 
	 * Removed unused parts.
	 */
	private static class Polynomial implements Comparable<Polynomial> {

		/** a reverse comparator so that polynomials are printed out correctly */
		private static final class ReverseComparator implements Comparator<BigInteger> {
			@Override
			public int compare(BigInteger o1, BigInteger o2) {
				return -1 * o1.compareTo(o2);
			}
		}

		/**
		 * Constructs a polynomial using the bits from a long. Note that Java does not support unsigned longs.
		 */
		public static Polynomial createFromLong(long l) {
			TreeSet<BigInteger> dgrs = Polynomial.createDegreesCollection();
			for (int i = 0; i < 64; i++) {
				if (((l >> i) & 1) == 1) {
					dgrs.add(BigInteger.valueOf(i));
				}
			}
			return new Polynomial(dgrs);
		}

		/**
		 * A (sorted) set of the degrees of the terms of the polynomial. The sortedness helps quickly compute the degree as well as print out the terms in order. The O(nlogn) performance of insertions
		 * and deletions might actually hurt us, though, so we might consider moving to a HashSet
		 */
		private final TreeSet<BigInteger> degrees;

		/**
		 * Construct a new polynomial from a collection of degrees
		 */
		private Polynomial(TreeSet<BigInteger> degrees) {
			this.degrees = degrees;
		}

		/**
		 * Factory for create the degrees collection.
		 */
		protected static TreeSet<BigInteger> createDegreesCollection() {
			return new TreeSet<BigInteger>(new ReverseComparator());
		}

		/**
		 * Factory for create the copy of current degrees collection.
		 */
		@SuppressWarnings("unchecked")
		protected TreeSet<BigInteger> createDegreesCollectionCopy() {
			return (TreeSet<BigInteger>) this.degrees.clone();
		}

		/**
		 * Returns the degree of the highest term or -1 otherwise.
		 */
		public BigInteger degree() {
			if (this.degrees.isEmpty()) {
				return BigInteger.ONE.negate();
			}
			return this.degrees.first();
		}

		/**
		 * Tests if the polynomial is empty, i.e. it has no terms
		 */
		public boolean isEmpty() {
			return this.degrees.isEmpty();
		}

		/**
		 * Computes (this ^ that) in GF(2^k)
		 */
		public Polynomial xor(Polynomial that) {
			TreeSet<BigInteger> dgrs0 = this.createDegreesCollectionCopy();
			dgrs0.removeAll(that.degrees);
			TreeSet<BigInteger> dgrs1 = that.createDegreesCollectionCopy();
			dgrs1.removeAll(this.degrees);
			dgrs1.addAll(dgrs0);
			return new Polynomial(dgrs1);
		}

		/**
		 * Computes (this mod that) in GF(2^k) using synthetic division
		 */
		public Polynomial mod(Polynomial that) {
			BigInteger da = this.degree();
			BigInteger db = that.degree();
			Polynomial register = new Polynomial(this.degrees);
			for (BigInteger i = da.subtract(db); i.compareTo(BigInteger.ZERO) >= 0; i = i.subtract(BigInteger.ONE)) {
				if (register.hasDegree(i.add(db))) {
					Polynomial shifted = that.shiftLeft(i);
					register = register.xor(shifted);
				}
			}
			return register;
		}

		/**
		 * Computes (this << shift) in GF(2^k)
		 */
		public Polynomial shiftLeft(BigInteger shift) {
			TreeSet<BigInteger> dgrs = Polynomial.createDegreesCollection();
			for (BigInteger degree : this.degrees) {
				BigInteger shifted = degree.add(shift);
				dgrs.add(shifted);
			}
			return new Polynomial(dgrs);
		}

		/**
		 * Tests if there exists a term with degree k
		 */
		public boolean hasDegree(BigInteger k) {
			return this.degrees.contains(k);
		}

		/**
		 * Construct a BigInteger whose value represents this polynomial. This can lose information if the degrees of the terms are larger than Integer.MAX_VALUE;
		 */
		public BigInteger toBigInteger() {
			BigInteger b = BigInteger.ZERO;
			for (BigInteger degree : this.degrees) {
				b = b.setBit((int) degree.longValue());
			}
			return b;
		}

		/**
		 * Returns standard ascii representation of this polynomial in the form:
		 * 
		 * e.g.: x^8 + x^4 + x^3 + x + 1
		 */
		public String toPolynomialString() {
			StringBuffer str = new StringBuffer();
			for (BigInteger degree : this.degrees) {
				if (str.length() != 0) {
					str.append(" + ");
				}
				if (degree.compareTo(BigInteger.ZERO) == 0) {
					str.append("1");
				} else {
					str.append("x^" + degree);
				}
			}
			return str.toString();
		}

		/**
		 * Default toString override uses the ascii representation
		 */
		@Override
		public String toString() {
			return this.toPolynomialString();
		}

		/**
		 * Compares this polynomial to the other
		 */
		@Override
		public int compareTo(Polynomial o) {
			int cmp = this.degree().compareTo(o.degree());
			if (cmp != 0) {
				return cmp;
			}
			// get first degree difference
			Polynomial x = this.xor(o);
			if (x.isEmpty()) {
				return 0;
			}
			return this.hasDegree(x.degree()) ? 1 : -1;
		}
	}

}
