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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import miselico.clusteringLSH.input.SparseVector;
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
public class PreprocessTRC2VectorSecondTry {

	private final static Splitter tokenSplitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();

	private final static CharMatcher punctuationMarks = CharMatcher.anyOf("-_!@#$%*()_+{}:\"'\\,./<>?|[]`~=;");

	private final static Set<String> stopWords = new HashSet<>();

	static {
		InputStream s = PreprocessTRC2VectorSecondTry.class.getResourceAsStream("stopwords");
		LineReader r = new LineReader(new InputStreamReader(s));
		String line;
		try {
			while ((line = r.readLine()) != null) {
				PreprocessTRC2VectorSecondTry.stopWords.add(line);
			}
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	private static Multiset<String> clean(String article) {
		Iterable<String> dirtytokens = PreprocessTRC2VectorSecondTry.tokenSplitter.split(article);
		Multiset<String> cleanedTokens = HashMultiset.create();
		for (String dirtyToken : dirtytokens) {
			String noPunctLowerCase = PreprocessTRC2VectorSecondTry.punctuationMarks.removeFrom(dirtyToken).toLowerCase();
			if (noPunctLowerCase.length() < 2) {
				continue;
			}
			if (Doubles.tryParse(noPunctLowerCase) != null) {
				continue;
			}
			if (PreprocessTRC2VectorSecondTry.stopWords.contains(noPunctLowerCase)) {
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

	private static Queue<Future<Multiset<String>>> getAllCleaned(File originalCSVData) throws IOException {
		try (Reader r = new FileReader(originalCSVData)) {
			CSVStrategy strat = new CSVStrategy(',', '"', '\0', true, true);
			CSVReader<String> csvNewItemReader = new CSVReaderBuilder<String>(r).strategy(strat).entryParser(new NewsItemParser()).build();

			//List of all articles' multisets
			Queue<Future<Multiset<String>>> allFutureArticles = new LinkedList<>();
			for (final String string : csvNewItemReader) {
				allFutureArticles.add(ThreadManager.pool.submit(new Callable<Multiset<String>>() {

					@Override
					public Multiset<String> call() throws Exception {
						return PreprocessTRC2VectorSecondTry.clean(string);
					}

				}));

			}
			return allFutureArticles;
		}

	}

	public static void preprocess(File originalCSVData, File preprocessed) throws IOException {
		Queue<Future<Multiset<String>>> allFutureArticles = PreprocessTRC2VectorSecondTry.getAllCleaned(originalCSVData);
		//set containing ALL words
		TreeSet<String> superset = new TreeSet<String>();
		int acceptedArticleCount = 0;
		while (!allFutureArticles.isEmpty()) {
			Multiset<String> cleanedTokens = Futures.getUnchecked(allFutureArticles.poll());
			if (cleanedTokens.isEmpty()) {
				continue;
			}
			acceptedArticleCount++;
			if ((acceptedArticleCount % 10000) == 0) {
				System.out.println("collated " + acceptedArticleCount);
			}
			superset.addAll(cleanedTokens);
		}
		ArrayList<String> superList = Lists.newArrayList(superset);
		Collections.sort(superList);
		System.out.println("Vector size : " + superList.size());
		//now, the superlist is created, we loop over the articles a second time.
		//This seems like a waste, but there is just not enough memory to do it in one go.
		allFutureArticles = PreprocessTRC2VectorSecondTry.getAllCleaned(originalCSVData);
		int skipcounter = 0;
		int counter = 0;
		try (ObjectOutputStream w = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(preprocessed), 2 << 15))) {
			w.writeInt(acceptedArticleCount);

			while (!allFutureArticles.isEmpty()) {
				Multiset<String> cleanedTokens = Futures.getUnchecked(allFutureArticles.poll());
				counter++;
				if ((counter % 10000) == 0) {
					System.out.println("written " + counter + ", still " + allFutureArticles.size());
				}
				if (cleanedTokens.isEmpty()) {
					skipcounter++;
					continue;
				}
				SparseVector v = SparseVector.create(cleanedTokens, superList);
				w.writeObject(v);

			}

			System.out.printf("%d articles processed, %d skipped, final vector length: %d\n", counter, skipcounter, superList.size());
			w.flush();
			w.close();
		}
	}

	public static void main(String[] args) throws IOException {
		File input = new File(args[0]);
		File output = new File(args[1]);
		PreprocessTRC2VectorSecondTry.preprocess(input, output);
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
