package miselico.clusteringLSH.input.preprocess;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import miselico.clusteringLSH.parallel.ThreadManager;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.io.LineReader;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

/**
 * Preprocesses the Thomson Reuters Text Research Collection (TRC2) to form
 * vwordvectors.
 *
 * Each item (article) in our dataset is a vector of words representing the
 * article.
 *
 * During the preprocessing, we split the article text on whitespace. Then, for
 * each word we
 *
 * removed punctuation marks,
 *
 * converted to lowercase,
 *
 * and applied Porter stemming.
 *
 * From the resulting multiset, we removed stop words, single characters and
 * numbers.
 *
 * Articles that resulted in an empty mutliset were ignored,
 *
 * which finally resulted in 1.68 million multisets, each representing one
 * article.
 *
 * These multisets are then converted to vectors.
 *
 * @author michael
 *
 */
public class PreprocessTRC2Vector {

	private final static Splitter tokenSplitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();

	private final static CharMatcher punctuationMarks = CharMatcher.anyOf("-_!@#$%*()_+{}:\"'\\,./<>?|[]`~=;");

	private final static Set<String> stopWords = new HashSet<>();

	static {
		InputStream s = PreprocessTRC2Vector.class.getResourceAsStream("stopwords");
		LineReader r = new LineReader(new InputStreamReader(s));
		String line;
		try {
			while ((line = r.readLine()) != null) {
				PreprocessTRC2Vector.stopWords.add(line);
			}
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	private static Multiset<String> clean(String article) {
		Iterable<String> dirtytokens = PreprocessTRC2Vector.tokenSplitter.split(article);
		Multiset<String> cleanedTokens = HashMultiset.create();
		for (String dirtyToken : dirtytokens) {
			String noPunctLowerCase = PreprocessTRC2Vector.punctuationMarks.removeFrom(dirtyToken).toLowerCase();
			if (noPunctLowerCase.length() < 2) {
				continue;
			}
			if (Doubles.tryParse(noPunctLowerCase) != null) {
				continue;
			}
			if (PreprocessTRC2Vector.stopWords.contains(noPunctLowerCase)) {
				continue;
			}
			Stemmer s = new Stemmer();
			s.add(noPunctLowerCase.toCharArray(), noPunctLowerCase.length());
			s.stem();
			String cleaned = s.toString();
			cleaned = cleaned.intern();
			cleanedTokens.add(cleaned);
		}
		return cleanedTokens;
	}

	public static void preprocess(File originalCSVData, File preprocessed) throws IOException {

		Reader r = new FileReader(originalCSVData);
		CSVStrategy strat = new CSVStrategy(',', '"', '\0', true, true);
		CSVReader<String> csvNewItemReader = new CSVReaderBuilder<String>(r).strategy(strat).entryParser(new NewsItemParser()).build();

		//set containing ALL words
		TreeSet<String> superset = new TreeSet<String>();
		//List of all articles' multisets
		List<Future<Multiset<String>>> allFutureArticles = new ArrayList<>(1800000);
		for (final String string : csvNewItemReader) {
			allFutureArticles.add(ThreadManager.pool.submit(new Callable<Multiset<String>>() {

				@Override
				public Multiset<String> call() throws Exception {
					return PreprocessTRC2Vector.clean(string);
				}

			}));

		}

		int counter = 0;
		int skipcounter = 0;
		ArrayList<Multiset<String>> allArticles = new ArrayList<>(1800000);
		for (Future<Multiset<String>> future : allFutureArticles) {
			Multiset<String> cleanedTokens = Futures.getUnchecked(future);
			counter++;
			if ((counter % 1000) == 0) {
				System.out.println(counter);
			}
			if (cleanedTokens.isEmpty()) {
				skipcounter++;
				continue;
			}
			allArticles.add(cleanedTokens);
			superset.addAll(cleanedTokens.elementSet());
		}

		final List<String> superList = Lists.newArrayList(superset);
		final Queue<Future<int[]>> resultQueue = new LinkedList<Future<int[]>>();//   <>(allArticles.size());
		for (final Multiset<String> article : allArticles) {
			resultQueue.add(ThreadManager.pool.submit(new Callable<int[]>() {

				@Override
				public int[] call() throws Exception {
					int[] asArray = new int[superList.size()];

					for (int i = 0; i < superList.size(); i++) {
						String token = superList.get(i);
						asArray[i] = article.count(token);
					}
					return asArray;
				}
			}));
		}
		try (ObjectOutputStream w = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(preprocessed), 2 << 15))) {
			w.writeInt(allArticles.size());

			for (int i = 0; i < allArticles.size(); i++) {
				Future<int[]> future = resultQueue.poll();
				try {
					w.writeObject(future.get());
				} catch (InterruptedException | ExecutionException e) {
					throw new Error(e);
				}
				if ((i % 1000) == 0) {
					System.out.println("written " + i);
				}
			}

			System.out.printf("%d articles processed, %d skipped, final vector length: %d\n", counter, skipcounter, superList.size());
			w.flush();
			w.close();
		}
		r.close();

	}

	public static void main(String[] args) throws IOException {
		File input = new File(args[0]);
		File output = new File(args[1]);
		PreprocessTRC2Vector.preprocess(input, output);
	}

	public static class NewsItemParser implements CSVEntryParser<String> {
		@Override
		public String parseEntry(String... data) {
			//			System.out.println("1" + data[0]);
			//			System.out.println("2" + data[1]);
			//			System.out.println("3" + data[2]);
			return data[2];
		}
	}

}
