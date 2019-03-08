package soot.lambdaMetaFactory;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 Jon Mathews
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

/**
 * Tests parameter and return value adaptations for modeling of LambdaMetaFactory.
 * 
 * <p>
 * According to the javadoc for
 * <a href="https://docs.oracle.com/javase/10/docs/api/java/lang/invoke/LambdaMetafactory.html">LambaMetaFactory</a>,
 * adaptations may include boxing and unboxing. Varargs are expected to be handled by the caller, hence need not be tested
 * here.
 * 
 * 
 * <p>
 * Parameters are adapted per <a href="https://docs.oracle.com/javase/specs/jls/se10/html/jls-5.html#jls-5.3">JLS §5.3
 * Invocation Contexts</a>; the cases below enumerate the adaptations which may occur to parameters.
 * 
 * <pre>
<ol>
<li>an identity conversion (§5.1.1)
<li>a widening primitive conversion (§5.1.2)
<li>a widening reference conversion (§5.1.5)
<li>a widening reference conversion followed by an unboxing conversion
<li>a widening reference conversion followed by an unboxing conversion, then followed by a widening primitive conversion
<li>a boxing conversion (§5.1.7)
<li>a boxing conversion followed by widening reference conversion
<li>an unboxing conversion (§5.1.8)
<li>an unboxing conversion followed by a widening primitive conversion
</ol>
 * </pre>
 * 
 * <p>
 * Return values are adapted per <a href="https://docs.oracle.com/javase/specs/jls/se10/html/jls-5.html#jls-5.2">JLS §5.2
 * Assignment Contexts</a>.
 * 
 * <pre>
 *  
<ol>
<li>an identity conversion (§5.1.1)
<li>a widening primitive conversion (§5.1.2)
<li>a widening reference conversion (§5.1.5)
<li>a widening reference conversion followed by an unboxing conversion
<li>a widening reference conversion followed by an unboxing conversion, then followed by a widening primitive conversion
<li>a boxing conversion (§5.1.7)
<li>a boxing conversion followed by a widening reference conversion
<li>an unboxing conversion (§5.1.8)
<li>an unboxing conversion followed by a widening primitive conversion
</ol>
 * </pre>
 * 
 */
public class Adapt {

  public void parameterBoxingTarget() {
    parameterBoxing(p -> System.out.println(p), p -> System.out.println(p), p -> System.out.println(p));
  }

  public void parameterBoxing(IntInterfaceParam I0param, IntegerInterfaceParam I1param, LongInterfaceParam L0param) {
    // unbox; parameter
    // CASE 8
    // JLS 5.1.8
    // Integer -> int
    IntegerInterfaceParam I1var = I0param::adapt;
    I1var.adapt(Integer.valueOf(1));

    // unbox, widen; parameter
    // CASE 9
    // JLS 5.1.8, 5.1.2
    // Short -> short -> int
    ShortInterfaceParam S1var = I0param::adapt;
    S1var.adapt(Short.valueOf((short) 1));

    // auto box; parameter
    // CASE 6
    // JLS 5.1.7
    // int -> Integer
    IntInterfaceParam I0var = I1param::adapt;
    I0var.adapt(1);

  }

  public void parameterWidening() {
    parameterWidening(p -> System.out.println(p), p -> System.out.println(p), p -> System.out.println(p),
        p -> System.out.println(p), p -> System.out.println(p), p -> System.out.println(p));
  }

  public void parameterWidening(IntInterfaceParam I0param, IntegerInterfaceParam I1param, LongInterfaceParam L0param,
      FloatInterfaceParam F0param, NumberInterfaceParam Nparam, ObjectInterfaceParam Oparam) {
    // primitive widening; parameter
    // CASE 2
    // JLS 5.1.2
    // int -> long
    IntInterfaceParam I0var = L0param::adapt;
    I0var.adapt(1);

    // primitive widening; parameter
    // CASE 2
    // JLS 5.1.2
    // int -> float
    /* IntInterfaceParam */
    I0var = F0param::adapt;
    I0var.adapt(1);

    // autobox followed by reference widening
    // CASE 7 (which includes CASE 3)
    // JLS 5.1.2, 5.1.5
    // int -> Integer -> Number
    /* IntInterfaceParam */
    I0var = Nparam::adapt;

    // autobox followed by reference widening
    // CASE 7 (which includes CASE 3)
    // JLS 5.1.2, 5.1.5
    // int -> Integer -> Object
    /* IntInterfaceParam */
    I0var = Oparam::adapt;

  }

  public void returnBoxing() {
    returnBoxing(() -> 1, () -> new Integer(2), () -> 3L);
  }

  public void returnBoxing(IntInterfaceReturn I0param, IntegerInterfaceReturn I1param, LongInterfaceReturn L0param) {
    // unbox; parameter
    // Integer -> int
    IntegerInterfaceReturn I1var = I0param::adapt;
    I1var.adapt();

    // auto box; parameter
    // int -> Integer
    IntInterfaceReturn I0var = I1param::adapt;
    I0var.adapt();
  }

  public void returnWidening() {
    returnWidening(() -> 1, () -> new Integer(2), () -> 3L);
  }

  public void returnWidening(IntInterfaceReturn I0param, IntegerInterfaceReturn I1param, LongInterfaceReturn L0param) {
    // primitive widening; return
    // JLS 5.1.2
    // int -> long
    LongInterfaceReturn L0var = I0param::adapt;
    L0var.adapt();
  }

  interface ShortInterfaceParam {
    public void adapt(Short p);
  }

  interface IntInterfaceParam {
    public void adapt(int p);
  }

  interface IntegerInterfaceParam {
    public void adapt(Integer p);
  }

  interface LongInterfaceParam {
    public void adapt(long p);
  }

  interface FloatInterfaceParam {
    public void adapt(float p);
  }

  interface NumberInterfaceParam {
    public void adapt(Number p);
  }

  interface ObjectInterfaceParam {
    public void adapt(Object p);
  }

  interface IntInterfaceReturn {
    public int adapt();
  }

  interface ShortInterfaceReturn {
    public short adapt();
  }

  interface IntegerInterfaceReturn {
    public Integer adapt();
  }

  interface LongInterfaceReturn {
    public long adapt();
  }

};