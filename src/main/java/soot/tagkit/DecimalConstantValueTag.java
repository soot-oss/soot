package soot.tagkit;

import java.math.BigDecimal;

import soot.DecimalConstant;

/**
 * .NET decimal constant (128 bit)
 */
public class DecimalConstantValueTag extends ConstantValueTag {

  public static final String NAME = "DoubleConstantValueTag";

  private final BigDecimal value;

  public DecimalConstantValueTag(BigDecimal val) {
    super(null);
    this.value = val;
  }

  public BigDecimal getDecimalValue() {
    return value;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String toString() {
    return "ConstantValue: " + value;
  }

  @Override
  public DecimalConstant getConstant() {
    return DecimalConstant.v(value);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + value.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj) || (this.getClass() != obj.getClass())) {
      return false;
    }
    DecimalConstantValueTag other = (DecimalConstantValueTag) obj;
    return this.value.equals(other.value);
  }
}
