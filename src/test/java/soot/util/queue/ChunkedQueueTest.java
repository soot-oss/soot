package soot.util.queue;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

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

	@Test
	public void removeFromLargeQueueTest() {
		ChunkedQueue<String> queue = new ChunkedQueue<>();
		QueueReader<String> rdr = queue.reader();
		for (int i = 0; i < 100; i++)
			queue.add("Hello World " + i);
		Assert.assertTrue(rdr.hasNext());
		rdr.remove("Hello World 90");
		Assert.assertTrue(rdr.hasNext());
		Assert.assertEquals(rdr.next(), "Hello World 0");
		Assert.assertTrue(rdr.hasNext());
	}

}
