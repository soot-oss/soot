package soot.sootify;

import soot.AnySubType;
import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.ErroneousType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.NullType;
import soot.RefType;
import soot.ShortType;
import soot.StmtAddressType;
import soot.Type;
import soot.TypeSwitch;
import soot.UnknownType;
import soot.VoidType;
import soot.jimple.Jimple;

public class TypeTemplatePrinter extends TypeSwitch {

	private String varName;
	private final TemplatePrinter p;
	
	public TypeTemplatePrinter(TemplatePrinter p) {
		this.p = p;
	}

	public void setVariableName(String name) {
		this.varName = name;		
	}
	
	public void caseAnySubType(AnySubType t) {
		throw new IllegalArgumentException("cannot print this type");
	}

	public void caseArrayType(ArrayType t) {
		String oldName = varName;
		
		Type baseType = t.getElementType();
		setVariableName("baseType");
		baseType.apply(this);
		
		int numDimensions = t.numDimensions;
		p.println("int numDimensions=" + numDimensions+ ";");
		
		p.println("Type "+ oldName +" = ArrayType.v(baseType, numDimensions);");
		varName = oldName;
	}

	public void caseBooleanType(BooleanType t) {
		p.println("Type "+varName+" = BooleanType.v();");	
	}

	public void caseByteType(ByteType t) {
		
		p.println("Type "+varName+" = ByteType.v();");	
		
	}

	public void caseCharType(CharType t) {
		p.println("Type "+varName+" = CharType.v();");
		
	}

	public void caseDefault(Type t) {
		throw new IllegalArgumentException("cannot print this type");		
	}

	public void caseDoubleType(DoubleType t) {
		p.println("Type "+varName+" = DoubleType.v();");
		
	}

	public void caseErroneousType(ErroneousType t) {
		throw new IllegalArgumentException("cannot print this type");		
	}

	public void caseFloatType(FloatType t) {
		
		p.println("Type "+varName+" = FloatType.v();");
		
	}

	public void caseIntType(IntType t) {
	
		p.println("Type "+varName+" = IntType.v();");
		
	}

	public void caseLongType(LongType t) {
		p.println("Type "+varName+" = LongType.v();");
		
	}

	public void caseNullType(NullType t) {
	
		p.println("Type "+varName+" = NullType.v();");
		
	}

	public void caseRefType(RefType t) {
		
		p.println("Type "+varName+" = RefType.v("+t.getClassName()+");;");
		
	}

	public void caseShortType(ShortType t) {
		p.println("Type "+varName+" = ShortType.v();");
		
	}

	public void caseStmtAddressType(StmtAddressType t) {
		throw new IllegalArgumentException("cannot print this type");		
	}

	public void caseUnknownType(UnknownType t) {
		throw new IllegalArgumentException("cannot print this type");		
	}

	public void caseVoidType(VoidType t) {
		p.println("Type "+varName+" = VoidType.v();");
		
	}

}
