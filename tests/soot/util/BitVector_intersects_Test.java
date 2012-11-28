package soot.util;

import soot.util.BitVector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit test suite for the BitVector.intersects() method
 * @Author Quentin Sabah
*/
public class BitVector_intersects_Test extends TestCase {

  public BitVector_intersects_Test(String name) {
    super(name);
  }

  public void testEmptyBitvectorDontIntersectsItself() {
    BitVector a = new BitVector();
    assertFalse(a.intersects(a));
  }

  public void testEmptyBitVectorsDontIntersects() {
    BitVector a = new BitVector();
    BitVector b = new BitVector();
    assertFalse(a.intersects(b));
    assertFalse(b.intersects(a));
  }

  public void testEquallySizedEmptyBitVectorsDontIntersects() {
    BitVector a = new BitVector(1024);
    BitVector b = new BitVector(1024);
    assertFalse(a.intersects(b));
    assertFalse(b.intersects(a));
  }

  public void testNotEquallySizedEmptyBitVectorsDontIntersects() {
    BitVector a = new BitVector(2048);
    BitVector b = new BitVector(1024);
    assertFalse(a.intersects(b));
    assertFalse(b.intersects(a));
  }

  public void testSizedEmptyBitVectorDontIntersectsItself() {
    BitVector a = new BitVector(1024);
    assertFalse(a.intersects(a));
  }

  public void testNonOverlappingBitVectorsDontIntersects() {
    BitVector a = new BitVector();
    BitVector b = new BitVector();
    int i;
    for(i = 0; i < 512; i++) {
      if(i % 2 == 0)
        a.set(i);
      else
        b.set(i);
    }
    assertFalse(a.intersects(b));
    assertFalse(b.intersects(a));
  }

  public void testNotEquallySizedNonOverlappingBitVectorsDontIntersects() {
    BitVector a = new BitVector();
    BitVector b = new BitVector();
    int i;
    for(i = 0; i < 512; i++) {
      a.set(i);
    }
    for(; i < 1024; i++) {
      b.set(i);
    }
    assertFalse(a.intersects(b));
    assertFalse(b.intersects(a));
  }

  public void testNonEmptyBitVectorIntersectsItself() {
    BitVector a = new BitVector();
    a.set(337);
    assertTrue(a.intersects(a));
  }

  public void testNotEquallySizedOverlappingBitVectorsIntersects() {
    BitVector a = new BitVector(1024);
    BitVector b = new BitVector(512);

    a.set(337);
    b.set(337);
    assertTrue(a.intersects(b));
    assertTrue(b.intersects(a));
    a.clear(337);
    b.clear(337);

    for(int i = 0; i < 512; i++) {
      a.set(i); b.set(i);
      assertTrue(a.intersects(b));
      assertTrue(b.intersects(a));
      a.clear(i); b.clear(i);
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(BitVector_intersects_Test.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
