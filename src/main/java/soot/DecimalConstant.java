package soot;

import java.math.BigDecimal;

import soot.jimple.ConstantSwitch;
import soot.jimple.IntConstant;
import soot.jimple.NumericConstant;
import soot.jimple.RealConstant;
import soot.util.Switch;

public class DecimalConstant extends RealConstant {
  private static final long serialVersionUID = 0L;
  public BigDecimal value;

  public DecimalConstant(BigDecimal value) {
    this.value = value;
  }

  public static DecimalConstant v(BigDecimal b) {
    return new DecimalConstant(b);
  }

  public BigDecimal getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }

  @Override
  public Type getType() {
    return DecimalType.v();
  }

  @Override
  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseDecimalConstant(this);
  }

  /**
   * Checks if passed argument is instance of expected class.
   *
   * @param constant
   *          the instance to check
   * @throws IllegalArgumentException
   *           when check fails
   */
  private void assertInstanceOf(NumericConstant constant) {
    if (!(constant instanceof DecimalConstant)) {
      throw new IllegalArgumentException("DecimalConstant expected");
    }
  }

  @Override
  public NumericConstant add(NumericConstant c) {
    assertInstanceOf(c);
    return DecimalConstant.v(this.value.add(((DecimalConstant) c).value));
  }

  @Override
  public NumericConstant subtract(NumericConstant c) {
    assertInstanceOf(c);
    return DecimalConstant.v(this.value.subtract(((DecimalConstant) c).value));
  }

  @Override
  public NumericConstant multiply(NumericConstant c) {
    assertInstanceOf(c);
    return DecimalConstant.v(this.value.multiply(((DecimalConstant) c).value));
  }

  @Override
  public NumericConstant divide(NumericConstant c) {
    assertInstanceOf(c);
    return DecimalConstant.v(this.value.divide(((DecimalConstant) c).value));
  }

  @Override
  public NumericConstant remainder(NumericConstant c) {
    assertInstanceOf(c);
    return DecimalConstant.v(this.value.remainder((((DecimalConstant) c).value)));
  }

  @Override
  public NumericConstant equalEqual(NumericConstant c) {
    assertInstanceOf(c);
    return IntConstant.v(this.value.compareTo(((DecimalConstant) c).value) == 0 ? 1 : 0);
  }

  @Override
  public NumericConstant notEqual(NumericConstant c) {
    assertInstanceOf(c);
    return IntConstant.v(this.value.compareTo(((DecimalConstant) c).value) != 0 ? 1 : 0);
  }

  @Override
  public boolean isLessThan(NumericConstant c) {
    assertInstanceOf(c);
    return this.value.compareTo(((DecimalConstant) c).value) < 0;
  }

  @Override
  public NumericConstant lessThan(NumericConstant c) {
    assertInstanceOf(c);
    return IntConstant.v(this.value.compareTo(((DecimalConstant) c).value) < 0 ? 1 : 0);
  }

  @Override
  public NumericConstant lessThanOrEqual(NumericConstant c) {
    assertInstanceOf(c);
    return IntConstant.v(this.value.compareTo(((DecimalConstant) c).value) <= 0 ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThan(NumericConstant c) {
    assertInstanceOf(c);
    return IntConstant.v(this.value.compareTo(((DecimalConstant) c).value) > 0 ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThanOrEqual(NumericConstant c) {
    assertInstanceOf(c);
    return IntConstant.v(this.value.compareTo(((DecimalConstant) c).value) >= 0 ? 1 : 0);
  }

  @Override
  public IntConstant cmpg(RealConstant constant) {
    assertInstanceOf(constant);
    final BigDecimal cValue = ((DecimalConstant) constant).value;
    return IntConstant.v(value.compareTo(cValue));
  }

  @Override
  public IntConstant cmpl(RealConstant constant) {
    assertInstanceOf(constant);
    final BigDecimal cValue = ((DecimalConstant) constant).value;
    return IntConstant.v(-value.compareTo(cValue));
  }

  @Override
  public NumericConstant negate() {
    return DecimalConstant.v(this.value.negate());
  }
}
