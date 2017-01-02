package soot.dexpler;

import java.util.Iterator;

import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;

import soot.Modifier;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootResolver;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;
import soot.tagkit.InnerClassAttribute;
import soot.tagkit.InnerClassTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.Tag;

/**
 * Class for loading methods from dex files 
 */
public class DexClassLoader {
    
	/**
	 * Loads a single method from a dex file
	 * @param dexFile The dex file from which to load the method
	 * @param method The method to load
	 * @param declaringClass The class that declares the method to load
	 * @param annotations The worker object for handling annotations
	 */
    private void loadMethod(DexFile dexFile, Method method,
			SootClass declaringClass, DexAnnotation annotations) {
        SootMethod sm = DexMethod.makeSootMethod(dexFile, method, declaringClass);
        if (declaringClass.declaresMethod(sm.getName(), sm.getParameterTypes(), sm.getReturnType()))
        	return;
       	declaringClass.addMethod(sm);
        annotations.handleMethodAnnotation(sm, method);	
    }

    public Dependencies makeSootClass(SootClass sc, ClassDef defItem, DexFile dexFile) {
        String superClass = defItem.getSuperclass();
        Dependencies deps = new Dependencies();

        // source file
        String sourceFile = defItem.getSourceFile();
        if (sourceFile != null) {
            sc.addTag(new SourceFileTag(sourceFile));
        }

        // super class for hierarchy level
        if (superClass != null) {
	        String superClassName = Util.dottedClassName(superClass);
	        SootClass sootSuperClass = SootResolver.v().makeClassRef(superClassName);
	        sc.setSuperclass(sootSuperClass);
	        deps.typesToHierarchy.add(sootSuperClass.getType());
        }

        // access flags
        int accessFlags = defItem.getAccessFlags();
        sc.setModifiers(accessFlags);

        // Retrieve interface names
        if (defItem.getInterfaces() != null) {
            for (String interfaceName : defItem.getInterfaces()) {
                String interfaceClassName = Util.dottedClassName(interfaceName);
                if (sc.implementsInterface(interfaceClassName))
                    continue;
                
                SootClass interfaceClass = SootResolver.v().makeClassRef(interfaceClassName);
                interfaceClass.setModifiers(interfaceClass.getModifiers() | Modifier.INTERFACE);
                sc.addInterface(interfaceClass);
                deps.typesToHierarchy.add(interfaceClass.getType());
            }
        }
        
        if (Options.v().oaat() && sc.resolvingLevel() <= SootClass.HIERARCHY) {
            return deps;
        }
        DexAnnotation da = new DexAnnotation(sc, deps);
        
        // get the fields of the class
        for (Field sf : defItem.getStaticFields()) {
        	if (sc.declaresField(sf.getName(), DexType.toSoot(sf.getType())))
        		continue;
        	SootField sootField = DexField.makeSootField(sf);
        	sc.addField(sootField);
        	da.handleFieldAnnotation(sootField, sf);
        }
        for (Field f: defItem.getInstanceFields()) {
        	if (sc.declaresField(f.getName(), DexType.toSoot(f.getType())))
        		continue;
        	SootField sootField = DexField.makeSootField(f);
        	sc.addField(sootField);
        	da.handleFieldAnnotation(sootField, f);
        }
        
        // get the methods of the class
        for (Method method : defItem.getDirectMethods()) {
        	loadMethod(dexFile, method, sc, da);
        }
        for (Method method : defItem.getVirtualMethods()) {
        	loadMethod(dexFile, method, sc, da);
        }
        
        da.handleClassAnnotation(defItem);
        
        // In contrast to Java, Dalvik associates the InnerClassAttribute
        // with the inner class, not the outer one. We need to copy the
        // tags over to correspond to the Soot semantics.
        InnerClassAttribute ica = (InnerClassAttribute) sc.getTag("InnerClassAttribute");
        if (ica != null) {
        	Iterator<InnerClassTag> innerTagIt = ica.getSpecs().iterator();
        	while (innerTagIt.hasNext()) {
        		Tag t = innerTagIt.next();
        		if (t instanceof InnerClassTag) {
        			InnerClassTag ict = (InnerClassTag) t;
        			
        			// Get the outer class name
        			String outer = DexInnerClassParser.getOuterClassNameFromTag(ict);
        			if (outer == null) {
						// If we don't have any clue what the outer class is, we just remove
						// the reference entirely
						innerTagIt.remove();
						continue;
        			}
        			
        			// If the tag is already associated with the outer class,
        			// we leave it as it is
        			if (outer.equals(sc.getName()))
        				continue;

        			// Check the inner class to make sure that this tag actually
        			// refers to the current class as the inner class
        			String inner = ict.getInnerClass().replaceAll("/", ".");
        			if (!inner.equals(sc.getName())) {
						innerTagIt.remove();
        				continue;
        			}
        			
        			SootClass osc = SootResolver.v().makeClassRef(outer);
        			if (osc == sc) {
        				if (!sc.hasOuterClass())
        					continue;
        				osc = sc.getOuterClass();
        			}
        			else
        				deps.typesToHierarchy.add(osc.getType());
        			
        			// Get the InnerClassAttribute of the outer class
        			InnerClassAttribute icat = (InnerClassAttribute)osc.getTag("InnerClassAttribute");
        			if (icat == null) {
        				icat = new InnerClassAttribute();
        				osc.addTag(icat);
        			}
        			
        			// Transfer the tag from the inner class to the outer class
        			InnerClassTag newt = new InnerClassTag(ict.getInnerClass(), ict.getOuterClass(),
        					ict.getShortName(), ict.getAccessFlags());
        			icat.add(newt);
        			
        			// Remove the tag from the inner class as inner classes do
        			// not have these tags in the Java / Soot semantics. The
        			// DexPrinter will copy it back if we do dex->dex.
					innerTagIt.remove();

					// Add the InnerClassTag to the inner class. This tag will be put in an InnerClassAttribute 
					// within the PackManager in method handleInnerClasses().
					if (!sc.hasTag("InnerClassTag")) {
						if (((InnerClassTag) t).getInnerClass().replaceAll("/", ".").equals(sc.toString())) {
							sc.addTag(t);
						}
					}
        		}
        	}
			// remove tag if empty
			if (ica.getSpecs().isEmpty()) {
				sc.getTags().remove(ica);
			}
        }
        
        return deps;
    }

}
