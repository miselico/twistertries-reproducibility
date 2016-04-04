package miselico.clusteringLSH.input.preprocess;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.LineReader;
import com.google.common.primitives.Doubles;
import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

/**
 * Preprocesses the Thomson Reuters Text Research Collection (TRC2) as was done for the SIGMOD 2015 paper.
 *
 * Each item (article) in our dataset was a set of words representing the article.
 *
 * During the preprocessing, we split the article text on whitespace. Then, for each word we
 *
 * removed punctuation marks,
 *
 * converted to lowercase,
 *
 * and applied Porter stemming.
 *
 * From the resulting set, we removed stop words, single characters and numbers.
 *
 * Articles that resulted in an empty set were ignored,
 *
 * which finally resulted in 1.68 million sets, each representing one article.
 *
 * @author michael
 *
 */
public class PreprocessTRC2 {

	private final static Splitter tokenSplitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();

	private final static CharMatcher punctuationMarks = CharMatcher.anyOf("-_!@#$%*()_+{}:\"'\\,./<>?|[]`~=;");

	private final static Set<String> stopWords = new HashSet<>();

	static {
		InputStream s = PreprocessTRC2.class.getResourceAsStream("stopwords");
		LineReader r = new LineReader(new InputStreamReader(s));
		String line;
		try {
			while ((line = r.readLine()) != null) {
				PreprocessTRC2.stopWords.add(line);
			}
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	public static void preprocess(File originalCSVData, File preprocessed) throws IOException {

		Reader r = new FileReader(originalCSVData);
		CSVStrategy strat = new CSVStrategy(',', '"', '\0', true, true);
		CSVReader<String> csvNewItemReader = new CSVReaderBuilder<String>(r).strategy(strat).entryParser(new NewsItemParser()).build();
		FileWriter w = new FileWriter(preprocessed);

		int counter = 0;
		int skipcounter = 0;
		for (String string : csvNewItemReader) {
			//clean up string
			Iterable<String> tokens = PreprocessTRC2.tokenSplitter.split(string);
			Iterable<String> transformed = Iterables.transform(tokens, new Function<String, String>() {
				@Override
				public String apply(String s) {
					return PreprocessTRC2.punctuationMarks.removeFrom(s).toLowerCase();
				}
			});

			Iterable<String> filtered = Iterables.filter(transformed, new Predicate<String>() {

				@Override
				public boolean apply(String input) {
					if (input.length() < 2) {
						return false;
					}
					if (Doubles.tryParse(input) != null) {
						return false;
					}
					if (PreprocessTRC2.stopWords.contains(input)) {
						return false;
					}
					return true;
				}
			});

			final Iterable<String> stemmed = Iterables.transform(filtered, new Function<String, String>() {
				@Override
				public String apply(String input) {
					Stemmer s = new Stemmer();
					s.add(input.toCharArray(), input.length());
					s.stem();
					return s.toString();
				}
			});

			Set<String> result = Sets.newHashSet(stemmed);
			if (result.isEmpty()) {
				counter++;
				skipcounter++;
				continue;
			}
			w.write("article" + counter + ": ");
			for (String string2 : result) {
				w.write(string2 + ' ');
			}
			w.write('\n');
			counter++;
			if ((counter % 100000) == 0) {
				System.out.println(counter);
			}
		}
		System.out.printf("%d articles processed, %d skipped \n", counter, skipcounter);
		w.flush();
		w.close();
		r.close();

	}

	public static void main(String[] args) throws IOException {
		File f = new File(args[1]);
		File preprocessed = new File("cleaned-stemmed" + f.getName());
		PreprocessTRC2.preprocess(f, preprocessed);
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
