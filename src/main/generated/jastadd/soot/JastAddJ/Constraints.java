package soot.JastAddJ;

import java.util.HashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;
/**
  * @ast class
 * 
 */
public class Constraints extends java.lang.Object {

    static class ConstraintSet {
      public Collection supertypeConstraints = new HashSet(4);
      public Collection subtypeConstraints = new HashSet(4);
      public Collection equaltypeConstraints = new HashSet(4);
      public TypeDecl typeArgument;
    }


    private Collection typeVariables;


    private Map constraintsMap;


    public boolean rawAccess = false;



    public Constraints() {
      typeVariables = new ArrayList(4);
      constraintsMap = new HashMap();
    }



    public void addTypeVariable(TypeVariable T) {
      if(!typeVariables.contains(T)) {
        typeVariables.add(T);
        constraintsMap.put(T, new ConstraintSet());
      }
    }



    public boolean unresolvedTypeArguments() {
      for(Iterator iter = typeVariables.iterator(); iter.hasNext(); ) {
        TypeVariable T = (TypeVariable)iter.next();
        ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
        if(set.typeArgument == null)
          return true;
      }
      return false;
    }



    public void printConstraints() {
      System.err.println("Current constraints:");
      for(Iterator iter = typeVariables.iterator(); iter.hasNext(); ) {
        TypeVariable T = (TypeVariable)iter.next();
        ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
        for(Iterator i2 = set.supertypeConstraints.iterator(); i2.hasNext(); ) {
          TypeDecl U = (TypeDecl)i2.next();
          System.err.println("  " + T.fullName() + " :> " + U.fullName());
        }
        for(Iterator i2 = set.subtypeConstraints.iterator(); i2.hasNext(); ) {
          TypeDecl U = (TypeDecl)i2.next();
          System.err.println("  " + T.fullName() + " <: " + U.fullName());
        }
        for(Iterator i2 = set.equaltypeConstraints.iterator(); i2.hasNext(); ) {
          TypeDecl U = (TypeDecl)i2.next();
          System.err.println("  " + T.fullName() + " = " + U.fullName());
        }
      }
    }



    
    public void resolveBounds() {
      for(Iterator iter = typeVariables.iterator(); iter.hasNext(); ) {
        TypeVariable T = (TypeVariable)iter.next();
        ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
        if(set.typeArgument == null) {
          //if(T.getNumTypeBound() == 1)
            set.typeArgument = T.getTypeBound(0).type();
          //else
          //  throw new Error("Not supported for multiple bounds yet");
        }
      }
    }



    public void resolveEqualityConstraints() {
      for(Iterator iter = typeVariables.iterator(); iter.hasNext(); ) {
        TypeVariable T = (TypeVariable)iter.next();
        ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
        boolean done = false;
        for(Iterator i2 = set.equaltypeConstraints.iterator(); !done && i2.hasNext(); ) {
          TypeDecl U = (TypeDecl)i2.next();
          if(!typeVariables.contains(U)) {
            replaceEqualityConstraints(T, U);   // replace equality constraints for other type variables
            set.equaltypeConstraints.clear();
            set.equaltypeConstraints.add(U);    // make U is the only equality constraint for T
            set.typeArgument = U;
            done = true;                        // continue on next type variable
          }
          else if(T == U) {
            //i2.remove();                        // discard constraint
          }
          else {
            replaceAllConstraints(T, U);         // rewrite all constraints involving T to use U instead
            done = true;                        // continue on next type variable
          }
        }
        if(set.typeArgument == null && set.equaltypeConstraints.size() == 1 && set.equaltypeConstraints.contains(T))
        	set.typeArgument = T;
      }
    }



    public void replaceEqualityConstraints(TypeDecl before, TypeDecl after) {
      for(Iterator iter = typeVariables.iterator(); iter.hasNext(); ) {
        TypeVariable T = (TypeVariable)iter.next();
        ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
        replaceConstraints(set.equaltypeConstraints, before, after);
      }
    }


    
    public void replaceAllConstraints(TypeDecl before, TypeDecl after) {
      for(Iterator iter = typeVariables.iterator(); iter.hasNext(); ) {
        TypeVariable T = (TypeVariable)iter.next();
        ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
        replaceConstraints(set.supertypeConstraints, before, after);
        replaceConstraints(set.subtypeConstraints, before, after);
        replaceConstraints(set.equaltypeConstraints, before, after);
      }
    }


    
    private void replaceConstraints(Collection constraints, TypeDecl before, TypeDecl after) {
      Collection newConstraints = new ArrayList();
      for(Iterator i2 = constraints.iterator(); i2.hasNext(); ) {
        TypeDecl U = (TypeDecl)i2.next();
        if(U == before) { //  TODO: fix parameterized type
          i2.remove();
          newConstraints.add(after);
        }
      }
      constraints.addAll(newConstraints);
    }



    public void resolveSubtypeConstraints() {
      for(Iterator iter = typeVariables.iterator(); iter.hasNext(); ) {
        TypeVariable T = (TypeVariable)iter.next();
        ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
        if((!set.subtypeConstraints.isEmpty() || T.getNumTypeBound() > 0) && set.typeArgument == null) {
          ArrayList bounds = new ArrayList();
          for(Iterator i2 = set.subtypeConstraints.iterator(); i2.hasNext(); ) {
            bounds.add(i2.next());
          }
          for(int i = 0; i < T.getNumTypeBound(); i++) {
            bounds.add(T.getTypeBound(i).type());
          }
          set.typeArgument = GLBTypeFactory.glb(bounds);
        }
      }
    }


    
    public void resolveSupertypeConstraints() {
      for(Iterator iter = typeVariables.iterator(); iter.hasNext(); ) {
        TypeVariable T = (TypeVariable)iter.next();
        ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
        if(!set.supertypeConstraints.isEmpty() && set.typeArgument == null) {
          //TypeDecl typeDecl = lub(set.supertypeConstraints);
          TypeDecl typeDecl = T.lookupLUBType(set.supertypeConstraints).lub();
          set.typeArgument = typeDecl;
          /*
          TypeDecl EC = (TypeDecl)set.supertypeConstraints.get(0);
          for(Iterator i2 = set.supertypeConstraints.iterator(); i2.hasNext(); ) {
            TypeDecl U = (TypeDecl)i2.next();
            TypeDecl ST = U;
            TypeDecl EST = ST.erasure();
            EC = intersect(EC, EST);
          }
          TypeDecl MEC = EC;
          //System.err.println(" MEC(" + T.fullName() + ") = " + MEC.fullName());
          set.typeArgument = MEC;
          */
        }
      }
    }


    
    /*
    // operates only on erased types. does it matter? (no type variables, no partypedecl)
    private TypeDecl intersect(TypeDecl t1, TypeDecl t2) {
      if(t1.instanceOf(t2))
        return t1;
      else if(t2.instanceOf(t1))
        return t2;
      else {
        HashSet set = new HashSet();
        for(Iterator iter = directSupertypes(t1).iterator(); iter.hasNext(); ) {
          TypeDecl t1Super = (TypeDecl)iter.next();
          set.add(intersect(t1Super, t2));
        }
        if(set.isEmpty())
          throw new Error("Empty intersection of " + t1.fullName() + " and " + t2.fullName());
        TypeDecl lowestType = (TypeDecl)set.iterator().next();
        for(Iterator iter = set.iterator(); iter.hasNext(); ) {
          TypeDecl type = (TypeDecl)iter.next();
          if(type.instanceOf(lowestType))
            lowestType = type;
          else if(!lowestType.instanceOf(type))
            throw new Error("Several leaf types in intersection, " + lowestType.fullName() + " and " + type.fullName());
        }
        return lowestType;
      }
    }
    */

    public static HashSet directSupertypes(TypeDecl t) {
      if(t instanceof ClassDecl) {
        ClassDecl type = (ClassDecl)t;
        HashSet set = new HashSet();
        if(type.hasSuperclass())
          set.add(type.superclass());
        for(int i = 0; i < type.getNumImplements(); i++)
          set.add(type.getImplements(i).type());
        return set;
      }
      else if(t instanceof InterfaceDecl) {
        InterfaceDecl type = (InterfaceDecl)t;
        HashSet set = new HashSet();
        for(int i = 0; i < type.getNumSuperInterfaceId(); i++)
          set.add(type.getSuperInterfaceId(i).type());
        return set;
      }
      else if(t instanceof TypeVariable) {
        TypeVariable type = (TypeVariable)t;
        HashSet set = new HashSet();
        for(int i = 0; i < type.getNumTypeBound(); i++)
          set.add(type.getTypeBound(i).type());
        return set;
      }
      else
        throw new Error("Operation not supported for " + t.fullName() + ", " + t.getClass().getName());
    }



    public static HashSet parameterizedSupertypes(TypeDecl t) {
      HashSet result = new HashSet();
      addParameterizedSupertypes(t, new HashSet(), result);
      return result;
    }


    public static void addParameterizedSupertypes(TypeDecl t, HashSet processed, HashSet result) {
      if(!processed.contains(t)) {
        processed.add(t);
        if(t.isParameterizedType() /*&& !t.isRawType()*/)
          result.add(t);
        for(Iterator iter = directSupertypes(t).iterator(); iter.hasNext(); ) {
          TypeDecl typeDecl = (TypeDecl)iter.next();
          addParameterizedSupertypes(typeDecl, processed, result);
        }
      }
    }



    public Collection typeArguments() {
      ArrayList list = new ArrayList(typeVariables.size());
      for(Iterator iter = typeVariables.iterator(); iter.hasNext(); ) {
        TypeVariable T = (TypeVariable)iter.next();
        ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
        list.add(set.typeArgument);
      }
      return list;
    }



    public void addSupertypeConstraint(TypeDecl T, TypeDecl A) {
      ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
      set.supertypeConstraints.add(A);
      //System.out.println(T.name() + " :> " + A.fullName());
    }


    public void addSubtypeConstraint(TypeDecl T, TypeDecl A) {
      ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
      set.subtypeConstraints.add(A);
      //System.out.println(T.name() + " <: " + A.fullName());
    }


    public void addEqualConstraint(TypeDecl T, TypeDecl A) {
      ConstraintSet set = (ConstraintSet)constraintsMap.get(T);
      set.equaltypeConstraints.add(A);
      //System.out.println(T.name() + " = " + A.fullName());
    }


    
    public void convertibleTo(TypeDecl A, TypeDecl F) {
      //System.out.println("Convertible to called with " + A.fullName() + "(" + A.getClass().getName() + ")" + " and " + F.fullName() + "(" + F.getClass().getName() + ")");
      // If F does not involve a type parameter Tj then con constraint is implied on Tj
      if(!F.involvesTypeParameters())
        return;
      // If A is the type of null, no constraint is implied on Tj.
      if(A.isNull())
        return;
      // If A is a primitive type, then A is converted to a reference type U via boxing conversion
      // and this algorithm is applied recursively to the constraint U << F.
      if(A.isUnboxedPrimitive()) {
        TypeDecl U = A.boxed();
        convertibleTo(U, F);
      }
      // If F = Tj, then the constrint Tj :> A is implied
      else if(F instanceof TypeVariable) {
        if(typeVariables.contains(F))
          addSupertypeConstraint(F, A);
      }
      // If F = U[], where the type U involves Tj, then if A is an array type V[]
      // or a type variable with an upper bound that is an array type V[],
      // where V is a reference type, this algorithm is applied recursively
      // to the constraint V << U
      else if(F.isArrayDecl()) {
        //System.out.println("convertibleTo array decl");
        TypeDecl U = ((ArrayDecl)F).componentType();
        if(!U.involvesTypeParameters())
          return;
        if(A.isArrayDecl()) {
          TypeDecl V = ((ArrayDecl)A).componentType();
          if(V.isReferenceType())
            convertibleTo(V, U);
        }
        else if(A.isTypeVariable()) {
          TypeVariable t = (TypeVariable)A;
          for(int i = 0; i < t.getNumTypeBound(); i++) {
            TypeDecl typeBound = t.getTypeBound(i).type();
            if(typeBound.isArrayDecl() && ((ArrayDecl)typeBound).componentType().isReferenceType()) {
              TypeDecl V = ((ArrayDecl)typeBound).componentType();
              convertibleTo(V, U);
            }
          }
        }
      }
      else if(F instanceof ParTypeDecl && !F.isRawType()) {
        for(Iterator iter = parameterizedSupertypes(A).iterator(); iter.hasNext(); ) {
          ParTypeDecl PF = (ParTypeDecl)F;
          ParTypeDecl PA = (ParTypeDecl)iter.next();
          if(PF.genericDecl() == PA.genericDecl()) {
            if(A.isRawType())
              rawAccess = true;
            else
            for(int i = 0; i < PF.getNumArgument(); i++) {
              TypeDecl T = PF.getArgument(i).type();
              if(T.involvesTypeParameters()) {
                if(!T.isWildcard()) {
                  TypeDecl U = T;
                  TypeDecl V = PA.getArgument(i).type();
                  constraintEqual(V, U);
                }
                else if(T instanceof WildcardExtendsType) {
                  TypeDecl U = ((WildcardExtendsType)T).getAccess().type();
                  TypeDecl S = PA.getArgument(i).type();
                  if(!S.isWildcard()) {
                    TypeDecl V = S;
                    convertibleTo(V, U);
                  }
                  else if(S instanceof WildcardExtendsType) {
                    TypeDecl V = ((WildcardExtendsType)S).getAccess().type();
                    convertibleTo(V, U);
                  }
                }
                else if(T instanceof WildcardSuperType) {
                  TypeDecl U = ((WildcardSuperType)T).getAccess().type();
                  TypeDecl S = PA.getArgument(i).type();
                  if(!S.isWildcard()) {
                    TypeDecl V = S;
                    convertibleFrom(V, U);
                  }
                  else if(S instanceof WildcardSuperType) {
                    TypeDecl V = ((WildcardSuperType)S).getAccess().type();
                    convertibleFrom(V, U);
                  }
                }
              }
            }
          }
        }
      }
    }




    public void convertibleFrom(TypeDecl A, TypeDecl F) {
      //System.out.println("ConvertibleFrom called with " + A.fullName() + "(" + A.getClass().getName() + ")" + " and " + F.fullName() + "(" + F.getClass().getName() + ")");
      // If F does not involve a type parameter Tj then con constraint is implied on Tj
      if(!F.involvesTypeParameters())
        return;
      // If A is the type of null, no constraint is implied on Tj.
      else if(A.isNull())
        return;
      else if(F instanceof TypeVariable) {
        if(typeVariables.contains(F))
          addSubtypeConstraint(F, A);
      }
      else if(F.isArrayDecl()) {
        TypeDecl U = ((ArrayDecl)F).componentType();
        if(A.isArrayDecl()) {
          TypeDecl V = ((ArrayDecl)A).componentType();
          convertibleFrom(V, U);
        }
        else if(A.isTypeVariable()) {
          TypeVariable t = (TypeVariable)A;
          for(int i = 0; i < t.getNumTypeBound(); i++) {
            TypeDecl typeBound = t.getTypeBound(i).type();
            if(typeBound.isArrayDecl() && ((ArrayDecl)typeBound).componentType().isReferenceType()) {
              TypeDecl V = ((ArrayDecl)typeBound).componentType();
              convertibleFrom(V, U);
            }
          }
        }
      }
      else if(F instanceof ParTypeDecl && !F.isRawType() && A instanceof ParTypeDecl && !A.isRawType()) {
        ParTypeDecl PF = (ParTypeDecl)F;
        ParTypeDecl PA = (ParTypeDecl)A;
        TypeDecl G = PF.genericDecl();
        TypeDecl H = PA.genericDecl();
        for(int i = 0; i < PF.getNumArgument(); i++) {
          TypeDecl T = PF.getArgument(i).type();
          if(T.involvesTypeParameters()) {
            // If F has the form G<...,U,...> where U is a type expression that involves Tj
            if(!T.isWildcard()) {
              TypeDecl U = T;
              if(G.instanceOf(H)) {
                if(H != G) {
                  for(Iterator iter = parameterizedSupertypes(F).iterator(); iter.hasNext(); ) {
                    TypeDecl V = (TypeDecl)iter.next();
                    if(!V.isRawType() && ((ParTypeDecl)V).genericDecl() == H) {
                      if(F.instanceOf(V))
                        convertibleFrom(A, V);
                    }
                  }
                }
                else if(PF.getNumArgument() == PA.getNumArgument()) {
                  TypeDecl X = PA.getArgument(i).type();
                  if(!X.isWildcard()) {
                    TypeDecl W = X;
                    constraintEqual(W, U);
                  }
                  else if(X instanceof WildcardExtendsType) {
                    TypeDecl W = ((WildcardExtendsType)X).getAccess().type();
                    convertibleFrom(W, U);
                  }
                  else if(X instanceof WildcardSuperType) {
                    TypeDecl W = ((WildcardSuperType)X).getAccess().type();
                    convertibleTo(W, U);
                  }
                }
              }
            }
            else if(T instanceof WildcardExtendsType) {
              TypeDecl U = ((WildcardExtendsType)T).getAccess().type();
              if(G.instanceOf(H)) {
                if(H != G) {
                  for(Iterator iter = parameterizedSupertypes(F).iterator(); iter.hasNext(); ) {
                    TypeDecl V = (TypeDecl)iter.next();
                    if(!V.isRawType() && ((ParTypeDecl)V).genericDecl() == H) {
                      // replace type argument Un with ? extends Un in V
                      ArrayList list = new ArrayList();
                      for(int j = 0; j < ((ParTypeDecl)V).getNumArgument(); j++)
                        list.add(((ParTypeDecl)V).getArgument(j).type().asWildcardExtends());
                      V = ((GenericTypeDecl)H).lookupParTypeDecl(list);
                      convertibleFrom(A, V);
                    }
                  }
                }
                else if(PF.getNumArgument() == PA.getNumArgument()) {
                  TypeDecl X = PA.getArgument(i).type();
                  if(X instanceof WildcardExtendsType) {
                    TypeDecl W = ((WildcardExtendsType)X).getAccess().type();
                    convertibleFrom(W, U);
                  }
                }
              }
            }
            else if(T instanceof WildcardSuperType) {
              TypeDecl U = ((WildcardSuperType)T).getAccess().type();
              if(G.instanceOf(H)) {
                if(H != G) {
                  for(Iterator iter = parameterizedSupertypes(F).iterator(); iter.hasNext(); ) {
                    TypeDecl V = (TypeDecl)iter.next();
                    if(!V.isRawType() && ((ParTypeDecl)V).genericDecl() == H) {
                      // replace type argument Un with ? super Un in V
                      ArrayList list = new ArrayList();
                      for(int j = 0; j < ((ParTypeDecl)V).getNumArgument(); j++)
                        list.add(((ParTypeDecl)V).getArgument(j).type().asWildcardExtends());
                      V = ((GenericTypeDecl)H).lookupParTypeDecl(list);
                      convertibleFrom(A, V);
                    }
                  }
                }
                else if(PF.getNumArgument() == PA.getNumArgument()) {
                  TypeDecl X = PA.getArgument(i).type();
                  if(X instanceof WildcardSuperType) {
                    TypeDecl W = ((WildcardSuperType)X).getAccess().type();
                    convertibleTo(W, U);
                  }
                }
              }
            }
          }
        }
      }
      else if(F.isRawType())
        rawAccess = true;
    }



    public void constraintEqual(TypeDecl A, TypeDecl F) {
      //System.out.println("ConstraintEqual called with " + A.fullName() + "(" + A.getClass().getName() + ")" + " and " + F.fullName() + "(" + F.getClass().getName() + ")");
      // If F does not involve a type parameter Tj then con constraint is implied on Tj
      if(!F.involvesTypeParameters())
        return;
      // If A is the type of null, no constraint is implied on Tj.
      else if(A.isNull())
        return;
      else if(F instanceof TypeVariable) {
        if(typeVariables.contains(F))
          addEqualConstraint(F, A);
      }
      else if(F.isArrayDecl()) {
        TypeDecl U = ((ArrayDecl)F).componentType();
        if(A.isArrayDecl()) {
          TypeDecl V = ((ArrayDecl)A).componentType();
          constraintEqual(V, U);
        }
        else if(A.isTypeVariable()) {
          TypeVariable t = (TypeVariable)A;
          for(int i = 0; i < t.getNumTypeBound(); i++) {
            TypeDecl typeBound = t.getTypeBound(i).type();
            if(typeBound.isArrayDecl() && ((ArrayDecl)typeBound).componentType().isReferenceType()) {
              TypeDecl V = ((ArrayDecl)typeBound).componentType();
              constraintEqual(V, U);
            }
          }
        }
      }
      else if(F instanceof ParTypeDecl && !F.isRawType()) {
        if(A instanceof ParTypeDecl) {
          ParTypeDecl PF = (ParTypeDecl)F;
          ParTypeDecl PA = (ParTypeDecl)A;
          if(PF.genericDecl() == PA.genericDecl()) {
            if(A.isRawType())
              rawAccess = true;
            else
            for(int i = 0; i < PF.getNumArgument(); i++) {
              TypeDecl T = PF.getArgument(i).type();
              if(T.involvesTypeParameters()) {
                if(!T.isWildcard()) {
                  TypeDecl U = T;
                  TypeDecl V = PA.getArgument(i).type();
                  constraintEqual(V, U);
                }
                else if(T instanceof WildcardExtendsType) {
                  TypeDecl U = ((WildcardExtendsType)T).getAccess().type();
                  TypeDecl S = PA.getArgument(i).type();
                  if(S instanceof WildcardExtendsType) {
                    TypeDecl V = ((WildcardExtendsType)S).getAccess().type();
                    constraintEqual(V, U);
                  }
                }
                else if(T instanceof WildcardSuperType) {
                  TypeDecl U = ((WildcardSuperType)T).getAccess().type();
                  TypeDecl S = PA.getArgument(i).type();
                  if(S instanceof WildcardSuperType) {
                    TypeDecl V = ((WildcardSuperType)S).getAccess().type();
                    constraintEqual(V, U);
                  }
                }
              }
            }
          }
        }
      }
    }


}
