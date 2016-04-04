package miselico.clusteringLSH.parallel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import miselico.clusteringLSH.hashing.DownHasher;
import miselico.clusteringLSH.hashing.LSHFunctionFactory;
import miselico.clusteringLSH.input.InputSet;
import miselico.clusteringLSH.management.HashCodeProvider;
import miselico.clusteringLSH.twistertrie.ForestConfig;

import com.google.common.hash.HashCode;

public class ParallelHashing<E> {

	private final int height;
	private final int number;

	private final DownHasher<E>[][] hashers;

	public ParallelHashing(ForestConfig config, LSHFunctionFactory<E> lshFact, long seed) {
		this(config.getTrieheight(), config.getNumberOfTries(), lshFact, seed);
	}

	@SuppressWarnings("unchecked")
	private ParallelHashing(final int height, final int tries, LSHFunctionFactory<E> lshFact, long seed) {

		this.height = height;
		this.number = tries;

		this.hashers = new DownHasher[this.number][this.height];

		Random rand = new Random(seed);

		for (int i = 0; i < this.number; i++) {
			this.hashers[i] = new DownHasher[this.height];
			for (int j = 0; j < height; j++) {
				this.hashers[i][j] = lshFact.getLSHFunction(rand.nextLong());
				// this.hashers[i][j] = lshFact.getLSHFunction((i + 1) * (j + 1)
				// * 4654687871111254598L);
			}
		}

	}

	public void printHashStuff() {
		for (int i = 0; i < this.number; i++) {
			for (int j = 0; j < this.height; j++) {
				System.out.println(this.hashers[i][j]);
			}
		}
	}

	// TODO this should not be used any loinger, since it scrambles the input
	// order
	// /**
	// *
	// * @param input
	// * @return
	// */
	// public BlockingQueue<HashResult> hashParallel(final Iterable<InputSet>
	// input) {
	// final BlockingQueue<HashResult> result = new LinkedBlockingQueue<>();
	//
	// //read all tasks
	// ParallelHashing.pool.submit(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// for (final InputSet in : input) {
	// ParallelHashing.pool.submit(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// HashResult res = ParallelHashing.this.hashSerial(in);
	// //System.out.println("DONE");
	// result.add(res);
	// } catch (Throwable t) {
	// t.printStackTrace();
	// }
	// }
	// });
	// }
	//
	// } catch (Throwable t) {
	// t.printStackTrace();
	// }
	// }
	// });
	//
	// return result;
	// }

	/**
	 *
	 * @param input
	 * @return
	 */
	public List<Future<HashResult<E>>> hashParallel(final BlockingQueue<InputSet<E>> input, final int amount) {
		// final BlockingQueue<HashResult> result = new LinkedBlockingQueue<>();

		List<Future<HashResult<E>>> futureResults = new ArrayList<>(amount);

		// read all tasks
		for (int i = 0; i < amount; i++) {
			final InputSet<E> in;
			try {
				in = input.take();
			} catch (InterruptedException e) {
				throw new Error(e);
			}

			futureResults.add(ThreadManager.pool.submit(new Callable<HashResult<E>>() {

				@Override
				public HashResult<E> call() throws Exception {
					return ParallelHashing.this.hashSerial(in);
				}
			}));

		}

		return futureResults;
	}

	private HashResult<E> hashSerial(final InputSet<E> in) {
		final HashCode[][] hashArray = new HashCode[this.number][this.height];
		for (int i = 0; i < this.number; i++) {
			for (int j = 0; j < this.height; j++) {
				DownHasher<E> hasher = this.hashers[i][j];
				hashArray[i][j] = hasher.hash(in.data);
			}
		}
		return new HashResult<E>() {

			@Override
			public String getID() {
				return in.id;
			}

			@Override
			public Iterator<HashCodeProvider> iterator() {
				return new Iterator<HashCodeProvider>() {

					int currentNumber = 0;

					@Override
					public boolean hasNext() {
						return this.currentNumber < ParallelHashing.this.number;
					}

					@Override
					public HashCodeProvider next() {
						final int mynumber = this.currentNumber;

						HashCodeProvider provider = new HashCodeProvider() {

							int myHeight = 0;

							@Override
							public HashCode pop() {
								HashCode val = hashArray[mynumber][this.myHeight];
								this.myHeight++;
								return val;
							}

							@Override
							public boolean isEmpty() {
								return this.myHeight == ParallelHashing.this.height;
							}

							@Override
							public int getAmountLeft() {
								return ParallelHashing.this.height - this.myHeight;
							}
						};
						this.currentNumber++;
						return provider;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			@Override
			public E getContent() {
				return in.data;
			}

		};
	}
}
