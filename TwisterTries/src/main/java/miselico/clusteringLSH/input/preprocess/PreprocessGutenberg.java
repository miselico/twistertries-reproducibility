package miselico.clusteringLSH.input.preprocess;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.LineReader;

/**
 * This class can be used to pre-process the guthenberg dataset for consumption by the twister trie algorithm.
 *
 * This has not been used for the SIGMOD 2015 paper.
 *
 * @author michael
 *
 */
public class PreprocessGutenberg {

	private final static Splitter tokenSplitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();

	private final static CharMatcher m = CharMatcher.anyOf("-_!@#$%*()_+{}:\"'\\,./<>?|[]`~=;");

	private final static Set<String> stopWords = new HashSet<>();

	static {
		InputStream s = PreprocessGutenberg.class.getResourceAsStream("stopwords");
		LineReader r = new LineReader(new InputStreamReader(s));
		String line;
		try {
			while ((line = r.readLine()) != null) {
				PreprocessGutenberg.stopWords.add(line);
			}
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	public static void main(String[] args) throws IOException {
		//System.out.println(Charset.defaultCharset());

		File directory = new File("datasets/guthenberg/original");
		FileWriter w = new FileWriter("datasets/guthenberg/cleaned/combined");
		for (File in : directory.listFiles()) {
			PreprocessGutenberg.process(in, w);
		}
	}

	public static void process(File f, Writer w) throws IOException {
		Reader r = new FileReader(f);

		LineReader lr = new LineReader(r);

		int counter = 0;
		int skipcounter = 0;

		String line = lr.readLine();

		List<String> paragraphs = new LinkedList<>();
		List<String> currentParagraph = new LinkedList<>();
		//FIXME, this code now swallows the last paragraph
		while ((line = lr.readLine()) != null) {
			if (line.isEmpty()) {
				if (currentParagraph.size() > 10) {
					StringBuilder b = new StringBuilder();
					for (String string : currentParagraph) {
						b.append(string);
						b.append(' ');
					}
					paragraphs.add(b.toString());
					currentParagraph.clear();
				} else {
					//keep appending
				}
			} else {
				currentParagraph.add(line);
			}
		}
		for (String string : paragraphs) {
			//clean up string
			Iterable<String> tokens = PreprocessGutenberg.tokenSplitter.split(string);
			Iterable<String> transformed = Iterables.transform(tokens, new Function<String, String>() {
				@Override
				public String apply(String s) {
					return PreprocessGutenberg.m.removeFrom(s).toLowerCase();
				}
			});

			Iterable<String> filtered = Iterables.filter(transformed, new Predicate<String>() {

				@Override
				public boolean apply(String input) {
					if (PreprocessGutenberg.stopWords.contains(input)) {
						return false;
					}
					return true;
				}
			});

			TreeSet<String> result = Sets.newTreeSet(filtered);
			if (result.isEmpty() || (result.size() < 6)) {
				counter++;
				skipcounter++;
				continue;
			}
			for (String string2 : filtered) {
				w.write(string2 + ' ');
			}
			w.write('\n');
			counter++;
			if ((counter % 1000) == 0) {
				System.out.println(counter);
			}
		}
		System.out.printf("%d paragraphs processed, %d skipped \n", counter, skipcounter);
		w.flush();
	}

}
