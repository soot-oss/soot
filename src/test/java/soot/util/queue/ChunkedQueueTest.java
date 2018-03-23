package soot.util.queue;

import org.junit.Test;
import org.testng.Assert;

public class ChunkedQueueTest {

	@Test
	public void simpleQueueTest() {
		ChunkedQueue<String> queue = new ChunkedQueue<>();
		queue.add("Hello World");
		QueueReader<String> rdr = queue.reader();
		queue.add("Test");

		Assert.assertTrue(rdr.hasNext());
		Assert.assertEquals(rdr.next(), "Test");
		Assert.assertFalse(rdr.hasNext());
	}

	@Test
	public void emptyQueueTest() {
		ChunkedQueue<String> queue = new ChunkedQueue<>();
		queue.add("Hello World");
		queue.add("Test");
		QueueReader<String> rdr = queue.reader();
		Assert.assertFalse(rdr.hasNext());
	}

	@Test
	public void removeFromQueueTest() {
		ChunkedQueue<String> queue = new ChunkedQueue<>();
		QueueReader<String> rdr = queue.reader();
		queue.add("Hello World");
		queue.add("Test");
		Assert.assertTrue(rdr.hasNext());
		rdr.remove("Hello World");
		Assert.assertTrue(rdr.hasNext());
		rdr.remove("Test");
		Assert.assertFalse(rdr.hasNext());
	}

	@Test
	public void removeFromQueueTest2() {
		ChunkedQueue<String> queue = new ChunkedQueue<>();
		QueueReader<String> rdr = queue.reader();
		queue.add("Hello World");
		queue.add("Test");
		rdr.remove("Hello World");
		rdr.remove("Test");
		Assert.assertFalse(rdr.hasNext());
	}

	@Test
	public void removeFromQueueTest3() {
		ChunkedQueue<String> queue = new ChunkedQueue<>();
		QueueReader<String> rdr = queue.reader();
		queue.add("Hello World");
		queue.add("Test");
		queue.add("Foo");
		rdr.remove("Hello World");
		rdr.remove("Test");
		Assert.assertEquals(rdr.next(), "Foo");
	}

	@Test
	public void removeFromQueueTest4() {
		ChunkedQueue<String> queue = new ChunkedQueue<>();
		QueueReader<String> rdr = queue.reader();
		queue.add("Hello World");
		queue.add("Test");
		queue.add("Foo");
		rdr.remove("Hello World");
		rdr.remove("Test");
		Assert.assertEquals(rdr.hasNext(), true);
		Assert.assertEquals(rdr.next(), "Foo");
	}

	@Test
	public void removeFromQueueAndGetTest() {
		ChunkedQueue<String> queue = new ChunkedQueue<>();
		QueueReader<String> rdr = queue.reader();
		queue.add("Hello World");
		queue.add("Test");
		Assert.assertTrue(rdr.hasNext());
		rdr.remove("Hello World");
		Assert.assertTrue(rdr.hasNext());
		Assert.assertEquals(rdr.next(), "Test");
		Assert.assertFalse(rdr.hasNext());
	}

	@Test
	public void readerRemoveTest() {
		ChunkedQueue<String> queue = new ChunkedQueue<>();
		QueueReader<String> rdr = queue.reader();
		QueueReader<String> rdr2 = queue.reader();
		queue.add("Hello World");
		queue.add("Test");

		Assert.assertTrue(rdr.hasNext());
		Assert.assertEquals(rdr.next(), "Hello World");
		rdr.remove();
		Assert.assertTrue(rdr.hasNext());
		Assert.assertEquals(rdr.next(), "Test");
		Assert.assertFalse(rdr.hasNext());

		Assert.assertTrue(rdr2.hasNext());
		Assert.assertEquals(rdr2.next(), "Test");
		Assert.assertFalse(rdr2.hasNext());
	}

}
