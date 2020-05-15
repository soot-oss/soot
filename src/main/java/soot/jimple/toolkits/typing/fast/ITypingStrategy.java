package soot.jimple.toolkits.typing.fast;

import java.util.List;

import soot.Local;
import soot.util.Chain;

/**
 * Provides a way to use different was to create and minimize typings
 * 
 * @author Marc Miltenberger
 */
public interface ITypingStrategy {

  /**
   * Creates a new typing class instance with initialized bottom types for the given locals
   * 
   * @param locals
   *          the locals
   * @return the typing
   */
  public Typing createTyping(Chain<Local> locals);

  /**
   * Creates a new typing class as a copy from a given class
   * 
   * @param locals
   *          the locals
   * @return the typing
   */
  public Typing createTyping(Typing tg);

  /**
   * Minimize the given typing list using the hierarchy
   * 
   * @param tgs
   *          the typign list
   * @param h
   *          the hierarchy
   */
  public void minimize(List<Typing> tgs, IHierarchy h);
}
