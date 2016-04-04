package miselico.clusteringLSH.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SparseVectorReader {

	public static BlockingQueue<InputSet<SparseVector>> readAsync(final InputStream input) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(input);
		int amount = ois.readInt();
		return SparseVectorReader.readAsync(ois, amount);
	}

	public static BlockingQueue<InputSet<SparseVector>> readAsync(final InputStream input, final int limit) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(input);
		//ignore the amount encoded.
		ois.readInt();
		return SparseVectorReader.readAsync(ois, limit);
	}

	private static BlockingQueue<InputSet<SparseVector>> readAsync(final ObjectInputStream input, final int limit) throws IOException {
		final BlockingQueue<InputSet<SparseVector>> result = new ArrayBlockingQueue<>(1000);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					for (int i = 0; i < limit; i++) {
						SparseVector articleVec = (SparseVector) input.readObject();
						result.put(new InputSet<SparseVector>(String.valueOf(i), articleVec));
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
