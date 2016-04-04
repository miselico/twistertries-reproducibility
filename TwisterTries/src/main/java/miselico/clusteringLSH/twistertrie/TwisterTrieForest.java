package miselico.clusteringLSH.twistertrie;

import java.util.Random;

import miselico.clusteringLSH.hashing.DownHasher;
import miselico.clusteringLSH.hashing.LSHFunctionFactory;
import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveLeaf;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;
import miselico.clusteringLSH.management.Driver;
import miselico.clusteringLSH.management.HashCodeProvider;
import miselico.clusteringLSH.splitmap.SplitMap;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;

/**
 * This was the first imnplementation of the Twister Trie forest.
 * {@link TwisterTrieForestIOFriendlyAdd} sould likely be used in all cases
 * since it is better maintained and tested.
 * 
 * @author michael
 *
 * @param <E>
 */
public class TwisterTrieForest<E> {

	private final ImmutableList<TwisterTrie<E>> tries;
	private final ImmutableList<ImmutableList<DownHasher<E>>> allHashers;
	private final int trieHeight;
	private final HierarchyFactory<E> factory;

	public TwisterTrieForest(ForestConfig config, HierarchyFactory<E> factory, LSHFunctionFactory<E> lshFact) {
		this.factory = factory;
		int trieHeight = config.getTrieheight();
		int numberOfTries = config.getNumberOfTries();
		this.trieHeight = trieHeight;

		Builder<TwisterTrie<E>> twisterTreeListBuilder = ImmutableList.builder();
		Builder<ImmutableList<DownHasher<E>>> allHashersBuilder = ImmutableList.builder();

		for (int i = 0; i < numberOfTries; i++) {
			Builder<DownHasher<E>> trieHashers = ImmutableList.builder();
			for (int j = 0; j < trieHeight; j++) {
				//Adding 1 to i and j is needed because otherwise a lot of salts will be 0
				trieHashers.add(lshFact.getLSHFunction((i + 1) * (j + 1) * 4654687871111254598L));
			}

			allHashersBuilder.add(trieHashers.build());
			twisterTreeListBuilder.add(new TwisterTrie<E>(trieHeight));

		}
		this.tries = twisterTreeListBuilder.build();
		this.allHashers = allHashersBuilder.build();
		//	this.executor = Executors.newFixedThreadPool(config.getNumberOfTries());
	}

	public void add(String ID, final E set) {
		ActiveLeaf<E> leaf = this.factory.createLeaf(ID, set);
		int trieCounter = 0;
		for (TwisterTrie<E> trie : this.tries) {
			final ImmutableList<DownHasher<E>> hashers = this.allHashers.get(trieCounter);
			HashCodeProvider provider = new HashCodeProvider() {

				private int current = 0;

				@Override
				public HashCode pop() {
					DownHasher<E> hasher = hashers.get(this.current);
					HashCode code = hasher.hash(set);
					this.current++;
					return code;
				}

				@Override
				public boolean isEmpty() {
					return this.current == TwisterTrieForest.this.trieHeight;
				}

				@Override
				public int getAmountLeft() {
					return TwisterTrieForest.this.trieHeight - this.current;
				}
			};

			trie.add(leaf, provider);

			trieCounter++;
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
