package soot.javaToJimple;

import java.util.*;
public class PrivateMethodAccMethodSource implements soot.MethodSource {

    private polyglot.types.MethodInstance methodInst;

    public void setMethodInst(polyglot.types.MethodInstance mi) {
        methodInst = mi;
    }
    
    private boolean isCallParamType(soot.Type sootType) {
        Iterator it = methodInst.formalTypes().iterator();
        while (it.hasNext()) {
            soot.Type compareType = Util.getSootType((polyglot.types.Type)it.next());
            if (compareType.equals(sootType)) return true;
        }
        return false;
    }
    
    public soot.Body getBody(soot.SootMethod sootMethod, String phaseName){
            
        soot.Body body = soot.jimple.Jimple.v().newBody(sootMethod);
        
        soot.Local base = null;
        ArrayList methParams = new ArrayList();
        ArrayList methParamsTypes = new ArrayList();
        // create parameters
        Iterator paramIt = sootMethod.getParameterTypes().iterator();
        int paramCounter = 0;
        while (paramIt.hasNext()) {
            soot.Type sootType = (soot.Type)paramIt.next();
            soot.Local paramLocal = generateLocal(sootType);
            body.getLocals().add(paramLocal);
            soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(sootType, paramCounter);
            soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(paramLocal, paramRef);
            body.getUnits().add(stmt);
            if (!isCallParamType(sootType)){
                base = paramLocal;
            }
            else {
                methParams.add(paramLocal);
                methParamsTypes.add(paramLocal.getType());
            }
            paramCounter++;
        }
        
        // create return type local
        soot.Type type = Util.getSootType(methodInst.returnType());
        
        soot.Local returnLocal = null;
        if (!(type instanceof soot.VoidType)){
            returnLocal = generateLocal(type);
            body.getLocals().add(returnLocal);
        }

        // assign local to meth
        soot.SootMethod meth = sootMethod.getDeclaringClass().getMethod(methodInst.name(), methParamsTypes, Util.getSootType(methodInst.returnType()));

        soot.jimple.InvokeExpr invoke = null;
        if (methodInst.flags().isStatic()) {
            invoke = soot.jimple.Jimple.v().newStaticInvokeExpr(meth, methParams);
        }
        else {
            invoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(base, meth, methParams);
        }

        soot.jimple.Stmt stmt = null;
        if (!(type instanceof soot.VoidType)){
            stmt = soot.jimple.Jimple.v().newAssignStmt(returnLocal, invoke);
        }
        else{
            stmt = soot.jimple.Jimple.v().newInvokeStmt(invoke);
        }
        body.getUnits().add(stmt);

        //return local
        soot.jimple.Stmt retStmt = null;
        if (!(type instanceof soot.VoidType)) {
            retStmt = soot.jimple.Jimple.v().newReturnStmt(returnLocal);
        }
        else {
            retStmt = soot.jimple.Jimple.v().newReturnVoidStmt();
        }
        body.getUnits().add(retStmt);
        
        return body;
     
    }
    
    private soot.Local generateLocal(soot.Type type){
        
		String name = "v";
		if (type instanceof soot.IntType) {
			name = nextIntName();
		}
        else if (type instanceof soot.ByteType) {
			name = nextByteName();
		}
        else if (type instanceof soot.ShortType) {
			name = nextShortName();
		}
        else if (type instanceof soot.BooleanType) {
			name = nextBooleanName();
		}
        else if (type instanceof soot.VoidType) {
			name = nextVoidName();
		}
        else if (type instanceof soot.CharType) {
            name = nextIntName();
            type = soot.IntType.v();
        }
		else if (type instanceof soot.DoubleType) {
			name = nextDoubleName();
		}
		else if (type instanceof soot.FloatType) {
			name = nextFloatName();
		}
		else if (type instanceof soot.LongType) {
			name = nextLongName();
		}
        else if (type instanceof soot.RefLikeType) {
            name = nextRefLikeTypeName();
        }
        else {
            //System.out.println("Unhandled Type of local to generate: "+type);
            throw new RuntimeException("Unhandled Type of Local variable to Generate - Not Implemented");
        }
		
		return soot.jimple.Jimple.v().newLocal(name, type);
		
	}

	private int tempInt = -1;
	private int tempVoid = -1;
	private int tempBoolean = -1;
	private int tempLong = -1;
	private int tempDouble = -1;
	private int tempFloat = -1;
    private int tempRefLikeType = -1;
    private int tempByte = -1;
    private int tempShort = -1;
	
    private String nextIntName(){
		tempInt++;
		return "$i"+tempInt;
	}

	private String nextVoidName(){
		tempVoid++;
		return "$v"+tempVoid;
	}

	private String nextByteName(){
		tempByte++;
		return "$b"+tempByte;
	}

	private String nextShortName(){
		tempShort++;
		return "$s"+tempShort;
	}

	private String nextBooleanName(){
		tempBoolean++;
		return "$z"+tempBoolean;
	}

	private String nextDoubleName(){
		tempDouble++;
		return "$d"+tempDouble;
	}
    
	private String nextFloatName(){
		tempFloat++;
		return "$f"+tempFloat;
	}

	private String nextLongName(){
		tempLong++;
		return "$l"+tempLong;
	}

    private String nextRefLikeTypeName(){
        tempRefLikeType++;
        return "$r"+tempRefLikeType;
    }

}
