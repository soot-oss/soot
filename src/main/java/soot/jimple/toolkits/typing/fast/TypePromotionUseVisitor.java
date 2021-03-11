package soot.jimple.toolkits.typing.fast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.IntType;
import soot.Local;
import soot.ShortType;
import soot.Type;
import soot.Value;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;

public class TypePromotionUseVisitor implements IUseVisitor {
  private static final Logger logger = LoggerFactory.getLogger(TypePromotionUseVisitor.class);
  private JimpleBody jb;
  private Typing tg;

  public boolean fail;
  public boolean typingChanged;

  private final ByteType byteType = ByteType.v();
  private final Integer32767Type integer32767Type = Integer32767Type.v();
  private final Integer127Type integer127Type = Integer127Type.v();

  public TypePromotionUseVisitor(JimpleBody jb, Typing tg) {
    this.jb = jb;
    this.tg = tg;

    this.fail = false;
    this.typingChanged = false;
  }

  private Type promote(Type tlow, Type thigh) {
    if (tlow instanceof Integer1Type) {
      if (thigh instanceof IntType) {
        return Integer127Type.v();
      } else if (thigh instanceof ShortType) {
        return byteType;
      } else if (thigh instanceof BooleanType || thigh instanceof ByteType || thigh instanceof CharType
          || thigh instanceof Integer127Type || thigh instanceof Integer32767Type) {
        return thigh;
      } else {
        throw new RuntimeException();
      }
    } else if (tlow instanceof Integer127Type) {
      if (thigh instanceof ShortType) {
        return byteType;
      } else if (thigh instanceof IntType) {
        return integer127Type;
      } else if (thigh instanceof ByteType || thigh instanceof CharType || thigh instanceof Integer32767Type) {
        return thigh;
      } else {
        throw new RuntimeException();
      }
    } else if (tlow instanceof Integer32767Type) {
      if (thigh instanceof IntType) {
        return integer32767Type;
      } else if (thigh instanceof ShortType || thigh instanceof CharType) {
        return thigh;
      } else {
        throw new RuntimeException();
      }
    } else {
      throw new RuntimeException();
    }
  }

  @Override
  public Value visit(Value op, Type useType, Stmt stmt, boolean checkOnly) {
    if (this.finish()) {
      return op;
    }

    Type t = AugEvalFunction.eval_(this.tg, op, stmt, this.jb);

    if (!AugHierarchy.ancestor_(useType, t)) {
      logger.error(String.format("Failed Typing in %s at statement %s: Is not cast compatible: %s <-- %s",
          jb.getMethod().getSignature(), stmt, useType, t));
      this.fail = true;
    } else if (!checkOnly && op instanceof Local && (t instanceof Integer1Type || t instanceof Integer127Type
        || t instanceof Integer32767Type || t instanceof WeakObjectType)) {
      Local v = (Local) op;
      if (!TypeResolver.typesEqual(t, useType)) {
        Type t_ = this.promote(t, useType);
        if (!TypeResolver.typesEqual(t, t_)) {
          this.tg.set(v, t_);
          this.typingChanged = true;
        }
      }
    }

    return op;
  }

  @Override
  public boolean finish() {
    return this.typingChanged || this.fail;
  }
}