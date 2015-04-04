package soot.util.annotations;

import soot.tagkit.AbstractAnnotationElemTypeSwitch;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationBooleanElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationDoubleElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationEnumElem;
import soot.tagkit.AnnotationFloatElem;
import soot.tagkit.AnnotationIntElem;
import soot.tagkit.AnnotationLongElem;
import soot.tagkit.AnnotationStringElem;

public class AnnotationElemSwitch extends AbstractAnnotationElemTypeSwitch{
	
	public class KeyValuePair<V>{
		
		private String name;
		private V value;
		


		public KeyValuePair(String name, V value) {
			this.name = name;
			this.value = value;
		}

		public String getKey() {
			return name;
		}

		public V getValue() {
			return value;
		}
	}

	@Override
	public void caseAnnotationAnnotationElem(AnnotationAnnotationElem v) {
		AnnotationInstanceCreator aic = new AnnotationInstanceCreator();
		
		Object result = aic.create(v.getValue());
		
		setResult(new KeyValuePair<Object>(v.getName(), result));
	}

	@Override
	public void caseAnnotationArrayElem(AnnotationArrayElem v) {
		
		
		Object[] result = new Object[v.getNumValues()];
		
		int i = 0;
		for (AnnotationElem elem : v.getValues()){
			AnnotationElemSwitch sw = new AnnotationElemSwitch();
			elem.apply(sw);	
			result[i] =  ((KeyValuePair<?>) sw.getResult()).getValue();
			
			i++;
		}
		
		setResult(new KeyValuePair<Object[]>(v.getName(), result));
		
	}

	@Override
	public void caseAnnotationBooleanElem(AnnotationBooleanElem v) {
		setResult(new KeyValuePair<Boolean>(v.getName(), v.getValue()));
		
	}

	@Override
	public void caseAnnotationClassElem(AnnotationClassElem v) {
		ClassLoader cl = this.getClass().getClassLoader();
		try {
			Class<?> clazz = cl.loadClass(toQuallifiedClassName(v.getDesc()));
			setResult(new KeyValuePair<Class<?>>(v.getName(), clazz));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load class: " + v.getDesc());
		}
		
	}

	@Override
	public void caseAnnotationDoubleElem(AnnotationDoubleElem v) {
		setResult(new KeyValuePair<Double>(v.getName(), v.getValue()));
	}

	@Override
	public void caseAnnotationEnumElem(AnnotationEnumElem v) {
		ClassLoader cl = this.getClass().getClassLoader();
		try {
			Class<?> clazz = cl.loadClass(toQuallifiedClassName(v.getTypeName()));
			
			Enum<?> result = null;
			for (Object o : clazz.getEnumConstants()) {
				try {
					Enum<?> t = (Enum<?>) o;
					if (t.name().equals(v.getConstantName())) {
						result = t;
						break;
					}
				} catch (ClassCastException e) {
					throw new RuntimeException("Class " + v.getTypeName() + " is no Enum");
				}
			}
			
			if (result == null) {
				throw new RuntimeException(v.getConstantName() + " is not a EnumConstant of " + v.getTypeName());
			}
			
			setResult(new KeyValuePair<Enum<?>>(v.getName(), result));
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load class: " + v.getTypeName());
		}	
		
	}

	@Override
	public void caseAnnotationFloatElem(AnnotationFloatElem v) {
		setResult(new KeyValuePair<Float>(v.getName(), v.getValue()));
	}

	@Override
	public void caseAnnotationIntElem(AnnotationIntElem v) {
		setResult(new KeyValuePair<Integer>(v.getName(), v.getValue()));
	}

	@Override
	public void caseAnnotationLongElem(AnnotationLongElem v) {
		setResult(new KeyValuePair<Long>(v.getName(), v.getValue()));
	}

	@Override
	public void caseAnnotationStringElem(AnnotationStringElem v) {
		setResult(new KeyValuePair<String>(v.getName(), v.getValue()));
	}

	@Override
	public void defaultCase(Object object) {
		throw new RuntimeException("Unexpected AnnotationElem");
	}

	
	
	public static String toQuallifiedClassName(String name) {
		name = name.substring(1, name.length() - 1);
		name = name.replace('/', '.');
		return name;
	}
}
