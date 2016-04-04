package miselico.clusteringLSH.parallel;

import java.util.ArrayList;
import java.util.Iterator;

import miselico.clusteringLSH.management.HashCodeProvider;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.hash.HashCode;

/** This class simulates the bad case where a series of hash functions used to
 * create tries is exactly the same hash function. As a result, the splitpoint
 * will very often be at the bottom of the trie causing complete random
 * clustering.
 *
 * This class re-uses hash codes from the masked ones to create these new
 * HashCodeProviders. this ensures that the tries produced are really possible.
 *
 * @author michael
 *
 * @param <E> */
public class HashResultWithTerribleTries<E> implements HashResult<E> {

	private final HashResult<E> masked;
	private final int numberOfExtraTries;
	private final int treeHeight;

	public HashResultWithTerribleTries(HashResult<E> masked, int treeheight, int numberOfExtraTries) {
		Preconditions.checkArgument(treeheight >= numberOfExtraTries, "The current implementation uses the fact that the tree height is more as the number of extra tries needed. "
				+ "If this is a problem, the code of the iterator() method has to be revised.");
		this.masked = masked;
		this.numberOfExtraTries = numberOfExtraTries;
		this.treeHeight = treeheight;
	}

	@Override
	public Iterator<HashCodeProvider> iterator() {
		//NOTE: extraTries cannot be re-used anyhow since the CyclicHashCodeProvider is statefull!
		ArrayList<HashCodeProvider> extraTries = new ArrayList<HashCodeProvider>(this.numberOfExtraTries);
		HashCodeProvider providerOfFirstTrie = this.masked.iterator().next();
		for (int i = 0; i < this.numberOfExtraTries; i++) {
			assert !providerOfFirstTrie.isEmpty() : "This should never be true because of the precondition in the constructor";
			if (providerOfFirstTrie.isEmpty()) {
				throw new Error("This should never be true since there is a precondition which should prevent this.");
			}
			HashCode hc = providerOfFirstTrie.pop();
			extraTries.add(new CyclicHashcodeProvider(hc, this.treeHeight));
		}
		return Iterators.concat(extraTries.iterator(), this.masked.iterator());
	}

	@Override
	public String getID() {
		return this.masked.getID();
	}

	@Override
	public E getContent() {
		return this.masked.getContent();
	}

	private static class CyclicHashcodeProvider implements HashCodeProvider {

		private final HashCode value;
		private int amountLeft;

		public CyclicHashcodeProvider(HashCode value, int treeHeight) {
			super();
			this.value = value;
			this.amountLeft = treeHeight;
		}

		@Override
		public boolean isEmpty() {
			return this.amountLeft == 0;
		}

		@Override
		public HashCode pop() {
			if (this.isEmpty()) {
				throw new Error("Pop from empty CyclicHashcodeProvider");
			}
			this.amountLeft--;
			return this.value;
		}

		@Override
		public int getAmountLeft() {
			return this.amountLeft;
		}

	}

}
