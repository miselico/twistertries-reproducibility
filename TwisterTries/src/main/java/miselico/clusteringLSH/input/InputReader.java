package miselico.clusteringLSH.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.LineReader;

public class InputReader {

	//	private static int longestIdentifierSize = 500;
	//	private static int BUFFER_SIZE = longestIdentifierSize;
	//
	//	private static String extractValue(ByteBuffer buffer) {
	//		String value = Arrays.copyOf(buffer.array(), buffer.position());
	//		buffer.clear();
	//		return value;
	//	}

	public static List<InputSet<ImmutableList<String>>> read(File f) throws FileNotFoundException, IOException {
		return InputReader.read(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
	}

	// public static List<InputSet> read(InputStream in) throws IOException {
	// ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
	// int b;
	// List<InputSet> sets = Lists.newArrayList();
	// b = in.read();
	// nextLine: do {
	// if (b == '#') {
	// // comment
	// do {
	// // dispose
	// } while ((b = in.read()) != '\n');
	// continue nextLine;
	// }
	//
	// do {
	// buffer.put((byte) b);
	// } while ((b = in.read()) != ':');
	// String key = extractValue(buffer);
	// List<String> values = Lists.newArrayList();
	// b = in.read();
	// do {
	// if (b != ' '){
	// throw new Error("Each value must be preceded by space.");
	// }
	// b = in.read();
	// do {
	// buffer.put((byte) b);
	// } while ((b = in.read()) != ' ' && (b != '\n'));
	// String value = extractValue(buffer);
	// values.add(value);
	// } while (b != '\n');
	// InputSet set = new InputSet(values, key);
	// sets.add(set);
	// } while (b != -1);
	// return sets;
	// }

	private static Splitter colonSplitter = Splitter.on(':').limit(2).trimResults();
	private static Splitter valueSplitter = Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings();

	public static List<InputSet<ImmutableList<String>>> read(Reader input) throws IOException {
		List<InputSet<ImmutableList<String>>> result = new ArrayList<>();
		LineReader l = new LineReader(input);
		String line;
		while ((line = l.readLine()) != null) {
			if (line.startsWith("#")) {
				continue;
			}
			Iterator<String> it = InputReader.colonSplitter.split(line).iterator();
			String key = it.next();
			ImmutableList<String> values = ImmutableList.copyOf(InputReader.valueSplitter.split(it.next()));
			InputSet<ImmutableList<String>> set = new InputSet<ImmutableList<String>>(key, values);
			result.add(set);
		}
		return result;
	}

	public static BlockingQueue<InputSet<ImmutableList<String>>> readAsync(final Reader input, final int limit) throws IOException {
		final BlockingQueue<InputSet<ImmutableList<String>>> result = new ArrayBlockingQueue<>(1000);
		new Thread(new Runnable() {

			@Override
			public void run() {
				int counter = 0;

				try {
					LineReader l = new LineReader(input);
					String line;
					while (counter < limit) {
						line = l.readLine();
						if (line.startsWith("#")) {
							continue;
						}
						counter++;
						Iterator<String> it = InputReader.colonSplitter.split(line).iterator();
						String key = it.next();
						ImmutableList<String> values = ImmutableList.copyOf(InputReader.valueSplitter.split(it.next()));
						InputSet<ImmutableList<String>> set = new InputSet<ImmutableList<String>>(key, values);
						result.put(set);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					throw new Error(e);
				} finally {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();

		return result;
	}
}
