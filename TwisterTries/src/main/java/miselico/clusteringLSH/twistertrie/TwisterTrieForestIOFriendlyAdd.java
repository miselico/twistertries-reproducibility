package miselico.clusteringLSH.twistertrie;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveLeaf;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;
import miselico.clusteringLSH.management.Driver;
import miselico.clusteringLSH.management.HashCodeProvider;
import miselico.clusteringLSH.parallel.HashResult;
import miselico.clusteringLSH.splitmap.SplitMap;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

/**
 * This class implements a Twister tries forest to which HashResults can be
 * added using a modified add function.
 *
 * The benefit of adding things this way is that reading and hashing the input
 * data does not have to be finished before first elements are added.
 *
 * Note: the adding and twisting do not themselves make use of
 * parallel/concurrent processing. An implementation of this idea is below, but
 * commented out since it does not work as it should.
 *
 *
 * @author michael
 *
 */
public class TwisterTrieForestIOFriendlyAdd<E> {

	private final ImmutableList<TwisterTrie<E>> tries;
	private final int trieHeight;
	private final HierarchyFactory<E> factory;

	public TwisterTrieForestIOFriendlyAdd(ForestConfig config, HierarchyFactory<E> factory) {
		this.factory = factory;
		int trieHeight = config.getTrieheight();
		int numberOfTries = config.getNumberOfTries();
		this.trieHeight = trieHeight;

		Builder<TwisterTrie<E>> twisterTreeListBuilder = ImmutableList.builder();

		for (int i = 0; i < numberOfTries; i++) {
			twisterTreeListBuilder.add(new TwisterTrie<E>(trieHeight));
		}
		this.tries = twisterTreeListBuilder.build();
	}

	/**
	 * Adds the {@link HashResult}s in the futureResuls one by one to the
	 * twister tries.
	 *
	 * note: this operation will not add the elements concurrently/parallel to
	 * multiple tries!
	 *
	 * @param futureResults
	 */
	public void add(Iterable<Future<HashResult<E>>> futureResults) {

		for (Future<HashResult<E>> future : futureResults) {
			HashResult<E> hr;
			try {
				hr = future.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new Error(e);
			}
			this.add(hr);
		}

	}

	public void add(HashResult<E> hr) {
		Iterator<HashCodeProvider> hri = hr.iterator();
		ActiveLeaf<E> leaf = this.factory.createLeaf(hr.getID(), hr.getContent());
		//System.out.println(leaf.getID());
		for (TwisterTrie<E> trie : this.tries) {
			HashCodeProvider provider = hri.next();
			trie.add(leaf, provider);
		}
	}

	// It was not this simple to make things thread safe.
	// No time right now to figure out what went wrong.
	//	private final ExecutorService executor;
	//
	//	public void addMultiThread(String ID, final ImmutableList<byte[]> set) {
	//		final ThreadSafeActiveLeaf leaf = new ThreadSafeActiveLeaf(ID);
	//		//The tries
	//		List<Future<?>> jobs = Lists.newArrayListWithCapacity(this.tries.size());
	//		for (final TwisterTrie trie : this.tries) {
	//			jobs.add(this.executor.submit(new Callable<Void>() {
	//
	//				@Override
	//				public Void call() throws Exception {
	//					trie.add(leaf, set);
	//					return null;
	//				}
	//			}));
	//		}
	//		for (Future<?> job : jobs) {
	//			try {
	//				job.get();
	//			} catch (InterruptedException | ExecutionException e) {
	//				throw new Error(e);
	//			}
	//		}
	//	}

	public ActiveElement<E> twist(Random rng) {

		Iterable<SplitMap<E>> splitMaps = Iterables.transform(this.tries, new Function<TwisterTrie<E>, SplitMap<E>>() {

			@Override
			public SplitMap<E> apply(TwisterTrie<E> trie) {
				return trie.getSplitMap();
			}

		});

		Driver.cluster(splitMaps, this.trieHeight, this.factory, rng);
		//get the activeElemtn from the bottom of the twistertrie

		return this.tries.get(0).getResult();
	}
}
