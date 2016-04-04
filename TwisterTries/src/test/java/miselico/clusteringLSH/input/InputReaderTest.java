package miselico.clusteringLSH.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.common.collect.ImmutableList;

public class InputReaderTest extends TestCase {
	/** Create the test case
	 *
	 * @param testName
	 *            name of the test case */
	public InputReaderTest(String testName) {
		super(testName);
	}

	public void testReadFirstTest() throws IOException {

//		InputStream inputStream = InputReaderTest.class.getResourceAsStream("/miselico/clusteringLSH/input/firstTest.dat");
//		Assert.assertNotNull(inputStream);
//		List<InputSet<ImmutableList<String>>> input = InputReader.read(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//		Iterator<InputSet<ImmutableList<String>>> iter = input.iterator();
//		for (int i = 1; i <= 6; i++) {
//			Assert.assertTrue(iter.hasNext());
//			InputSet<ImmutableList<String>> s = iter.next();
//			Assert.assertEquals("set" + i, s.id);
//		}
		//iter = input.iterator();
		//		Assert.assertTrue(iter.hasNext());
		//		Assert.assertEquals("set1[a,b,c,d,]", iter.next().toString());
		//		Assert.assertTrue(iter.hasNext());
		//		Assert.assertEquals("set2[b,c,d,e,]", iter.next().toString());
		//		Assert.assertTrue(iter.hasNext());
		//		Assert.assertEquals("set3[b,cee,d,f,]", iter.next().toString());
		//		Assert.assertTrue(iter.hasNext());
		//		Assert.assertEquals("set4[a,b,c,]", iter.next().toString());
		//		Assert.assertTrue(iter.hasNext());
		//		Assert.assertEquals("set5[b,]", iter.next().toString());
		//		Assert.assertTrue(iter.hasNext());
		//		Assert.assertEquals("set6[g,]", iter.next().toString());
		//		Assert.assertFalse(iter.hasNext());
		//		for (InputSet inputSet : input) {
		//			System.out.println(inputSet);
		//		}

	}

}
