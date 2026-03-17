package soot;

/**
 * A wrapper object for a pack of optimizations. Provides chain-like operations, except that the key is the phase name.
 */
public class BodyPack extends Pack {

  public BodyPack(String name) {
    super(name);
  }

  @Override
  protected void internalApply(Body b) {
    for (Transform t : this) {
      t.apply(b);
    }
  }
}
