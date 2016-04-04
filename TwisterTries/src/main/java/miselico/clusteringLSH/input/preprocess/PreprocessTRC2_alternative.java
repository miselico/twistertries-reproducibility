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
import java.util.TreeSet;

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
 * This was a test to see whether processing the dataset differently would make any difference.
 *
 * This is NOT the pre-processing used in the experiments for SIGMOD 2015, see PreprocessReuters2.
 *
 *
 * @author michael
 *
 */
public class PreprocessTRC2_alternative {

	private final static Splitter tokenSplitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();

	private final static CharMatcher m = CharMatcher.anyOf("-_!@#$%*()_+{}:\"'\\,./<>?|[]`~=;");

	private final static Set<String> stopWords = new HashSet<>();

	static {
		InputStream s = PreprocessTRC2_alternative.class.getResourceAsStream("stopwords");
		LineReader r = new LineReader(new InputStreamReader(s));
		String line;
		try {
			while ((line = r.readLine()) != null) {
				PreprocessTRC2_alternative.stopWords.add(line);
			}
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	private static final CharMatcher firstCharacter = CharMatcher.anyOf("a");

	public static void main(String[] args) throws IOException {
		File f = new File(args[1]);
		Reader r = new FileReader(f);
		CSVStrategy strat = new CSVStrategy(',', '"', '\0', true, true);
		CSVReader<String> csvPersonReader = new CSVReaderBuilder<String>(r).strategy(strat).entryParser(new NewsItemParser()).build();
		FileWriter w = new FileWriter("cleaned-reduced-stemmed" + f.getName());

		int counter = 0;
		int skipcounter = 0;
		for (String string : csvPersonReader) {
			//clean up string
			Iterable<String> tokens = PreprocessTRC2_alternative.tokenSplitter.split(string);
			Iterable<String> transformed = Iterables.transform(tokens, new Function<String, String>() {
				@Override
				public String apply(String s) {
					return PreprocessTRC2_alternative.m.removeFrom(s).toLowerCase();
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
					//This is a try to reduce the size of the sets and get a broader distribution of distances.
					if (!PreprocessTRC2_alternative.firstCharacter.matches(input.charAt(0))) {
						return false;
					}
					if (PreprocessTRC2_alternative.stopWords.contains(input)) {
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

			TreeSet<String> result = Sets.newTreeSet(stemmed);
			if (result.isEmpty() || (result.size() < 6)) {
				counter++;
				skipcounter++;
				continue;
			}
			w.write("article" + counter + ": ");
			int wordCounter = 0;
			for (String string2 : result) {
				w.write(string2 + ' ');
				wordCounter++;
				if (wordCounter == 5) {
					break;
				}
			}
			w.write('\n');
			counter++;
			if ((counter % 1000) == 0) {
				System.out.println(counter);
			}
		}
		System.out.printf("%d articles processed, %d skipped \n", counter, skipcounter);
		w.flush();
		w.close();
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
