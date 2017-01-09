package soot.cil.ast;

import java.util.ArrayList;
import java.util.List;

import soot.cil.Cil_Utils;
import soot.cil.ast.types.CilObjectTypeRef;


/**
 * Class for moedelling a class inside a CIL disassembly file
 * 
 * @author Steven Arzt
 *
 */
public class CilClass {
	private String className;
	private CilGenericDeclarationList generics;
	private boolean isInterface;
	private int accessModifiers;
	
	private CilObjectTypeRef superclass;
	private List<CilObjectTypeRef> interfaces = null;
	
	private List<CilField> fields = null;
	private List<CilEvent> events = null;
	private List<CilMethod> methods = null;
	private List<CilClass> innerClasses = null;
	private List<CilProperty> properties = null;
	
	public CilClass(String className,
			CilGenericDeclarationList generics,
			boolean isInterface,
			int accessModifiers) {
		this.className = Cil_Utils.removeGenericsDeclaration(className);
		this.generics = generics;
		this.isInterface = isInterface;
		this.accessModifiers = accessModifiers;
	}
	
	/**
	 * Gets the base class name, ignoring any generics
	 */
	public String getClassName() {
		return this.className;
	}
	
	/**
	 * Gets the unique name of the class including the number of generics
	 * @return The uniuque, mangled class name
	 */
	public String getUniqueClassName() {
		String mangledName = className;
		if (this.generics != null && !this.generics.isEmpty())
			 mangledName += "__" + this.generics.size();
		return mangledName;
	}
	
	public CilGenericDeclarationList getGenericParams() {
		return this.generics;
	}
	
	public boolean isInterface() {
		return this.isInterface;
	}
	
	public int getAccessModifiers() {
		return this.accessModifiers;
	}
	
	public void setSuperclass(CilObjectTypeRef superclass) {
		this.superclass = superclass;
	}
	
	public CilObjectTypeRef getSuperclass() {
		return this.superclass;
	}
	
	public List<CilObjectTypeRef> getInterfaces() {
		return this.interfaces;
	}
	
	public void addInterface(CilObjectTypeRef ifc) {
		if (this.interfaces == null)
			this.interfaces = new ArrayList<>();
		this.interfaces.add(ifc);
	}
	
	public List<CilField> getFields() {
		return this.fields;
	}
	
	public void addField(CilField fld) {
		if (this.fields == null)
			this.fields = new ArrayList<>();
		this.fields.add(fld);
	}
	
	public List<CilEvent> getEvents() {
		return this.events;
	}
	
	public void addEvent(CilEvent event) {
		if (this.events == null)
			this.events = new ArrayList<>();
		this.events.add(event);
	}
	
	public List<CilMethod> getMethods() {
		return this.methods;
	}
	
	public void addMethod(CilMethod method) {
		if (this.methods == null)
			this.methods = new ArrayList<>();
		this.methods.add(method);
	}
	
	public List<CilClass> getInnerClasses() {
		return this.innerClasses;
	}
	
	public void addInnerClass(CilClass clazz) {
		if (this.innerClasses == null)
			this.innerClasses = new ArrayList<>();
		this.innerClasses.add(clazz);
	}
	
	public List<CilProperty> getProperties() {
		return this.properties;
	}
	
	public void addProperty(CilProperty property) {
		if (this.properties == null)
			this.properties = new ArrayList<>();
		this.properties.add(property);
	}
	
	@Override
	public String toString() {
		return this.className;
	}
	
}
