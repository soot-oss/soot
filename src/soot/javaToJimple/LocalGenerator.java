package soot.javaToJimple;

import soot.*;
import java.util.*;

public class LocalGenerator{

    private soot.Body body;
    public LocalGenerator(Body b){
          body = b;
    }
    
    private boolean bodyContainsLocal(String name){
        Iterator it = body.getLocals().iterator();
        while (it.hasNext()){
            if (((soot.Local)it.next()).getName().equals(name)) return true;
        }
        return false;
    }
    
    /**
     * generates a new soot local given the type
     */
    public soot.Local generateLocal(soot.Type type){
        
		String name = "v";
		if (type instanceof soot.IntType) {
            while (true){
			    name = nextIntName();
                if (!bodyContainsLocal(name)) break;
            }
		}
        else if (type instanceof soot.ByteType) {
            while (true){
			    name = nextByteName();
                if (!bodyContainsLocal(name)) break;
            }
		}
        else if (type instanceof soot.ShortType) {
            while (true){
			    name = nextShortName();
                if (!bodyContainsLocal(name)) break;
            }
		}
        else if (type instanceof soot.BooleanType) {
            while (true){
			    name = nextBooleanName();
                if (!bodyContainsLocal(name)) break;
            }
		}
        else if (type instanceof soot.VoidType) {
            while (true){
			    name = nextVoidName();
                if (!bodyContainsLocal(name)) break;
            }
		}
        else if (type instanceof soot.CharType) {
            while (true){
                name = nextIntName();
                if (!bodyContainsLocal(name)) break;
            }
            type = soot.CharType.v();
        }
		else if (type instanceof soot.DoubleType) {
            while (true){
			    name = nextDoubleName();
                if (!bodyContainsLocal(name)) break;
            }
		}
		else if (type instanceof soot.FloatType) {
            while (true){
			    name = nextFloatName();
                if (!bodyContainsLocal(name)) break;
            }
		}
		else if (type instanceof soot.LongType) {
            while (true){
			    name = nextLongName();
                if (!bodyContainsLocal(name)) break;
            }
		}
        else if (type instanceof soot.RefLikeType) {
            while (true){
                name = nextRefLikeTypeName();
                if (!bodyContainsLocal(name)) break;
            }
        }
        else {
            throw new RuntimeException("Unhandled Type of Local variable to Generate - Not Implemented");
        }
		
		return createLocal(name, type);
		
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
    
    // this should be used for generated locals only
    private soot.Local createLocal(String name, soot.Type sootType) {
        if (sootType instanceof soot.CharType) {
            sootType = soot.IntType.v();
        }
        soot.Local sootLocal = soot.jimple.Jimple.v().newLocal(name, sootType);
        body.getLocals().add(sootLocal);
		return sootLocal;
	}
}
