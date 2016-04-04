package miselico.clusteringLSH;

import junit.framework.Test;
import junit.framework.TestSuite;
import miselico.clusteringLSH.input.InputReaderTest;
import miselico.clusteringLSH.list.SimpleListTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(InputReaderTest.class);
		suite.addTestSuite(IntegrationTest.class);
		suite.addTestSuite(SimpleListTest.class);
		//$JUnit-END$
		return suite;
	}

}
