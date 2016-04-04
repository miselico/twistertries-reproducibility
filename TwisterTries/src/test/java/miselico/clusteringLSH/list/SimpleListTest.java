package miselico.clusteringLSH.list;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SimpleListTest extends TestCase {

	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public SimpleListTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(SimpleListTest.class);
	}

	private static final class Node extends ListNode<Integer> {

		private int value;

		public Node(int val) {

			this.value = val;
		}

		@Override
		public Integer getValue() {
			return this.value;
		}

	}

	/**
	 * Rigourous Test :-)
	 */
	public void testListPushBackAndRemoval() {
		SimpleList<Integer> s = SimpleList.create();
		Node oneNode = new Node(1);
		Node twoNode = new Node(2);
		Node threeNode = new Node(3);
		Node fourNode = new Node(4);
		s.pushBack(oneNode);
		s.pushBack(twoNode);
		s.pushBack(threeNode);
		s.pushBack(fourNode);
		Assert.assertEquals(s.showAll(), "[1,2,3,4,]");
		twoNode.removeSelf();
		Assert.assertEquals(s.showAll(), "[1,3,4,]");
		oneNode.removeSelf();
		Assert.assertEquals(s.showAll(), "[3,4,]");
		boolean thrown = false;
		try {
			oneNode.removeSelf();
		} catch (Error r) {
			if (r.getMessage().equals("removal of disconnected node.")) {
				thrown = true;
			}
		} finally {
			assertTrue(thrown);
		}
		Assert.assertEquals(s.showAll(), "[3,4,]");
		threeNode.removeSelf();
		Assert.assertEquals(s.showAll(), "[4,]");
		fourNode.removeSelf();
		Assert.assertEquals(s.showAll(), "[]");

	}

	public void testLonelyNode() {
		SimpleList<Integer> s = SimpleList.create();
		Node oneNode = new Node(1);
		Node twoNode = new Node(2);
		Node threeNode = new Node(3);
		s.pushBack(oneNode);
		assertTrue(oneNode.isLonelyNode());
		s.pushBack(twoNode);
		assertFalse(oneNode.isLonelyNode());
		assertFalse(twoNode.isLonelyNode());
		oneNode.removeSelf();
		assertTrue(twoNode.isLonelyNode());
		boolean thrown = false;
		try {
			oneNode.isLonelyNode();
		} catch (Error r) {
			if (r.getMessage().equals("isLonelyNode check on disconnected node")) {
				thrown = true;
			}
		} finally {
			assertTrue(thrown);
		}
		s.pushBack(threeNode);
		assertFalse(twoNode.isLonelyNode());
		assertFalse(threeNode.isLonelyNode());
	}

	public void testListLength() {
		SimpleList<Integer> s = SimpleList.create();
		Node oneNode = new Node(1);
		Node twoNode = new Node(2);
		Node threeNode = new Node(3);
		assertTrue(s.length() == ListLength.ZERO);
		s.pushBack(oneNode);
		assertTrue(s.length() == ListLength.ONE);
		s.pushBack(twoNode);
		assertTrue(s.length() == ListLength.MORE_AS_ONE);
		oneNode.removeSelf();
		assertTrue(s.length() == ListLength.ONE);
		s.pushBack(threeNode);
		assertTrue(s.length() == ListLength.MORE_AS_ONE);
		twoNode.removeSelf();
		threeNode.removeSelf();
		assertTrue(s.length() == ListLength.ZERO);
	}

	public void testAsArray() {
		SimpleList<Integer> s = SimpleList.create();
		Node oneNode = new Node(1);
		Node twoNode = new Node(2);
		Node threeNode = new Node(3);
		Object[] a = s.asArray();
		assertEquals(0, a.length);
		s.pushBack(oneNode);
		a = s.asArray();
		assertEquals(1, a.length);
		s.pushBack(twoNode);
		s.pushBack(threeNode);
		a = s.asArray();
		assertEquals(3, a.length);
		assertEquals(oneNode.value, a[0]);
		assertEquals(twoNode.value, a[1]);
		assertEquals(threeNode.value, a[2]);

	}

}
