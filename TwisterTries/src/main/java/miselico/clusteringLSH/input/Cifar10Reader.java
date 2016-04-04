package miselico.clusteringLSH.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.UnsignedBytes;

public class Cifar10Reader {
	public static List<InputSet<int[]>> read(File f, int amount) throws FileNotFoundException, IOException {
		return Cifar10Reader.read(new FileInputStream(f), amount);
	}

	/**
	 * array containing all possible byte values. Storing this statically gives
	 * a HUGE memory and GC time gain.
	 */
	//	private static final byte[][] allByteArray = new byte[256][4];
	//
	//	static {
	//		for (int i = 0; i < Cifar10Reader.allByteArray.length; i++) {
	//			Cifar10Reader.allByteArray[i] = new byte[] { 0, 0, 0, UnsignedBytes.checkedCast(i) };
	//		}
	//	}

	private static ArrayList<InputSet<int[]>> read(InputStream fileInputStream, int amount) throws IOException {

		ByteBuffer imageBuffer = ByteBuffer.allocate(3073);
		byte[] asArray = imageBuffer.array();
		int counter = 0;
		// Builder<InputSet> result = ImmutableList.builder();
		ArrayList<InputSet<int[]>> result = new ArrayList<>(amount);
		//System.out.println("Cifar 10 reader was modified slightly (checking that enough bytes are read into asArray), bu t not tested after. Check results carefuly.");
		while (counter < amount) {
			if (!(fileInputStream.available() > 0)) {
				throw new IOException("not enough data available");
			}
			imageBuffer.clear();
			int read = 0;
			while (read != 3073) {
				read += fileInputStream.read(asArray, read, 3073 - read);
			}
			// get the label, and ignore it
			int label = UnsignedBytes.toInt(imageBuffer.get());
			// Builder<byte[]> imageByteArrayList = ImmutableList.builder();
			int[] imageAsIntArray = new int[3072];
			for (int i = 0; i < 3072; i++) {
				byte pixelVal = imageBuffer.get();
				// byte[] fourByteArray = new byte[] { 0, 0, 0, pixelVal };
				int unsignedPixelVal = UnsignedBytes.toInt(pixelVal);
				imageAsIntArray[i] = unsignedPixelVal;
				// imageByteArrayList.add(fourByteArray);
			}
			result.add(new InputSet<int[]>(Integer.toString(counter) + "_class_" + label, imageAsIntArray));
			counter++;
		}

		return result;
	}
}
