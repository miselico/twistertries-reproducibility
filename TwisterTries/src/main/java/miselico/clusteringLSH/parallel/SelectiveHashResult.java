package miselico.clusteringLSH.parallel;

import java.util.BitSet;
import java.util.Iterator;

import miselico.clusteringLSH.management.HashCodeProvider;

import com.google.common.collect.AbstractIterator;

public class SelectiveHashResult<E> implements HashResult<E> {

	private final HashResult<E> wrapped;
	private final BitSet enabled;

	public SelectiveHashResult(HashResult<E> wrapped, BitSet enabled) {
		this.wrapped = wrapped;
		this.enabled = enabled;
	}

	@Override
	public Iterator<HashCodeProvider> iterator() {
		return new AbstractIterator<HashCodeProvider>() {

			int position = 0;

			Iterator<HashCodeProvider> iterator = SelectiveHashResult.this.wrapped.iterator();

			@Override
			protected HashCodeProvider computeNext() {
				while (this.position < SelectiveHashResult.this.enabled.length()) {
					boolean active = SelectiveHashResult.this.enabled.get(this.position);
					HashCodeProvider element = this.iterator.next();
					this.position++;
					if (active) {
						return element;
					}
				}
				this.endOfData();
				return null;
			}
		};
	}

	@Override
	public String getID() {
		return this.wrapped.getID();
	}

	@Override
	public E getContent() {
		return this.wrapped.getContent();
	}

}
